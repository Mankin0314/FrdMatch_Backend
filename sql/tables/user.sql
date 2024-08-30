 -- auto-generated definition
create table user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账号',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '性别',
    userPassword varchar(512)                       not null comment '密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                       null comment '星球编号'
)
    comment '用户';

------队伍表
 CREATE TABLE frdmatch.`team` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `name` varchar(256) NOT NULL COMMENT '队伍名称',
                         `description` varchar(1024) DEFAULT NULL COMMENT '描述',
                         `maxNum` int NOT NULL DEFAULT '1' COMMENT '最大人数',
                         `expireTime` datetime DEFAULT NULL COMMENT '过期时间',
                         `userId` bigint DEFAULT NULL COMMENT '用户id',
                         `status` int NOT NULL DEFAULT '0' COMMENT '0 - 公开，1 - 私有，2 - 加密',
                         `password` varchar(512) DEFAULT NULL COMMENT '密码',
                         `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
                         PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='队伍';


-------队伍---用户表
 CREATE TABLE frdmatch.`user_team` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                              `userId` bigint DEFAULT NULL COMMENT '用户id',
                              `teamId` bigint DEFAULT NULL COMMENT '队伍id',
                              `joinTime` datetime DEFAULT NULL COMMENT '加入时间',
                              `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
                              PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户队伍关系';



