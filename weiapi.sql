/*
 Navicat Premium Data Transfer

 Source Server         : quake
 Source Server Type    : MySQL
 Source Server Version : 80034
 Source Host           : localhost:3306
 Source Schema         : weiapi

 Target Server Type    : MySQL
 Target Server Version : 80034
 File Encoding         : 65001

 Date: 02/06/2024 10:13:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for identify
-- ----------------------------
DROP TABLE IF EXISTS `identify`;
CREATE TABLE `identify`  (
  `id` bigint(0) NOT NULL,
  `accessKey` varchar(50) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '密钥',
  `secretKey` varchar(50) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '密钥',
  `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除（逻辑）',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `foreign_id` FOREIGN KEY (`id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of identify
-- ----------------------------
INSERT INTO `identify` VALUES (1, '39489cbf891474f98c28ee5071266dd4', 'a10248920c4f8bf58119ab5ee3511dd0', '2024-03-25 22:01:20', NULL, 0);

-- ----------------------------
-- Table structure for interface_info
-- ----------------------------
DROP TABLE IF EXISTS `interface_info`;
CREATE TABLE `interface_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '接口编号',
  `interfaceName` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL DEFAULT NULL COMMENT '接口名',
  `description` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL DEFAULT NULL COMMENT '接口描述',
  `url` varchar(512) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '接口地址',
  `requestHeader` text CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '请求头',
  `responseHeader` text CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '响应头',
  `method` varchar(15) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '请求类型',
  `params` text CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '请求参数',
  `interfaceStatus` tinyint(0) NOT NULL DEFAULT 0 COMMENT '接口状态',
  `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除（逻辑）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of interface_info
-- ----------------------------
INSERT INTO `interface_info` VALUES (1, 'getSpecifyName', NULL, 'http://localhost:8085/test/getName', '[{\"content-type\": \"json\"}]', '[{\"content-type\": \"json\"}]', 'GET', '[{\"name\":\"name\", \"type\":\"string\"}]', 0, '2024-03-09 16:20:40', '2024-05-29 09:56:48', 0);
INSERT INTO `interface_info` VALUES (2, 'getUser', NULL, 'http://localhost:8085/test/getUser', '[{\"content-type\": \"json\"}]', '[{\"content-type\": \"json\"}]', 'POST', '[{\"name\": \"username\", \"type\":\"string\"},{\"name\":\"like\", \"type\":\"string\"}]', 0, '2024-03-09 16:20:54', '2024-05-29 09:56:56', 0);
INSERT INTO `interface_info` VALUES (3, 'postSpecifyName', NULL, 'http://localhost:8085/test/getName', '[{\"content-type\": \"json\"}]', '[{\"content-type\": \"json\"}]', 'POST', '[{\"name\":\"name\", \"type\":\"string\"}]', 0, '2024-03-09 16:21:05', '2024-05-29 09:56:59', 0);


-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `username` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL DEFAULT NULL COMMENT '昵称',
  `description` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL DEFAULT NULL COMMENT '用户描述',
  `avatarUrl` varchar(1024) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL DEFAULT NULL COMMENT '头像',
  `email` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NULL DEFAULT NULL COMMENT '用户邮箱',
  `userAccount` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '账号',
  `userPassword` varchar(255) CHARACTER SET gbk COLLATE gbk_chinese_ci NOT NULL COMMENT '密码',
  `userStatus` int(0) NOT NULL DEFAULT 0 COMMENT '用户状态',
  `userRole` tinyint(0) NOT NULL DEFAULT 0 COMMENT '0-普通用户 1-管理员',
  `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除（逻辑）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'tes1', '一个积极上进的男生，正在学spring', '', '', 'test1', 'e1c0e0463d624e41e3a383b21a2972eb', 0, 0, '2023-09-04 22:01:44', '2024-01-04 08:31:37', 0);


-- ----------------------------
-- Table structure for user_interface
-- ----------------------------
DROP TABLE IF EXISTS `user_interface`;
CREATE TABLE `user_interface`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint(0) NOT NULL COMMENT '用户编号',
  `interfaceId` bigint(0) NOT NULL COMMENT '接口编号',
  `totalNum` int(0) NOT NULL DEFAULT 0 COMMENT '总调用次数',
  `leftNum` int(0) NOT NULL DEFAULT 0 COMMENT '剩余调用次数',
  `createTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updateTime` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `isDelete` tinyint(0) NOT NULL DEFAULT 0 COMMENT '是否删除（逻辑）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `foreign_userId`(`userId`) USING BTREE,
  INDEX `foreign_interfaceId`(`interfaceId`) USING BTREE,
  CONSTRAINT `foreign_interfaceId` FOREIGN KEY (`interfaceId`) REFERENCES `interface_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `foreign_userId` FOREIGN KEY (`userId`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = gbk COLLATE = gbk_chinese_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_interface
-- ----------------------------
INSERT INTO `user_interface` VALUES (1, 1, 1, 3, 2, '2024-03-27 09:01:30', '2024-03-27 22:26:48', 0);
INSERT INTO `user_interface` VALUES (2, 1, 2, 0, 5, '2024-03-27 09:01:39', NULL, 0);
INSERT INTO `user_interface` VALUES (3, 1, 3, 1, 4, '2024-03-27 09:01:47', '2024-03-27 09:05:43', 0);


SET FOREIGN_KEY_CHECKS = 1;
