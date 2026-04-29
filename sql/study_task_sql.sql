create table if not exists hjj.study_task
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId       bigint                             not null comment '用户 id',
    title        varchar(128)                       not null comment '任务标题',
    description  text                               null comment '任务描述',
    targetTime   int      default 0                 not null comment '目标学习时长（分钟）',
    taskDate     date                               not null comment '任务日期',
    status       tinyint  default 0                 not null comment '任务状态：0-待完成，1-已完成',
    likeNum      bigint   default 0                 not null comment '点赞数',
    checkinCount int      default 0                 not null comment '打卡次数',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '学习任务表';

create index idx_study_task_userId
    on hjj.study_task (userId);

create index idx_study_task_taskDate
    on hjj.study_task (taskDate);

create table if not exists hjj.study_checkin
(
    id           bigint auto_increment comment 'id'
        primary key,
    taskId       bigint                             not null comment '学习任务 id',
    userId       bigint                             not null comment '用户 id',
    checkinTime  datetime default CURRENT_TIMESTAMP not null comment '打卡时间',
    actualTime   int      default 0                 not null comment '实际学习时长（分钟）',
    note         varchar(512)                       null comment '打卡备注',
    images       varchar(2048)                      null comment '打卡图片列表（JSON数组）',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '学习打卡记录表';

create index idx_study_checkin_taskId
    on hjj.study_checkin (taskId);

create index idx_study_checkin_userId
    on hjj.study_checkin (userId);
