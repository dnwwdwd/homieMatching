create database if not exists hjj;

use hjj;

/*用户表*/
create table hjj.user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)  default '无名氏'                                                               not null comment '用户昵称',
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
    tags         varchar(1024)                                                                                null comment '标签列表(json)',
    longitude    decimal(10, 6)                                                                               null comment '经度',
    dimension    decimal(10, 6)                                                                               null comment '纬度',
    blogNum      bigint        default 0                                                                      not null comment '博客数',
    blogViewNum  bigint        default 0                                                                      not null comment '博客总浏览量',
    followNum    bigint        default 0                                                                      not null comment '关注数',
    fanNum       bigint        default 0                                                                      not null comment '粉丝数',
    score        int           default 0                                                                      not null comment '积分'
)
    comment '用户';



/*队伍表*/
create table hjj.team
(
    id          bigint auto_increment comment 'id'
        primary key,
    teamName    varchar(256)                       not null comment '队伍名称',
    description varchar(1024)                      null comment ' 描述',
    maxNum      int      default 1                 null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             not null comment '队伍创建者/队长id',
    status      tinyint  default 0                 null comment '队伍状态 - 0 - 公开， 1 - 私有，2 - 加密
- ',
    password    varchar(512)                       null comment '队伍密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍信息';



/*用户队伍关系*/
create table hjj.user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
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
create table hjj.chat
(
    id         bigint auto_increment comment '聊天记录id'
        primary key,
    fromId     bigint                                  not null comment '发送消息id',
    toId       bigint                                  null comment '接收消息id',
    text       varchar(512) collate utf8mb4_unicode_ci null,
    chatType   tinyint                                 not null comment '聊天类型 1-私聊 2-群聊',
    createTime datetime default CURRENT_TIMESTAMP      null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP      null,
    teamId     bigint                                  null,
    isDelete   tinyint  default 0                      null
)
    comment '聊天消息表' collate = utf8mb4_general_ci
                         row_format = COMPACT;



/*关注表*/
create table hjj.follow
(
    id         bigint auto_increment comment 'id'
        primary key,
    followeeId bigint                             not null comment '被关注者 id',
    followerId bigint                             not null comment '粉丝 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '关注表';


/*博客表*/
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
    viewNum    bigint   default 0                 not null comment '浏览数',
    likeNum    bigint   default 0                 not null comment '点赞数',
    starNum    bigint   default 0                 not null comment '收藏数',
    commentNum bigint   default 0                 not null comment '评论数',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '博客表';

/*评论表*/
create table hjj.comment
(
    id         bigint auto_increment
        primary key,
    userId     bigint                             null comment '用户id（评论者id）',
    blogId     bigint                             null comment '博客id',
    text       varchar(512)                       null comment '评论内容',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '评论表';


/*消息表*/
create table hjj.message
(
    id         bigint auto_increment comment '主键'
        primary key,
    type       tinyint                            null comment '类型 - 0 - 收藏 1 - 点赞 2 - 关注消息 3 - 私发消息 4 - 队伍消息',
    fromId     bigint                             null comment '消息发送的用户id',
    toId       bigint                             null comment '消息接收的用户id',
    text       varchar(255)                       null comment '消息内容',
    avatarUrl  varchar(256)                       null comment '头像',
    blogId     bigint                             null comment '博客 id',
    teamId     bigint                             null comment '队伍 id',
    isRead     tinyint  default 0                 null comment '已读-0 未读 ,1 已读',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 null comment '逻辑删除'
)
    collate = utf8mb4_general_ci
    row_format = COMPACT;

/*反馈表*/
create table hjj.feedback
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    rate       double                             null comment '评分',
    advice     varchar(500)                       null comment '建议',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    isDeleted  tinyint  default 0                 not null comment '是否删除'
)
    comment '反馈表';



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
      AND (fromId = 1 or toId = 1)
        AND toId IN (5862, 2, 6, 5882, 7, 5, 5887, 5856, 5859, 5915, 5921, 5923, 9, 5934, 5937, 5938, 5943, 5945, 5936, 5944, 5948, 5956)
    GROUP BY toId
        ) AS max_ids ON c.id = max_ids.max_id AND (c.fromId = 1 OR c.toId = 1) and chatType = 1 order by createTime desc;

select id from chat where toId = 2 or fromId = 2;

select friendId from friend where userId = 2;

select a.*,b.* from hjj.team a, hjj.user_team b where a.id = b.teamId;

alter table hjj.blog auto_increment = 8;

select * from chat where toId = 2 or fromId = 2 order by createTime desc;

select id from blog where isDelete = 0 limit 7, 4;
select id from blog limit 2, 4;
select id from blog limit 3, 4;

select friendId from friend where userId = 1 and isDelete = 0;

select * from chat where isDelete = 0 and (toId = 1 or fromId = 1) and chatType = 1 group by toId;

select sum(likeNum) from blog where userId = 1 and isDelete = 0