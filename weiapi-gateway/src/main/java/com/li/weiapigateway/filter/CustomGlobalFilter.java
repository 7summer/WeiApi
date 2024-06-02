package com.li.weiapigateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.li.weiapicommon.dubbo.InnerIdentityService;
import com.li.weiapicommon.dubbo.InnerInterfaceInfoService;
import com.li.weiapicommon.dubbo.InnerUserInterfaceService;
import com.li.weiapicommon.model.InnerIdentify;
import com.li.weiapicommon.model.InnerInterfaceInfo;
import com.li.weiapigateway.utils.HeaderUtils;
import com.sun.deploy.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.json.JsonUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CustomGlobalFilter implements GlobalFilter, Ordered {

	private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

	@DubboReference
	private InnerIdentityService identityService;

	@DubboReference
	private InnerInterfaceInfoService interfaceInfoService;

	@DubboReference
	private InnerUserInterfaceService userInterfaceService;

	private static final String PREFIXURL = "http://localhost:8085";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();
		HttpHeaders headers = request.getHeaders();

		String accessKey = headers.getFirst("accessKey");
		InnerIdentify innerIdentify = identityService.getIdentityByUserId(accessKey);
		if (innerIdentify.getId() == null) {
			return handleNoAuth(response);
		}
		// 统一鉴权
		if (!HeaderUtils.validateHeader(headers, innerIdentify.getSecretKey())) {
			return handleNoAuth(response);
		}

		// 验证接口存在
		String url = PREFIXURL + request.getPath().value();
		String method = request.getMethodValue();
		InnerInterfaceInfo innerInterfaceInfo = interfaceInfoService.getInterfaceInfoByPathAndMethod(url, method);
		if (innerInterfaceInfo == null) {
			return handleNoAuth(response);
		}

		// 用户能调用接口
		Long userId = innerIdentify.getId();
		Long interfaceId = innerInterfaceInfo.getId();
		if (!userInterfaceService.canInvoke(userId, interfaceId)) {
			return handleNoAuth(response);
		}

		// todo 统一业务处理（接口调用统计属于这一类）
		// todo 日志
		MediaType mediaType = request.getHeaders().getContentType();
		if(MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)){
			return writeBodyLog(exchange, chain, userId, interfaceId);
		}else{
			return writeBasicLog(exchange, chain, userId, interfaceId);
		}
	}

	@Override
	public int getOrder() {
		return -1;
	}

	/**
	 * 无权限执行接口
	 * @param response
	 * @return
	 */
	private Mono<Void> handleNoAuth(ServerHttpResponse response) {
		Map<String, Object> result = new HashMap<>();
		result.put("code", 403);
		result.put("data", null);
		result.put("message", "调用接口失败");
		result.put("description", "调用接口失败");

		String jsonResult = JSON.toJSONString(result);

		byte[] bytes = jsonResult.getBytes(StandardCharsets.UTF_8);
		DataBuffer buffer = response.bufferFactory().wrap(bytes);

		return response.writeWith(Mono.just(buffer));
	}

	/**
	 * 打印日志
	 * @param exchange
	 * @param chain
	 * @return
	 */
	private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, Long userId, Long interfaceId) {
		StringBuilder builder = new StringBuilder();
		MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
		for (Map.Entry<String, List<String>> entry : queryParams.entrySet())
			builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ","));

		//获取响应体
		ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange);

		return chain.filter(exchange.mutate().response(decoratedResponse).build())
				.then(Mono.fromRunnable(() -> {
					// 打印日志
					log.info("get请求:" + builder.toString());
					userInterfaceService.updateCount(userId, interfaceId);
				}));
	}

	/**
	 * 解决 request body 只能读取一次问题，
	 * 参考: org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
	 * @param exchange
	 * @param chain
	 * @return
	 */
	private Mono<Void> writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, Long userId, Long interfaceId) {
		ServerRequest serverRequest = ServerRequest.create(exchange,messageReaders);

		StringBuilder builder = new StringBuilder();

		Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
				.flatMap(body ->{
					builder.append(body);
					return Mono.just(body);
				});

		// 通过 BodyInserter 插入 body(支持修改body), 避免 request body 只能获取一次
		BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(exchange.getRequest().getHeaders());
		// the new content type will be computed by bodyInserter
		// and then set in the request decorator
		headers.remove(HttpHeaders.CONTENT_LENGTH);

		CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);

		return bodyInserter.insert(outputMessage,new BodyInserterContext())
				.then(Mono.defer(() -> {
					// 重新封装请求
					ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);

					// 记录响应日志
					ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange);

					// 记录普通的
					return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
							.then(Mono.fromRunnable(() -> {
								// 打印日志
								log.info("post请求:" + builder.toString());
								userInterfaceService.updateCount(userId, interfaceId);
							}));
				}));
	}

	/**
	 * 记录响应日志
	 * 通过 DataBufferFactory 解决响应体分段传输问题。
	 */
	private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		DataBufferFactory bufferFactory = response.bufferFactory();

		return new ServerHttpResponseDecorator(response) {
			@Override
			public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
				if (body instanceof Flux) {
					// 获取响应类型，如果是 json 就打印
					String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);

					Flux<? extends DataBuffer> fluxBody = Flux.from(body);
					return super.writeWith(fluxBody.buffer().map(dataBuffers -> {

						// 合并多个流集合，解决返回体分段传输
						DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
						DataBuffer join = dataBufferFactory.join(dataBuffers);
						byte[] content = new byte[join.readableByteCount()];
						join.read(content);

						// 释放掉内存
						DataBufferUtils.release(join);
						String responseResult = new String(content, StandardCharsets.UTF_8);

						return bufferFactory.wrap(content);
					}));

				}
				// if body is not a flux. never got there.
				return super.writeWith(body);
			}
		};
	}

	/**
	 * 请求装饰器，重新计算 headers
	 * @param exchange
	 * @param headers
	 * @param outputMessage
	 * @return
	 */
	private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers,
													   CachedBodyOutputMessage outputMessage) {
		return new ServerHttpRequestDecorator(exchange.getRequest()) {
			@Override
			public HttpHeaders getHeaders() {
				long contentLength = headers.getContentLength();
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.putAll(super.getHeaders());
				if (contentLength > 0) {
					httpHeaders.setContentLength(contentLength);
				} else {
					// TODO: this causes a 'HTTP/1.1 411 Length Required' // on
					// httpbin.org
					httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
				}
				return httpHeaders;
			}

			@Override
			public Flux<DataBuffer> getBody() {
				return outputMessage.getBody();
			}
		};
	}
}
