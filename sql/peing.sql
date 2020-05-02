/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost:3306
 Source Schema         : peing

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : 65001

 Date: 02/05/2020 19:04:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ban
-- ----------------------------
DROP TABLE IF EXISTS `ban`;
CREATE TABLE `ban`  (
  `baner_id` bigint(20) NOT NULL COMMENT '拉黑者id',
  `baned_id` bigint(20) NOT NULL COMMENT '被拉黑者id',
  `count` int(11) NULL DEFAULT NULL COMMENT '拉黑次数',
  PRIMARY KEY (`baner_id`, `baned_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `message_id` bigint(20) NOT NULL COMMENT '消息id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '消息内容',
  `publish_date` datetime(0) NULL DEFAULT NULL COMMENT '消息发送时间',
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for message_user
-- ----------------------------
DROP TABLE IF EXISTS `message_user`;
CREATE TABLE `message_user`  (
  `message_id` bigint(20) NOT NULL COMMENT '消息id ',
  `user_id` bigint(20) NOT NULL COMMENT '接受者id',
  `is_read` int(255) NULL DEFAULT NULL COMMENT '是否已查看',
  PRIMARY KEY (`message_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `question_id` bigint(255) NOT NULL COMMENT '提问id',
  `questioner_id` bigint(20) NULL DEFAULT NULL COMMENT '提问者id',
  `questioned_id` bigint(20) NULL DEFAULT NULL COMMENT '被提问者id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '提问内容',
  `question_date` datetime(0) NULL DEFAULT NULL COMMENT '提问时间',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回复',
  `reply_date` datetime(0) NULL DEFAULT NULL COMMENT '最后修改回复时间',
  `deleted` tinyint(1) NULL DEFAULT NULL COMMENT '逻辑删除',
  `delete_date` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `is_ban` tinyint(1) NULL DEFAULT NULL COMMENT '是否拉黑',
  `is_report` tinyint(255) NULL DEFAULT NULL COMMENT '是否举报',
  PRIMARY KEY (`question_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `report_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '举报id',
  `reporter_id` bigint(20) NULL DEFAULT NULL COMMENT '举报人id ',
  `reported_id` bigint(20) NULL DEFAULT NULL COMMENT '被举报人id ',
  `question_id` bigint(20) NULL DEFAULT NULL COMMENT '问题id',
  `reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '举报理由',
  `report_date` datetime(0) NULL DEFAULT NULL COMMENT '举报时间 ',
  `result` int(255) NULL DEFAULT NULL COMMENT '处理结果 0未处理 1封禁 2不封禁',
  PRIMARY KEY (`report_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` bigint(255) NOT NULL COMMENT '用户id',
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '自我介绍',
  `accept_question` int(255) NULL DEFAULT 0 COMMENT '是否开启提问箱',
  `signup_date` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  `update_date` datetime(0) NULL DEFAULT NULL COMMENT '最后修改密码时间',
  `is_active` tinyint(1) NULL DEFAULT 0 COMMENT '账户是否激活',
  `is_ban` tinyint(1) NULL DEFAULT 0 COMMENT '是否被封禁',
  `ban_date` datetime(0) NULL DEFAULT NULL COMMENT '被封禁时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user_id` bigint(255) NOT NULL COMMENT '用户id',
  `role_id` int(11) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
