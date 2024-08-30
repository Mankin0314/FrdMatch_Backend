/*
 Navicat Premium Data Transfer

 Source Server         : 本机MySQL
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : usercenter

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 01/07/2024 16:18:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `username` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userAccount` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '账号',
  `avatarUrl` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像',
  `gender` tinyint NULL DEFAULT NULL COMMENT '性别',
  `userPassword` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `phone` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `userStatus` int NOT NULL DEFAULT 0 COMMENT '状态 0 - 正常',
  `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  `userRole` int NOT NULL DEFAULT 0 COMMENT '用户角色 0 - 普通用户 1 - 管理员',
  `planetCode` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '星球编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('dingding', 1, 'dingjiaxiong', 'https://avatars.githubusercontent.com/u/61930795?v=4', NULL, '36c8e9015dc8fa1a07f91947b602e18b', NULL, NULL, 0, '2024-06-27 15:04:42', '2024-06-28 10:17:17', 0, 1, '19969');
INSERT INTO `user` VALUES ('xiaoadmin', 2, 'admin', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202008%2F03%2F20200803194454_fxzce.jpg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1722130919&t=6ff3a41a1ca45915606d186ff3256331', NULL, '36c8e9015dc8fa1a07f91947b602e18b', NULL, NULL, 0, '2024-06-27 18:12:43', '2024-06-28 09:42:16', 0, 0, NULL);
INSERT INTO `user` VALUES ('ding', 3, 'dingdingding', 'https://b0.bdstatic.com/a1588b60ed782b418ac46cc77d76d756.jpg@h_1280', NULL, '36c8e9015dc8fa1a07f91947b602e18b', NULL, NULL, 0, '2024-06-28 10:28:48', '2024-06-28 10:29:52', 0, 0, '2');
INSERT INTO `user` VALUES ('jia', 4, 'jiamin', 'https://img1.baidu.com/it/u=1523844190,2818655112&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=406', NULL, '36c8e9015dc8fa1a07f91947b602e18b', NULL, NULL, 0, '2024-06-28 12:17:19', '2024-06-28 12:18:03', 0, 0, '3');
INSERT INTO `user` VALUES ('dogDingJiaxiong', 5, 'dingjiaxiong', 'https://pics0.baidu.com/feed/5fdf8db1cb13495437cd2b54e0f12454d0094a7f.jpeg?token=958bb5f910b4e78dc9d5995fb46847d5', 0, '12345678', '123', '456', 0, '2024-06-28 15:29:26', '2024-06-28 15:29:26', 0, 0, NULL);
INSERT INTO `user` VALUES ('dogDingJiaxiong', 6, 'dingjiaxiong', 'https://pics0.baidu.com/feed/5fdf8db1cb13495437cd2b54e0f12454d0094a7f.jpeg?token=958bb5f910b4e78dc9d5995fb46847d5', 0, '12345678', '123', '456', 0, '2024-06-28 15:36:27', '2024-06-28 15:36:27', 0, 0, NULL);
INSERT INTO `user` VALUES ('dogDingJiaxiong', 7, 'dingjiaxiong', 'https://pics0.baidu.com/feed/5fdf8db1cb13495437cd2b54e0f12454d0094a7f.jpeg?token=958bb5f910b4e78dc9d5995fb46847d5', 0, '12345678', '123', '456', 0, '2024-06-28 15:36:54', '2024-06-28 15:36:54', 0, 0, NULL);

SET FOREIGN_KEY_CHECKS = 1;
