DROP DATABASE IF EXISTS `face_lib`;
CREATE DATABASE `face_lib`;

USE `face_lib`;


SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;

CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `userId` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户ID',
  `userName` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户名称',
  `userType` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户角色',
  `sex` tinyint(4) NOT NULL DEFAULT '0' COMMENT '性别',
  `gradeId` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '年级ID',
  `gradeName` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '年级名称',
  `classId` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '班级ID',
  `className` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '班级名称',
  `groupId` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '教师组ID',
  `groupName` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '教师组名称',
  `photoUrl` varchar(256) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '照片，url地址',
  `faceFeatureType` int(11) COLLATE utf8mb4_bin NOT NULL DEFAULT 0 COMMENT '人脸特征类型',
  `faceFeatureByte` blob COMMENT '人脸特征二进制',
  `faceFeatureFile` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '人脸特征本地文件',
  `score` int(11) COLLATE utf8mb4_bin NOT NULL DEFAULT 0 COMMENT '人脸评分',
   `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   `updateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT COMMENT='用户信息';
