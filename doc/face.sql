DROP DATABASE IF EXISTS `face`;
CREATE DATABASE `face`;

USE `face`;


SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for user_face_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_face_info` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `userId`    varchar(45)  default '' not null comment '用户ID',
    `userName`  varchar(45)  default '' not null comment '用户名称',
    `userType`  tinyint(1)      default 0  not null comment '用户角色',
    `sex`      tinyint(1)      default 0  not null comment '性别',
    `age`      int      default 0  not null comment '性别',
    `photoUrl`  varchar(256) default '' not null comment '照片，url地址',
    `faceFeature` blob COMMENT '人脸特征',
    `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `GROUP_ID` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
