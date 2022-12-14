DROP TABLE IF EXISTS `t_order_0`;
DROP TABLE IF EXISTS `t_order_1`;
DROP TABLE IF EXISTS `t_order_2`;
DROP TABLE IF EXISTS `t_order_3`;

DROP TABLE IF EXISTS `t_config`;

DROP TABLE IF EXISTS `t_user`;
DROP TABLE IF EXISTS `t_user_0`;
DROP TABLE IF EXISTS `t_user_1`;
DROP TABLE IF EXISTS `t_user_2`;
DROP TABLE IF EXISTS `t_user_3`;


DROP TABLE IF EXISTS `t_order_item_0`;
DROP TABLE IF EXISTS `t_order_item_1`;
DROP TABLE IF EXISTS `t_order_item_2`;
DROP TABLE IF EXISTS `t_order_item_3`;

CREATE TABLE `t_user_0` (`user_id` bigInt NOT NULL, `name` VARCHAR(45) NULL, PRIMARY KEY (`user_id`));
CREATE TABLE `t_user_1` (`user_id` bigInt NOT NULL, `name` VARCHAR(45) NULL, PRIMARY KEY (`user_id`));
CREATE TABLE `t_user_2` (`user_id` bigInt NOT NULL, `name` VARCHAR(45) NULL, PRIMARY KEY (`user_id`));
CREATE TABLE `t_user_3` (`user_id` bigInt NOT NULL, `name` VARCHAR(45) NULL, PRIMARY KEY (`user_id`));


CREATE TABLE `t_order_0` (`order_id` bigInt NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_id`));
CREATE TABLE `t_order_1` (`order_id` bigInt NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_id`));
CREATE TABLE `t_order_2` (`order_id` bigInt NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_id`));
CREATE TABLE `t_order_3` (`order_id` bigInt NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_id`));
CREATE TABLE `t_config` (`id` bigInt NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`id`));


CREATE TABLE `t_order_item_0` (`order_item_id` INT NOT NULL, `order_id` INT NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_item_id`));
CREATE TABLE `t_order_item_1` (`order_item_id` INT NOT NULL, `order_id` INT NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_item_id`));
CREATE TABLE `t_order_item_2` (`order_item_id` INT NOT NULL, `order_id` INT NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_item_id`));
CREATE TABLE `t_order_item_3` (`order_item_id` INT NOT NULL, `order_id` INT NOT NULL, `user_id` INT NOT NULL, `status` VARCHAR(45) NULL, PRIMARY KEY (`order_item_id`));


INSERT INTO `t_config` VALUES (1, 'config_key', 'config_value');
