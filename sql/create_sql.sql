
/*用户表*/
create table if not exists hjj.user
(
    username     varchar(256)                       null comment '用户昵称',
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                       null comment '账户',
    avatarUrl    varchar(1024)                      null comment '用户头像',
    gender       tinyint                            null comment '用户性别',
    profile      varchar(512)                       null comment '个人简介',
    userPassword varchar(512)                       not null comment '用户密码',
    phone        varchar(128)                       null comment '电话',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    userRole     int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode   varchar(512)                       null comment '星球编号',
    tags         varchar(1024)                      null comment '标签列表(json)'
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