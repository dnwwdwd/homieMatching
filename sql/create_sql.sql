create database if not exists hjj;

use hjj;

/*用户表*/
create table hjj.user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                                                                                 null comment '用户昵称',
    userAccount  varchar(256)                                                                                 null comment '账户',
    avatarUrl    varchar(1024) default 'https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg' not null comment '用户头像',
    gender       tinyint       default 1                                                                      null comment '用户性别',
    profile      varchar(512)                                                                                 null comment '个人简介',
    userPassword varchar(512)                                                                                 not null comment '用户密码',
    phone        varchar(128)                                                                                 null comment '电话',
    email        varchar(512)                                                                                 null comment '邮箱',
    userStatus   int           default 0                                                                      not null comment '状态 0 - 正常',
    createTime   datetime      default CURRENT_TIMESTAMP                                                      null comment '创建时间',
    updateTime   datetime      default CURRENT_TIMESTAMP                                                      null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint       default 0                                                                      not null comment '是否删除',
    userRole     int           default 0                                                                      not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                                                                                 null comment '星球编号',
    tags         varchar(1024)                                                                                null comment '标签列表(json)',
    longitude    decimal(10, 6)                                                                               null comment '经度',
    dimension    decimal(10, 6)                                                                               null comment '纬度',
    blogNum      bigint        default 0                                                                      not null comment '博客数',
    blogViewNum  bigint        default 0                                                                      not null comment '博客总浏览量',
    followNum    bigint        default 0                                                                      not null comment '关注数',
    fanNum       bigint        default 0                                                                      not null comment '粉丝数'
)
    comment '用户';



/*队伍表*/
create table if not exists hjj.team
(
    id           bigint auto_increment comment 'id'
        primary key,
    teamName   		varchar(256)                        not null comment '队伍名称',
    description 	varchar(1024)                       null comment ' 描述',
    maxNum        	int    default 1              		null comment '最大人数',
    expireTime      datetime							null comment '过期时间',
    userId 			bigint                              not null comment '队伍创建者/队长id',
    status         	tinyint default 0 		        	null comment '队伍状态 - 0 - 公开， 1 - 私有，2 - 加密
- ',
    password        varchar(512)                       null comment '队伍密码',
    createTime   	datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   	datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     	tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍信息';

/*用户队伍关系*/
create table if not exists hjj.user_team
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId 			bigint                             	comment '用户id',
    teamId 			bigint                             	comment '队伍id',
    joinTime   	datetime 							    comment '加入时间',
    createTime   	datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   	datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     	tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系表';

/*好友表*/
create table hjj.friend
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             not null comment '用户id（即自己id）',
    friendId   bigint                             not null comment '好友id',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '好友表';


/*聊天表*/
CREATE TABLE `chat`  (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '聊天记录id',
                         `fromId` bigint(20) NOT NULL COMMENT '发送消息id',
                         `toId` bigint(20) NULL DEFAULT NULL COMMENT '接收消息id',
                         `text` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                         `chatType` tinyint(4) NOT NULL COMMENT '聊天类型 1-私聊 2-群聊',
                         `createTime` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                         `teamId` bigint(20) NULL DEFAULT NULL,
                         `isDelete` tinyint(4) NULL DEFAULT 0,
                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '聊天消息表' ROW_FORMAT = COMPACT;

/**
  关注表
 */
create table hjj.follow
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             not null comment '被关注者 id',
    followerId bigint                             not null comment '粉丝 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '关注表';

create table hjj.blog
(
    id         bigint auto_increment comment 'id'
        primary key,
    title      varchar(128)                       not null comment '标题',
    coverImage varchar(256)                       null comment '封面图片',
    images     varchar(2048)                      null comment '图片列表',
    content    text                               not null comment '内容',
    userId     bigint                             not null comment '作者 id',
    tags       varchar(256)                       null comment '标签列表',
    viewNum    int      default 0                 not null comment '浏览数',
    likeNum    int      default 0                 not null comment '点赞数',
    starNum    int      default 0                 not null comment '收藏数',
    commentNum int      default 0                 not null comment '评论数',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '博客表';


select userId from user_team where teamId = 6 and isDelete = 0;

select * from team where id = 6 and isDelete = 0;


select * from user_team where teamId = 3;

SELECT ut.teamId, COUNT(ut.userId) AS member_count, MAX(t.maxNum) AS maxNum
FROM hjj.user_team ut
         JOIN hjj.team t ON ut.teamId = t.id
WHERE ut.isDelete = 0 AND t.isDelete = 0
GROUP BY ut.teamId
HAVING member_count > MAX(t.maxNum);


## 查询我和朋友们的最后一条聊天消息
SELECT c.*
FROM hjj.chat c
         JOIN (
    SELECT MAX(id) AS max_id
    FROM hjj.chat
    WHERE isDelete = 0
      AND (fromId = ? OR toId = ?) -- 假设这里的 ? 是你的用户 id
      AND toId IN (?) -- 假设这里的 ? 是 friendIdList
    GROUP BY toId
) AS max_ids ON c.id = max_ids.max_id;
