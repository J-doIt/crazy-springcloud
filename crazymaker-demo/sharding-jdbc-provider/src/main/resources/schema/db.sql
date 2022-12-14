 * db 操作之前登陆客户端

 mysql  -uroot -p123456

 drop database if exists   sharding-db1;
 drop database if exists   sharding-db2;

drop database if exists sharding_db1;
drop database if exists sharding_db2;

 create database sharding_db1 default character set utf8mb4 collate utf8mb4_unicode_ci;
 create database sharding_db2 default character set utf8mb4 collate utf8mb4_unicode_ci;




TRUNCATE TABLE `t_user_0`;
TRUNCATE TABLE `t_user_1`;
TRUNCATE TABLE `t_user_2`;
TRUNCATE TABLE `t_user_3`;


TRUNCATE TABLE `t_order_0`;
TRUNCATE TABLE `t_order_1`;
TRUNCATE TABLE `t_order_2`;
TRUNCATE TABLE `t_order_3`;



TRUNCATE TABLE `t_config`;
