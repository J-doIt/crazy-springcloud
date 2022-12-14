/*
 Navicat Premium Data Transfer

 Source Server         : cdh1
 Source Server Type    : MySQL
 Source Server Version : 50732
 Source Host           : cdh1:3306
 Source Schema         : store

 Target Server Type    : MySQL
 Target Server Version : 50732
 File Encoding         : 65001

 Date: 28/03/2022 08:32:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for foundation_msg_subscribe
-- ----------------------------
DROP TABLE IF EXISTS `foundation_msg_subscribe`;
CREATE TABLE `foundation_msg_subscribe`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bean_class` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `method_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `msg_group` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `msg_topic` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `prj_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `spring_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `subscribe_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of foundation_msg_subscribe
-- ----------------------------
INSERT INTO `foundation_msg_subscribe` VALUES (1, NULL, NULL, NULL, NULL, 'test', 'test1', 'kafka-provider', NULL, '1', NULL, NULL);

-- ----------------------------
-- Table structure for mq_confirm
-- ----------------------------
DROP TABLE IF EXISTS `mq_confirm`;
CREATE TABLE `mq_confirm`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `consume_count` int(11) NULL DEFAULT NULL,
  `consumer_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `created_time` datetime(0) NULL DEFAULT NULL,
  `message_id` bigint(20) NULL DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `version` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mq_consumer
-- ----------------------------
DROP TABLE IF EXISTS `mq_consumer`;
CREATE TABLE `mq_consumer`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `consumer_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `consumer_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `consumer_remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `consumer_status` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mq_group
-- ----------------------------
DROP TABLE IF EXISTS `mq_group`;
CREATE TABLE `mq_group`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_time` datetime(0) NULL DEFAULT NULL,
  `group_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `group_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `group_remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `group_status` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mq_message
-- ----------------------------
DROP TABLE IF EXISTS `mq_message`;
CREATE TABLE `mq_message`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_time` datetime(0) NULL DEFAULT NULL,
  `delay_level` int(11) NULL DEFAULT NULL,
  `message_body` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `message_status` int(11) NULL DEFAULT NULL,
  `message_topic` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `order_type` int(11) NULL DEFAULT NULL,
  `producer_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `resend_times` int(11) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `version` int(11) NULL DEFAULT NULL,
  `yn` int(11) NULL DEFAULT NULL,
  `msg_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `app_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `body` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `ext_field_1` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `ext_field_2` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `ext_field_3` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `ext_field_4` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `target_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `target_type` int(11) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mq_producer
-- ----------------------------
DROP TABLE IF EXISTS `mq_producer`;
CREATE TABLE `mq_producer`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `producer_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `producer_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `producer_remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `producer_status` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mq_stream
-- ----------------------------
DROP TABLE IF EXISTS `mq_stream`;
CREATE TABLE `mq_stream`  (
  `stream_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `app_key` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `msg_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `read_status` int(11) NULL DEFAULT NULL,
  `read_time` datetime(0) NULL DEFAULT NULL,
  `send_status` int(11) NULL DEFAULT NULL,
  `send_time` datetime(0) NULL DEFAULT NULL,
  `status` int(11) NULL DEFAULT NULL,
  `target_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `target_type` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`stream_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mq_subscribe_rule
-- ----------------------------
DROP TABLE IF EXISTS `mq_subscribe_rule`;
CREATE TABLE `mq_subscribe_rule`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_time` datetime(0) NULL DEFAULT NULL,
  `group_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `rule_remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `rule_status` int(11) NULL DEFAULT NULL,
  `subscribe_rule_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `topic_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mq_subscribe_rule
-- ----------------------------
INSERT INTO `mq_subscribe_rule` VALUES ('1', NULL, 'group.seckill.order', NULL, NULL, NULL, 'topic.seckill');

-- ----------------------------
-- Table structure for mq_topic
-- ----------------------------
DROP TABLE IF EXISTS `mq_topic`;
CREATE TABLE `mq_topic`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_time` datetime(0) NULL DEFAULT NULL,
  `mq_type` int(11) NULL DEFAULT NULL,
  `msg_type` int(11) NULL DEFAULT NULL,
  `topic_remarks` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `topic_status` int(11) NULL DEFAULT NULL,
  `topic_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `topic_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '应用标识',
  `resource_ids` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资源限定串(逗号分割)',
  `client_secret` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用密钥(bcyt) 加密',
  `client_secret_str` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '应用密钥(明文)',
  `scope` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '范围',
  `authorized_grant_types` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '5种oauth授权方式(authorization_code,password,refresh_token,client_credentials)',
  `web_server_redirect_uri` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '回调地址 ',
  `authorities` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限',
  `access_token_validity` int(11) NULL DEFAULT NULL COMMENT 'access_token有效期',
  `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT 'refresh_token有效期',
  `additional_information` varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '{}' COMMENT '{}',
  `autoapprove` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'true' COMMENT '是否自动授权 是-true',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `client_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '应用名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for sample
-- ----------------------------
DROP TABLE IF EXISTS `sample`;
CREATE TABLE `sample`  (
  `id` int(11) NOT NULL COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '标题',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seckill_order
-- ----------------------------
DROP TABLE IF EXISTS `seckill_order`;
CREATE TABLE `seckill_order`  (
  `order_id` bigint(20) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `sku_id` bigint(20) NULL DEFAULT NULL,
  `pay_money` decimal(19, 2) NULL DEFAULT NULL,
  `pay_time` datetime(0) NULL DEFAULT NULL,
  `status` smallint(6) NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE INDEX `order_index_1`(`sku_id`, `user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seckill_order
-- ----------------------------
INSERT INTO `seckill_order` VALUES (1157379809567835136, '2021-05-16 13:06:11', 1157197244718385152, NULL, NULL, 1, 1);
INSERT INTO `seckill_order` VALUES (1221693005367345152, '2021-08-13 06:45:17', 1157197244718385152, NULL, NULL, 1, 13);
INSERT INTO `seckill_order` VALUES (1221721872043869184, '2021-08-13 07:43:16', 1157197244718385152, NULL, NULL, 1, 16);
INSERT INTO `seckill_order` VALUES (1221723564655248384, '2021-08-13 07:46:38', 1157197244718385152, NULL, NULL, 1, 17);
INSERT INTO `seckill_order` VALUES (1221724156245050368, '2021-08-13 07:47:49', 1157197244718385152, NULL, NULL, 1, 18);
INSERT INTO `seckill_order` VALUES (1221730721144505344, '2021-08-13 08:00:51', 1157197244718385152, NULL, NULL, 1, 20);
INSERT INTO `seckill_order` VALUES (1221738497233126400, '2021-08-13 08:16:18', 1157197244718385152, NULL, NULL, 1, 24);

-- ----------------------------
-- Table structure for seckill_segment_stock
-- ----------------------------
DROP TABLE IF EXISTS `seckill_segment_stock`;
CREATE TABLE `seckill_segment_stock`  (
  `seg_stock_id` bigint(20) NOT NULL,
  `good_id` bigint(20) NOT NULL,
  `seg_index` int(8) NOT NULL,
  `stock_count` bigint(20) NULL DEFAULT NULL,
  `raw_stock` int(8) NULL DEFAULT NULL COMMENT '原始库存',
  `sku_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`seg_stock_id`) USING BTREE,
  INDEX `good_id_segment_index`(`good_id`, `seg_index`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seckill_segment_stock
-- ----------------------------
INSERT INTO `seckill_segment_stock` VALUES (1155041631863310336, 1, 0, -1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635176810496, 1, 1, -1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635218753536, 1, 2, -1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635243919360, 1, 3, -1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635269085184, 1, 4, -1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635294251008, 1, 5, -1007, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635327805440, 1, 6, -1004, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635352971264, 1, 7, -996, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635378137088, 1, 8, -993, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1155041635411691520, 1, 9, -1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745506018304, 1157197244718385152, 0, 999, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745598292992, 1157197244718385152, 1, 999, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745598292993, 1157197244718385152, 2, 999, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745598292994, 1157197244718385152, 3, 999, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745606681600, 1157197244718385152, 4, 999, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745606681601, 1157197244718385152, 5, 1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745606681602, 1157197244718385152, 6, 999, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745615070208, 1157197244718385152, 7, 1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745615070209, 1157197244718385152, 8, 1000, 1000, NULL);
INSERT INTO `seckill_segment_stock` VALUES (1221215745623458816, 1157197244718385152, 9, 1001, 1001, NULL);

-- ----------------------------
-- Table structure for seckill_sku
-- ----------------------------
DROP TABLE IF EXISTS `seckill_sku`;
CREATE TABLE `seckill_sku`  (
  `sku_id` bigint(20) NOT NULL COMMENT '商品id',
  `cost_price` decimal(19, 2) NULL DEFAULT NULL COMMENT '秒杀价格',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `end_time` datetime(0) NULL DEFAULT NULL,
  `sku_image` varchar(400) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `sku_price` decimal(19, 2) NULL DEFAULT NULL COMMENT '价格',
  `start_time` datetime(0) NULL DEFAULT NULL,
  `stock_count` int(8) NULL DEFAULT NULL COMMENT '剩余库存',
  `sku_title` varchar(400) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL,
  `raw_stock` int(8) NULL DEFAULT NULL COMMENT '原始库存',
  `exposed_key` varchar(400) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '秒杀md5',
  PRIMARY KEY (`sku_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seckill_sku
-- ----------------------------
INSERT INTO `seckill_sku` VALUES (1, 1.00, '2019-09-08 00:03:29', '2019-10-26 00:03:29', NULL, 10.00, '2019-08-08 00:03:29', 17, 'test', 20, NULL);
INSERT INTO `seckill_sku` VALUES (2, 1.00, '2019-09-05 11:24:27', '2019-10-26 11:24:19', NULL, 10.00, '2019-02-01 11:24:06', 10000, 'test2', NULL, NULL);
INSERT INTO `seckill_sku` VALUES (745622247921783808, 1.00, '2019-10-26 09:47:51', '2019-11-26 09:47:51', NULL, 11.00, '2019-09-26 09:47:51', 100, 'apple', NULL, NULL);
INSERT INTO `seckill_sku` VALUES (745650086154966016, 1.00, '2019-10-26 11:13:37', '2019-11-26 11:13:37', NULL, 11.00, '2019-09-26 11:13:37', 100, 'apple', NULL, NULL);
INSERT INTO `seckill_sku` VALUES (1157192023900423168, 10.00, '2021-05-16 06:53:59', '2021-06-16 06:53:59', NULL, 1000.00, '2021-04-16 06:53:59', 10000, '秒杀商品-1', 10000, NULL);
INSERT INTO `seckill_sku` VALUES (1157196356683565056, 10.00, '2021-05-16 07:02:35', '2021-06-16 07:02:35', NULL, 1000.00, '2021-04-16 07:02:35', 10000, '秒杀商品-1', 10000, NULL);
INSERT INTO `seckill_sku` VALUES (1157197244718385152, 10.00, '2021-05-16 07:04:21', '2021-12-16 07:04:21', NULL, 1000.00, '2021-04-16 07:04:21', 10001, '秒杀商品-1', 10001, '4b70903f6e1aa87788d3ea962f8b2f0e');
INSERT INTO `seckill_sku` VALUES (1224036540338929664, 1000.00, '2021-08-16 12:22:06', '2021-09-16 12:22:06', NULL, 1000.00, '2021-07-16 12:22:06', 10000, '秒杀商品-1', 10000, NULL);
INSERT INTO `seckill_sku` VALUES (1224036923438268416, 1000.00, '2021-08-16 12:22:52', '2021-09-16 12:22:52', NULL, 1000.00, '2021-07-16 12:22:52', 10000, '秒杀商品-3', 10000, NULL);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `parent_id` int(11) NOT NULL,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `path_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `css` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `sort` int(11) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `type` tinyint(1) NOT NULL,
  `hidden` tinyint(1) NOT NULL DEFAULT 0,
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`menu_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 85 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `login_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  `enabled` bit(1) NULL DEFAULT NULL,
  `head_img_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_del` bit(1) NULL DEFAULT NULL,
  `mobile` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `nick_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `open_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sex` int(11) NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `user_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('test', '$2a$10$AsCxXPI8B/JDzKK56ZACjuH9Pi2TuT6LLC0Nwh8Qt3a2eFp04gziy', 1, NULL, NULL, NULL, NULL, NULL, '测试用户1', NULL, NULL, NULL, NULL, 'test');
INSERT INTO `user_info` VALUES ('test2', '$2a$10$AsCxXPI8B/JDzKK56ZACjuH9Pi2TuT6LLC0Nwh8Qt3a2eFp04gziy', 2, NULL, NULL, NULL, NULL, NULL, '测试用户2', NULL, NULL, NULL, NULL, 'test2');

SET FOREIGN_KEY_CHECKS = 1;
