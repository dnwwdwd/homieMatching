# homie 匹配系统

> 作者：C1own
>
> [Github 主页](https://github.com/dnwwdwd)
>
> [CSDN 主页](https://blog.csdn.net/xyendjsj?type=blog)

![](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609142339.png)



## 项目介绍

homie 匹配系统是一个移动端网页的在线云交友平台。实现了按标签匹配、查找用户，基于 Redis GEO 实现搜索附近用户，同时个人还可以建队、组队以打造个人学习队伍。除了添加好友、搜索好友外，还基于 Websocket 实现好友间私聊，方便用户寻找志同道合的学习搭子。

### 线上地址

[厚米匹配系统](http://hm.hejiajun.icu/)

### 前端地址

[homie 匹配前端地址](https://github.com/dnwwdwd/homieMatching-fronted)

### 后端地址

[homie 匹配后端地址](https://github.com/dnwwdwd/homieMatching)

### 项目部署教程

[homie 匹配部署教程](https://blog.csdn.net/xyendjsj/article/details/135921460?spm=1001.2014.3001.5501)



## 技术选型

### 前端

| 技术       | 用途                       | 版本   |
| ---------- | -------------------------- | ------ |
| Vue        | 前端经典框架，方便开发页面 | 3.3.11 |
| Vue-Router | 细致的导航控制             | 4      |
| Axios      | 发送请求至后端             | 1.6.2  |
| Vant       | 移动端样式组件库           | 4.8.0  |
| Vite       | 前端构建工具               | 5.0.8  |



### 后端

| 技术                 | 用途                                         | 版本    |
| -------------------- | -------------------------------------------- | ------- |
| Spring Boot          | 快构建 Spring 应用                           | 2.7.6   |
| JDK                  | Java 应用开发工具                            | 1.8     |
| MyBatis              | 操作数据库的框架                             | 3.5.2   |
| MyBatis-Plus         | MyBatis的增强框架，无需编写 SQL 语句         | 3.5.2   |
| MySQL                | 一个关系型数据库产品，用于存储数据           | 8.0..33 |
| Redis                | 一个非关系型数据库产品，用于存储数据         | 5.      |
| WebSocket            | 使得客户端和服务器之间的数据交换变得更加简单 | 2.4.1   |
| Lombok               | 实体类方法的快速生成工具                     |         |
| knife4j              | 在线接口文档生成的库                         | 2.0.9   |
| EasyExcel            | 快速、低占用地操作 Excel                     | 3.3.2   |
| hutool               | 强而全的工具库                               | 5.7.17  |
| Guava-Retrying       | 提供重试机制的库                             | 1.0.6   |
| Apache-commons-lang3 | 工具库                                       | 3.12.0  |

## 功能展示

登录

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609143001.png" alt="image-20240609143001885" style="zoom: 67%;" />

注册

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609142952.png" alt="image-20240609142952622" style="zoom: 67%;" />

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609143117.png" alt="image-20240609143117032" style="zoom: 67%;" />

首页

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145323.png" alt="image-20240609145323203" style="zoom:67%;" />

按标签匹配相似用户

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145335.png" alt="image-20240609145335696" style="zoom:67%;" />

按标签搜索用户

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145012.png" alt="image-20240609145012357" style="zoom:67%;" />

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145002.png" alt="image-20240609145002577" style="zoom:67%;" />

按距离搜索用户

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145122.png" alt="image-20240609145122634" style="zoom:67%;" />

好友页面

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145434.png" alt="image-20240609145434695" style="zoom:67%;" />

搜索好友

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609171231.png" alt="image-20240609171231079" style="zoom:67%;" />

好友私聊

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145534.png" alt="image-20240609145534153" style="zoom:67%;" />

建队

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609145549.png" alt="image-20240609145549455" style="zoom:67%;" />

个人页面

<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609143327.png" alt="image-20240609143327588" style="zoom: 67%;" />



<img src="https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609143357.png" alt="image-20240609143357705" style="zoom:67%;" />



## 项目亮点

1. 基于 Spring AOP + Axios 实现前端登录拦截
2. 基于 Redis 实现分布式 Session 存储
3. 使用 Redis List 结构配合 Vue-infinite-loading 组件实现滑动加载
4. Spring Scheduling + Redis 分布式锁实现缓存预热
5. 了解编辑距离算法，可用于匹配相似字符串，单词校验
6. 基于 Redis GEO 存储用户地理微信信息，实现搜索附近用户
7. 基于 Redis 分布式锁防止用户重复入队
8. 通过 Guava 库实现重试机制，保证缓存数据一致性
9. 基于 Websocket 实现用户间私聊
10. 集成第三方库生成接口测试文档，方便测试项目接口
11. 熟悉 EasyExcel 的使用
12. 基于 Axios 封装请求实例，方便请求后端接口
13. 熟悉 Vant 组件库的使用
14. 熟悉 Vue3 setup 语法
15. 掌握 Vue-Router 基本使用



## 数据库表

> 如果大家拉取了后端源码，直接找到 sql/create_sql.sql 文件直接运行即可创建相应数据库和表结构

### 用户表

```sql
/*用户表*/
create table hjj.user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户昵称',
    userAccount  varchar(256)                       null comment '账户',
    avatarUrl    varchar(1024)                      null comment '用户头像' default 'https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',
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
    tags         varchar(1024)                      null comment '标签列表(json)',
    longitude    decimal(10, 6)                     null comment '经度',
    dimension    decimal(10, 6)                     null comment '纬度'
)
    comment '用户';
```



### 队伍表

```sql
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
```



### 用户关系表

```sql
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
```



### 好友表

```sql
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
```



### 聊天表

```sql
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
```



## 项目初始化

### 前端

基础环境

- 软件：WebStorm 2022.2.5 / Vscode
- 环境：Node 版本：18.0.2

1）拉取项目

```bash
git clone https://github.com/dnwwdwd/homeiMatching-frontend.git
```

2）安装依赖

```bash
npm install
```

3）运行项目

```bash
npm run dev
```

4）打包项目

```bash
npm run build
```



### 数据库

1）对于拉去后端项目的同学直接运行 /sql/create_sql.ql 文件即可，前提电脑装了 MySQL（5.7 或 8.x 都可）

2）完事后将 application.yml 文件中数据库的账号密码改为自己的即可，对了此项目还用了 Redis，所以还要修改 Redis 的连接配置，有密码的加上密码

```yml
spring:
  profiles:
    active: dev
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
  application:
    name: homieMatching
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/hjj?characterEncoding=UTF-8
    username: root
    password: 123456
  session:
    timeout: 86400
    store-type: redis
  redis:
    port: 6379
    host: localhost
    database: 0
server:
  port: 8080
  servlet:
    context-path: /api
    session:
      cookie:
        domain: localhost
        same-site: none
        secure: true
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: false
  global-config:
    db-config:
      logic-delete-field: isDelete
      logic-delete-value: 1
      logic-not-delete-value: 0
```



### 后端

基础环境

- 软件：IDEA 2023.1.6
- JDK 1.8 + MySQL 8.0.33 + Redis 5.x（最好是 5.x 以上） + Spring Boot 2.7.6 

> 更细的环境请看上面的技术选型，上面有具体的依赖关系和版本。
>
> **各位一定要注意在启动后端前，一定要先建好数据库表和安装 Redis，改好 yml 文件的连接信息再启动后端。**

1）拉取项目

```bash
git clone https://github.com/dnwwdwd/homieMatching.git
```

2）点击小圆圈重新加载所有 Maven 项目 ，下载依赖。

![image-20240609154936738](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609154936.png)

3）启动后端

找到 HomieMatchingApplication 主类，点击运行即可。

![image-20240609154419326](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240609154419.png)



## 项目部署

[homie 匹配部署教程](https://blog.csdn.net/xyendjsj/article/details/135921460?spm=1001.2014.3001.5501)

跟着上面的教程一步一步来即可，不行你不会，如果中间有任何问题欢迎在这个网站，或者 CSDN 上，亦或者是微信上向我提问。觉得项目还不错的话，请给我点个 Star 呗，谢谢！
