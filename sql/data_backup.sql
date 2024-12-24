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


INSERT INTO hjj.blog (title,coverImage,images,content,userId,tags,viewNum,likeNum,starNum,commentNum,createTime,updateTime,isDelete) VALUES
                                                                                                                                         ('EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/d7938aad-fba2-4cb0-ade1-383fbcb5e3d3.png','[]','## 前言

![fdb699e4771d377df0f42acaa44488c.png](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/01fbcd03-af69-4588-9d9d-155275ce4ebc.png)
大家好，我是雪荷。之前有一篇博客（[EasyExcel 初使用—— Java 实现读取 Excel 功能\_java easyexcel.read-CSDN博客](https://blog.csdn.net/xyendjsj/article/details/138135290?spm=1001.2014.3001.5501 "EasyExcel 初使用—— Java 实现读取 Excel 功能_java easyexcel.read-CSDN博客")）介绍了 Java 如何读取 Excel 表格，那么此篇博客就和大家介绍下 Java 如何利用 EasyExcel 写入 Excel。

![e229535851d178d8c0bd4ce34d6e3d86_1.jpg](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/0a34b79e-6a72-4ca3-9769-586c27aa9218.jpg)
![e229535851d178d8c0bd4ce34d6e3d86_1.jpg](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/a3eb982e-df00-43e5-8764-dd636fbdf35c.jpg)
EasyExcel 官方网址：[EasyExcel官方文档 - 基于Java的Excel处理工具 | Easy Excel](https://easyexcel.opensource.alibaba.com/ "EasyExcel官方文档 - 基于Java的Excel处理工具 | Easy Excel")

### 前置准备

#### 引入依赖

先创建一个 Spring Boot 工程，随后加入 EasyExcel 和 Lombok 依赖。

``````XML
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>easyexcel</artifactId>
            <version>3.1.1</version>
        </dependency>
``````

#### 设置一个写对象，即写入 Excel 的对象

``````java
@Data
@AllArgsConstructor
public class SaleData implements Serializable {

    @ExcelProperty("订单号")
    private Long id;

    @ExcelProperty("品种")
    private String name;

    @ExcelProperty("价格")
    private BigDecimal price;

    @ExcelProperty("数量")
    private Integer totalNum;

    @ExcelProperty("用户ID")
    private Long userId;

    @ExcelProperty("交易时间")
    private String datetime;
}
``````

### 写入 Excel

#### 最简单的写入 Excel 方式

> 看代码也能看出来，首先有一个写入 Excel 的对象 List，其次指定 Excel 表格的位置和表单名称，最后进行调用 write 方法将 list 写入 Excel 即可。

``````java
    @Test
    public void simpleWriteDataToExcel() {
        List<SaleData> list = new ArrayList<>();
        list.add(new SaleData(1L, "苹果", new BigDecimal("10.00"), 10, 1L,"2024-01-01 13:00:00"));
        list.add(new SaleData(2L, "梨子", new BigDecimal("12.00"), 10, 1L,"2025-01-01 13:00:00"));
        list.add(new SaleData(3L, "西瓜", new BigDecimal("5.00"), 10, 1L,"2024-03-01 13:00:00"));
        list.add(new SaleData(4L, "香蕉", new BigDecimal("7.00"), 10, 1L,"2024-02-01 13:00:00"));

        String fileName = "D:\\\\projects\\\\plant-sys-main\\\\src\\\\main\\\\resources\\\\销售数据1.xlsx";
        //写入excel
        EasyExcel.write(fileName, SaleData.class)
                .sheet("Sheet1")
                .doWrite(list);
    }
``````

写入效果：

![](https://i-blog.csdnimg.cn/direct/ed31cbbc66794f0f864279c46d45d2b0.png)

如果 EasyExcel.write() 方法不带上 SaleData.class，就表示未指定表头，那这样写入 Excel 时是直接写入的，但没有表头。

没指明表头的写入效果：

![](https://i-blog.csdnimg.cn/direct/22401d21567e470e9cf620fe95091773.png)

#### 根据参数写入指定列

> 与上一种方法类似，无非多了两个 Set，一个需要写入的 Set，另一个不需要写入的 Set。

``````java
    @Test
    public void excludeOrIncludeWrite() {
        List<SaleData> list = new ArrayList<>();
        list.add(new SaleData(1L, "苹果", new BigDecimal("10.00"), 10, 1L,"2024-01-01 13:00:00"));
        list.add(new SaleData(2L, "梨子", new BigDecimal("12.00"), 10, 1L,"2025-01-01 13:00:00"));
        list.add(new SaleData(3L, "西瓜", new BigDecimal("5.00"), 10, 1L,"2024-03-01 13:00:00"));
        list.add(new SaleData(4L, "香蕉", new BigDecimal("7.00"), 10, 1L,"2024-02-01 13:00:00"));

        String fileName = "D:\\\\projects\\\\plant-sys-main\\\\src\\\\main\\\\resources\\\\销售数据2.xlsx";
        // 无需写入的 Set 集合
        Set<String> excludSet = new HashSet<>();
        excludSet.add("id");

        // 需要写入的 Set 集合
        Set<String> includeSet = new HashSet<>();
        includeSet.add("userId");
        // 指定两个 Set，并写入 Excel
        EasyExcel.write(fileName, SaleDetailVO.class).includeColumnFieldNames(includeSet)
                .excludeColumnFiledNames(excludSet).sheet("Sheet1").doWrite(list);
    }
``````

 在此段代码中，我指定了 id 不需要写入 Excel，userId 需要写入 Excel。最终可以看出，只有 userId 被写入 Excel 中。

![](https://i-blog.csdnimg.cn/direct/e99a8328e7244b5ba5cabbc0a1daba4e.png)

#### 写入指定的列

可以利用 ExcelProperty 注解的 index 属性，指定某些字段写到 Excel 的某一列。

``````java
@Data
@AllArgsConstructor
public class SaleData implements Serializable {

    @ExcelProperty(value = "订单号", index = 1)
    private Long id;

    @ExcelProperty(value = "品种", index = 2)
    private String name;

    @ExcelProperty(value = "价格", index = 3)
    private BigDecimal price;

    @ExcelProperty(value = "数量", index = 4)
    private Integer totalNum;

    @ExcelProperty(value = "交易对象", index = 5)
    private Long userId;

    @ExcelProperty(value = "交易时间", index = 6)
    private String datetime;
}
``````

代码很简单我就不多解释了，相较于最简单的写入 Excel 方式，唯一不同的就是加了写入对象的 index 属性。 

``````java
    @Test
    public void writeSpecificColumn() {
        List<SaleData> list = new ArrayList<>();
        list.add(new SaleData(1L, "苹果", new BigDecimal("10.00"), 10, 1L,"2024-01-01 13:00:00"));
        list.add(new SaleData(2L, "梨子", new BigDecimal("12.00"), 10, 1L,"2025-01-01 13:00:00"));
        list.add(new SaleData(3L, "西瓜", new BigDecimal("5.00"), 10, 1L,"2024-03-01 13:00:00"));
        list.add(new SaleData(4L, "香蕉", new BigDecimal("7.00"), 10, 1L,"2024-02-01 13:00:00"));

        String fileName = "D:\\\\projects\\\\plant-sys-main\\\\src\\\\main\\\\resources\\\\销售数据3.xlsx";
        EasyExcel.write(fileName, SaleDetailVO.class).sheet("Sheet1").doWrite(list);
    }
``````

可以看出来，第一列被没有被写入，因为我的 index 是从 1 开始的，表格的第一列 index（索引） 为 0。最终效果如下：

![](https://i-blog.csdnimg.cn/direct/b24cb1f6300445b8b95dadcbe27d5a5b.png)

####  复杂的头写入

这个复杂头写入不太好解释，所以大家直接看代码和写入效果就懂了。

``````java
@Data
@AllArgsConstructor
public class SaleData implements Serializable {

    @ExcelProperty({"主标题", "订单号"})
    private Long id;

    @ExcelProperty({"主标题", "品种"})
    private String name;

    @ExcelProperty({"主标题", "价格"})
    private BigDecimal price;

    @ExcelProperty({"主标题", "数量"})
    private Integer totalNum;

    @ExcelProperty({"主标题", "交易对象"})
    private Long userId;

    @ExcelProperty({"主标题", "交易时间"})
    private String datetime;
}
``````

``````java
    @Test
    public void complexHeadWrite() {
        QueryWrapper<Sale> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderBy(true, false, "createTime");
        List<Sale> saleList = saleService.list(queryWrapper);
        List<SaleDetailVO> saleDetailVOList = saleList.stream().map(sale -> {
                    SaleDetailVO saleDetailVO = new SaleDetailVO();
                    saleDetailVO.setId(sale.getId());
                    Date createTime = sale.getCreateTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String datetime = dateFormat.format(createTime);
                    saleDetailVO.setDatetime(datetime);
                    saleDetailVO.setTotalNum(sale.getTotalNum());
                    saleDetailVO.setUserId(sale.getUserId());
                    Plant plant = plantService.getById(sale.getPlantId());
                    saleDetailVO.setPrice(plant.getPrice());
                    saleDetailVO.setName(plant.getName());
                    return saleDetailVO;
                })
                .collect(Collectors.toList());

        String fileName = "D:\\\\projects\\\\plant-sys-main\\\\src\\\\main\\\\resources\\\\销售数据4.xlsx";
        EasyExcel.write(fileName, SaleData.class).sheet("Sheet1").doWrite(saleDetailVOList);
    }
``````

运行效果：

![](https://i-blog.csdnimg.cn/direct/e14d1fdbd60a4daab2f3cf6256ce5a4b.png)

#### 重复多次写入

> 前几种写入方式都只能在数据量不大的情况下才可使用，这个数量是指在 5000 一下，超过这个量级我们就得换种方式了，在此我就介绍一下官方演示的重复多次写入 Excel 方式。

老实说看代码并不复杂，也就是加了个 for 循环罢了，对于重复多次写入，我们不仅可以写入不同的表单（Sheet）还能写入不同的对象，甚至写入不同的数据（即写入不同的 list）。

利用这个方式我们可以在循环中调整不同的分页参数，然后分页查询数据库并把查到的数据写入 Excel 中，是不是很巧妙啊，要是一次性写入的话内存会溢出的。

可能有人会问怎么写入不同的表单（Sheet）和对象，我这里提供个思路，你可以创建一个表单或者对象集合，可以是 List 也可以是 Map，然后根据循环的 i 去获取对应的元素嘛。

``````java
    @Test
    public void repeatedWrite() {
        List<SaleData> list = new ArrayList<>();
        list.add(new SaleData(1L, "苹果", new BigDecimal("10.00"), 10, 1L,"2024-01-01 13:00:00"));
        list.add(new SaleData(2L, "梨子", new BigDecimal("12.00"), 10, 1L,"2025-01-01 13:00:00"));
        list.add(new SaleData(3L, "西瓜", new BigDecimal("5.00"), 10, 1L,"2024-03-01 13:00:00"));
        list.add(new SaleData(4L, "香蕉", new BigDecimal("7.00"), 10, 1L,"2024-02-01 13:00:00"));

        String fileName = "D:\\\\projects\\\\plant-sys-main\\\\src\\\\main\\\\resources\\\\销售数据5.xlsx";

        // 写入不同对象，不同 sheet
        try (ExcelWriter excelWriter2 = EasyExcel.write(fileName, SaleData.class).build()) {
            for (int i = 0; i < 5; i++) {
                // 这里每次都要创建writeSheet，这里注意必须指定writerSheet，而且sheetNo必须不一样。这里注意DemoData.class 可以每次都变，我这里为了方便 所以用的同一个class
                WriteSheet writeSheet = EasyExcel.writerSheet(i, "模板" + i).head(SaleData.class).build();
                // 分页去数据库查询数据，这里可以去数据库查询每一页的数据
                excelWriter2.write(list, writeSheet);
            }
        }
    }
``````

写入效果：

![image-20240726153623063](https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/img/20240726153623.png)

### Java 读取 Excel

想要了解 Java 如何实现读取 Excel 的小伙伴可以看看这篇文章哈——[EasyExcel 初使用—— Java 实现读取 Excel 功能\_java easyexcel.read-CSDN博客](https://blog.csdn.net/xyendjsj/article/details/138135290?spm=1001.2014.3001.5501 "EasyExcel 初使用—— Java 实现读取 Excel 功能_java easyexcel.read-CSDN博客")。

在学习的过程中有任何问题都可以问我，如果你觉得还不错的话就给我点个赞吧。

### 开源项目

前一阵子，我开源了一个既好玩又能学到东西的项目，有兴趣的同学可以移步学习和体验哈——[我终于有我的开源项目了！！！-CSDN博客](https://blog.csdn.net/xyendjsj/article/details/139564041?spm=1001.2014.3001.5501 "我终于有我的开源项目了！！！-CSDN博客")。',1,'["Java","EasyExcel"]',41,4,4,1,'2024-08-13 14:50:00','2024-09-24 22:40:14',0),
                                                                                                                                         ('Spring Boot 引入 Guava Retry 实现重试机制','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/89140b11-5c54-4c77-985d-2cb2d2c89250.png','[]','## 为什么要用重试机制

在如今的系统开发中，为了保证接口调用的稳定性和数据的一致性常常会引入许多第三方的库。就拿缓存和数据库一致性这个问题来说，就有很多的实现方案，如先更新数据库再删除缓存、先更新缓存再更新数据库，又或者是异步缓存写入。然而某些场景下出现更新数据库成功，但删除缓存失败，又或者是更新缓存失败但更新数据库失败了。因此为保证缓存数据库的一致性，我们可以尝试引入重试机制来实现，当数据库更新成功后去删除缓存，如果删除失败就进行重试。

### 引入 Guava Retry 库实现重试机制

### 引入依赖

在引入依赖前，先创建一个 Spring Boot 工程，这里我就不演示了。

``````XML
        <dependency>
            <groupId>com.github.rholder</groupId>
            <artifactId>guava-retrying</artifactId>
            <version>1.0.6</version>
        </dependency>
``````

### 编写重试器的配置类

常见的重试策略

1.  重试间隔时间，等待进入下一次重试的时间
2.  重试的次数，超过设定次数就停止重试
3.  重试的时间限制，规定重试时的时间限制，若重试过程中超过了这个时间就会抛出异常
4.  重试时机，表明在什么场景需要重试，是抛出异常、还是返回 true，还是返回 false 呢。

在 config 包下编写一个重试器的配置类，设置重试的策略，当我们想在项目中使用时直接注入其依赖即可。

``````java
@Configuration
public class RetryConfig {

    @Bean
    public Retryer<Boolean> retryer() {
        return RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 设置出现 Exception 异常就重试
                .retryIfResult(Predicates.equalTo(false)) // 设置结果为 false 才重试
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS)) // 设置每次重试间隔为 2s
                .withStopStrategy(StopStrategies.stopAfterAttempt(2)) // 设置重试次数为 2 次，超过 2 次就停止
                .withAttemptTimeLimiter(AttemptTimeLimiters.fixedTimeLimit(3, TimeUnit.SECONDS)) // 设置每次重试的时间限制为 3s
                .build();
    }
}
``````

### 直接引入 Bean 进行重试

创建一个 SpringBoot 的测试类，再注入入 Retryer 的 Bean。我写了一个方法来判断生成的随机数是否大于 10，大于就返回 true，否则返回 false，同时还打印重试的次数，如果第一次调这个函数就返回 true 的话就只重试了一次，因为每调用一次算一次重试，即 i == 1。

``````java
@SpringBootTest
public class RetryerTest {

    @Resource
    private Retryer<Boolean> retryer;

    private int i = 1;

    @Test
    public void test() {
        try {
            retryer.call(() -> isGreaterThan10());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (RetryException e) { // 超过指定的重试次数、超过的最大等待时间或超过重试时间就会抛这个异常
            throw new RuntimeException(e);
        }
    }

    // 判断生成的随机数是否大于 10
    private boolean isGreaterThan10() {
        Random random = new Random();
        System.out.println("重试次数：" + i++);
        int num = random.nextInt();
        System.out.println(num);
        return num > 10;
    }

}
``````

从测试结果就可以看出来，重试次数为 1，因为第一次调用就返回了 true。

![](https://i-blog.csdnimg.cn/direct/735e0d861e6f4c1e9d0ae1d8d8178e2a.png)

再改造一下，看看它超过了规定的重试次数（这里我规定的是两次）会发生什么？

``````java
public class RetryerTest {

    @Resource
    private Retryer<Boolean> retryer;

    private int i = 1;

    @Test
    public void test() {
        try {
            retryer.call(() -> isGreaterThan1000000000());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (RetryException e) { // 超过指定的重试次数、超过的最大等待时间或超过重试时间就会抛这个异常
            throw new RuntimeException(e);
        }
    }

    // 判断生成的随机数是否大于 1000000000
    private boolean isGreaterThan1000000000() {
        Random random = new Random();
        System.out.println("重试次数：" + i++);
        int num = random.nextInt();
        System.out.println(num);
        return num > 1000000000;
    }

}
``````

从结果我们看出，调用两次后就抛出异常了，这个异常就是 RetryException，当超过指定的重试次数、超过的最大等待时间或超过重试时间就会抛这个异常。

> 当然这个方法写的不严谨，因为生成的数是随机的，如果两次生成的数都大于 1000000000，就不会抛出异常，要是第一次生成的数就大于 1000000000，那就只重试了一次。当然意思到了就行，具体的话就看你业务是啥样了，然后你再去调整就好了。

 ![](https://i-blog.csdnimg.cn/direct/bc39ab0438a04e86bbdadec5ad0f2aa1.png)

## 开源项目

前一阵子，我开源了一个既好玩又能学到东西的项目，有兴趣的同学可以移步学习和体验哈——[我终于有我的开源项目了！！！-CSDN博客](https://blog.csdn.net/xyendjsj/article/details/139564041?spm=1001.2014.3001.5501 "我终于有我的开源项目了！！！-CSDN博客")。',1,'["Java","Guava"]',35,5,2,2,'2024-08-13 21:02:30','2024-09-25 18:46:02',0),
                                                                                                                                         ('好厉害','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/5c639349-2a61-456e-b2c8-9587bdd5093f.png','[]','111',5948,'[]',14,0,0,1,'2024-08-16 11:30:39','2024-09-24 21:46:05',0),
                                                                                                                                         ('C1own','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/3655f9a7-81d0-4c48-8ed1-0a2ee04d67ef.jpg','[]','C1own',1,'[]',14,1,0,0,'2024-08-16 13:09:50','2024-09-24 22:23:36',0),
                                                                                                                                         ('厚米系统🫠','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/4302c45b-1dd2-4b0f-9822-3ff84d5ea828.jpg','[]','对于这个厚米系统，我不敢苟同。 我个人认为这个意大利面就应该拌42号混凝土。因为这个螺丝钉的长度，它很容易会直接影响到挖掘机的扭距，你往里砸的时候，一瞬间它就会产生大量的高能蛋白，俗称UFO。会严重影响经济的发展。 照你这么说，炸鸡块要用92#汽油，毕竟我们无法用光学透镜探测苏格拉底，如果二氧化氢持续侵蚀这个机床组件，那么我们早晚要在斐波那契曲线上安装一个胶原蛋白，否则我们将无法改变蜜雪冰城与阿尔别克的叠加状态，因为众所周知爱吃鸡摩人在捕鲲的时候往往需要用氢的同位素当做诱饵，但是原子弹的新鲜程度又会直接影响到我国东南部的季风和洋流，所以说在西伯利亚地区开设农学院显然是不合理的。',6046,'["java","python"]',13,2,2,2,'2024-09-14 10:30:37','2024-09-26 19:43:28',0),
                                                                                                                                         ('12e1','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/d4ad9b86-e70d-4189-88c6-85689f37db38.png','[]','[w1w2w12e2](url)',6073,'[]',3,1,0,0,'2024-09-24 16:42:04','2024-09-25 18:46:10',0);

INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (1,5862,'我',1,'2024-07-20 01:15:03','2024-07-20 01:15:03',NULL,0),
                                                                                           (1,2,'11',1,'2024-07-20 21:03:17','2024-07-20 21:03:17',NULL,0),
                                                                                           (1,5882,'1',1,'2024-07-23 21:14:35','2024-07-23 21:14:34',NULL,0),
                                                                                           (5887,1,'444',1,'2024-07-25 09:41:39','2024-07-25 09:41:38',NULL,0),
                                                                                           (5888,6,'马冬梅',1,'2024-07-25 14:11:49','2024-07-25 14:11:49',NULL,0),
                                                                                           (1,5856,'1',1,'2024-07-27 16:17:46','2024-07-27 16:17:46',NULL,0),
                                                                                           (5894,7,'hi',1,'2024-07-29 16:56:05','2024-07-29 16:56:05',NULL,0),
                                                                                           (5899,6,'阿什顿',1,'2024-08-03 18:00:56','2024-08-03 18:00:56',NULL,0),
                                                                                           (1,5862,'fdsaf',1,'2024-08-05 19:06:11','2024-08-05 19:06:10',NULL,0),
                                                                                           (1,5862,'fdsaf ',1,'2024-08-05 19:06:13','2024-08-05 19:06:12',NULL,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (1,7,'fdsafdsa ',1,'2024-08-05 19:06:21','2024-08-05 19:06:20',NULL,0),
                                                                                           (5915,1,'hello',1,'2024-08-06 20:13:49','2024-08-06 20:13:48',NULL,0),
                                                                                           (1,NULL,'各位好',3,'2024-08-13 16:40:47','2024-08-13 16:40:47',NULL,0),
                                                                                           (5921,1,'1',1,'2024-08-13 22:19:20','2024-08-13 22:19:20',NULL,0),
                                                                                           (5924,2,'哈哈哈哈哈哈哈哈哈哈哈哈',1,'2024-08-14 07:20:54','2024-08-14 07:20:53',NULL,0),
                                                                                           (5927,NULL,'好',3,'2024-08-14 11:02:30','2024-08-14 11:02:30',NULL,0),
                                                                                           (5930,NULL,'111',2,'2024-08-14 20:36:28','2024-08-14 20:36:28',18,0),
                                                                                           (5921,NULL,'222',2,'2024-08-14 20:36:56','2024-08-14 20:36:56',18,0),
                                                                                           (5944,NULL,'？',2,'2024-08-16 11:31:06','2024-08-16 11:31:05',20,0),
                                                                                           (5955,NULL,'0.0',2,'2024-08-16 11:44:19','2024-08-16 11:44:19',20,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (5955,NULL,'嘿嘿',2,'2024-08-16 11:45:48','2024-08-16 11:45:48',16,0),
                                                                                           (5956,NULL,'？',2,'2024-08-16 11:48:56','2024-08-16 11:48:56',20,0),
                                                                                           (5974,NULL,'有人吗',2,'2024-08-16 17:06:02','2024-08-16 17:06:02',21,0),
                                                                                           (1,NULL,'有的，你好',2,'2024-08-16 17:09:30','2024-08-16 17:09:30',21,0),
                                                                                           (5974,NULL,'123',2,'2024-08-16 17:11:45','2024-08-16 17:11:44',21,0),
                                                                                           (5991,NULL,'大家好',3,'2024-08-18 08:49:56','2024-08-18 08:49:55',NULL,0),
                                                                                           (5936,NULL,'哈哈哈',2,'2024-08-18 21:25:04','2024-08-18 21:25:04',22,0),
                                                                                           (5993,NULL,'99',3,'2024-08-19 07:08:30','2024-08-19 07:08:29',NULL,0),
                                                                                           (5993,NULL,'222',2,'2024-08-19 07:10:27','2024-08-19 07:10:26',22,0),
                                                                                           (5996,NULL,'你们好你们好',3,'2024-08-19 23:49:22','2024-08-19 23:49:22',NULL,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (5996,5878,'你好',1,'2024-08-19 23:51:57','2024-08-19 23:51:57',NULL,0),
                                                                                           (5996,5878,'你好',1,'2024-08-20 00:02:03','2024-08-20 00:02:02',NULL,0),
                                                                                           (5992,NULL,'hello ',2,'2024-08-20 19:02:23','2024-08-20 19:02:22',23,0),
                                                                                           (5992,NULL,'togenashi togeari',2,'2024-08-20 19:02:28','2024-08-20 19:02:28',23,0),
                                                                                           (6004,NULL,'11111111',3,'2024-08-23 14:09:25','2024-08-23 14:09:24',NULL,0),
                                                                                           (5936,NULL,'11',3,'2024-08-24 15:17:20','2024-08-24 15:17:20',NULL,0),
                                                                                           (5936,NULL,'hhh',3,'2024-08-27 10:00:51','2024-08-27 10:00:50',NULL,0),
                                                                                           (6008,8,'哈哈哈',1,'2024-08-29 14:47:12','2024-08-29 14:47:11',NULL,0),
                                                                                           (6011,7,'大一',1,'2024-09-01 23:21:39','2024-09-01 23:21:38',NULL,0),
                                                                                           (6011,NULL,'44',3,'2024-09-01 23:34:06','2024-09-01 23:34:06',NULL,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (6014,NULL,'??',2,'2024-09-04 10:29:28','2024-09-04 10:29:27',24,0),
                                                                                           (6016,NULL,'111',3,'2024-09-04 21:55:49','2024-09-04 21:55:49',NULL,0),
                                                                                           (6017,NULL,'滴滴',3,'2024-09-05 10:24:02','2024-09-05 10:24:02',NULL,0),
                                                                                           (6019,NULL,'111',3,'2024-09-05 15:11:59','2024-09-05 15:11:59',NULL,0),
                                                                                           (6019,NULL,'11',2,'2024-09-05 15:13:26','2024-09-05 15:13:26',26,0),
                                                                                           (6023,NULL,'aaa',3,'2024-09-08 15:20:41','2024-09-08 15:20:41',NULL,0),
                                                                                           (6027,6026,'你好',1,'2024-09-09 10:27:36','2024-09-09 10:27:35',NULL,0),
                                                                                           (1,NULL,'叭叭叭',3,'2024-09-10 00:01:03','2024-09-10 00:01:02',NULL,0),
                                                                                           (6029,NULL,'牛的',3,'2024-09-10 14:56:08','2024-09-10 14:56:08',NULL,0),
                                                                                           (6031,NULL,'1',3,'2024-09-10 17:17:44','2024-09-10 17:17:44',NULL,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (6031,1,'1',1,'2024-09-10 17:18:03','2024-09-10 17:18:03',NULL,0),
                                                                                           (6036,NULL,'6的',3,'2024-09-12 12:55:56','2024-09-12 12:55:56',NULL,0),
                                                                                           (6041,7,'你好',1,'2024-09-13 10:32:55','2024-09-13 10:32:55',NULL,0),
                                                                                           (6045,7,'哈喽',1,'2024-09-13 16:54:41','2024-09-13 16:54:40',NULL,0),
                                                                                           (6045,7,'哈哈楼',1,'2024-09-13 16:54:45','2024-09-13 16:54:45',NULL,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:12','2024-09-14 10:25:12',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:13','2024-09-14 10:25:12',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:13','2024-09-14 10:25:13',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:14','2024-09-14 10:25:13',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:14','2024-09-14 10:25:13',27,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:14','2024-09-14 10:25:14',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:14','2024-09-14 10:25:14',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:15','2024-09-14 10:25:14',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:15','2024-09-14 10:25:14',27,0),
                                                                                           (6046,NULL,'passion',2,'2024-09-14 10:25:15','2024-09-14 10:25:15',27,0),
                                                                                           (6046,NULL,'passion',3,'2024-09-14 10:27:23','2024-09-14 10:27:23',NULL,0),
                                                                                           (6046,NULL,'passion',3,'2024-09-14 10:27:23','2024-09-14 10:27:23',NULL,0),
                                                                                           (6046,NULL,'passion',3,'2024-09-14 10:27:24','2024-09-14 10:27:23',NULL,0),
                                                                                           (6046,NULL,'passion',3,'2024-09-14 10:27:24','2024-09-14 10:27:23',NULL,0),
                                                                                           (6046,NULL,'passion',3,'2024-09-14 10:27:24','2024-09-14 10:27:24',NULL,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (6046,NULL,'passion',3,'2024-09-14 10:27:24','2024-09-14 10:27:24',NULL,0),
                                                                                           (6047,NULL,'12',3,'2024-09-14 10:46:10','2024-09-14 10:46:10',NULL,0),
                                                                                           (6047,7,'222',1,'2024-09-14 10:46:23','2024-09-14 10:46:23',NULL,0),
                                                                                           (6052,NULL,'1',3,'2024-09-15 10:01:57','2024-09-15 10:01:57',NULL,0),
                                                                                           (6053,NULL,'1',2,'2024-09-15 10:05:50','2024-09-15 10:05:50',27,0),
                                                                                           (6052,NULL,'1',2,'2024-09-15 10:06:54','2024-09-15 10:06:54',27,0),
                                                                                           (5936,7,'这是神马',1,'2024-09-23 15:31:18','2024-09-23 15:31:17',NULL,0),
                                                                                           (5949,NULL,'1234',3,'2024-09-23 18:15:22','2024-09-23 18:15:21',NULL,0),
                                                                                           (5949,NULL,'3',3,'2024-09-23 18:15:29','2024-09-23 18:15:29',NULL,0),
                                                                                           (5949,NULL,'1234',2,'2024-09-23 18:16:01','2024-09-23 18:16:00',29,0);
INSERT INTO hjj.chat (fromId,toId,text,chatType,createTime,updateTime,teamId,isDelete) VALUES
                                                                                           (5949,NULL,'11',2,'2024-09-23 18:16:58','2024-09-23 18:16:58',29,0),
                                                                                           (5949,7,'1',1,'2024-09-23 21:28:45','2024-09-23 21:28:44',NULL,0),
                                                                                           (6069,NULL,'1',3,'2024-09-23 23:33:49','2024-09-23 23:33:48',NULL,0),
                                                                                           (6071,NULL,'qwe',3,'2024-09-24 09:50:48','2024-09-24 09:50:47',NULL,0),
                                                                                           (6073,NULL,'q',2,'2024-09-24 16:42:53','2024-09-24 16:42:53',29,0),
                                                                                           (5996,NULL,'trtre',2,'2024-09-24 22:21:08','2024-09-24 22:21:08',28,0),
                                                                                           (6075,7,'qwerqwer',1,'2024-09-25 09:35:04','2024-09-25 09:35:03',NULL,0);

INSERT INTO hjj.comment (userId,blogId,text,createTime,isDelete) VALUES
                                                                     (5921,2,'1','2024-08-13 22:21:26',0),
                                                                     (5936,1,'111','2024-08-23 18:22:09',0),
                                                                     (6045,2,'牛逼','2024-09-13 16:53:54',0),
                                                                     (6048,5,'没毛病','2024-09-14 17:10:45',0),
                                                                     (5996,3,'哈哈哈哈','2024-09-24 21:46:05',0),
                                                                     (5996,5,'真的酷','2024-09-24 21:54:24',0);



INSERT INTO hjj.feedback (userId,rate,advice,createTime,isDeleted) VALUES
                                                                       (1,5.0,'牛逼','2024-08-13 16:58:46',0),
                                                                       (5943,4.0,'好好','2024-08-16 11:26:06',0),
                                                                       (5945,3.0,'呃呃呃呃','2024-08-16 11:28:58',0),
                                                                       (5953,5.0,'nb','2024-08-16 11:42:02',0),
                                                                       (5996,4.5,'高浮雕高低杠','2024-08-20 00:07:56',0),
                                                                       (5986,4.0,'11','2024-08-29 02:36:25',0),
                                                                       (5936,5.0,'太酷啦','2024-09-04 15:22:19',0),
                                                                       (6017,5.0,'开源精神好','2024-09-05 10:27:22',0),
                                                                       (6031,5.0,'155','2024-09-10 17:18:39',0);


INSERT INTO hjj.follow (followeeId,followerId,createTime,updateTime,isDelete) VALUES
                                                                                  (5948,5943,'2024-08-16 11:36:53','2024-08-16 11:36:53',1),
                                                                                  (5948,5943,'2024-08-16 11:36:56','2024-08-16 11:36:56',0),
                                                                                  (1,5953,'2024-08-16 11:42:42','2024-08-16 11:42:42',0),
                                                                                  (1,5956,'2024-08-16 11:50:27','2024-08-16 11:50:28',1),
                                                                                  (1,5956,'2024-08-16 11:50:29','2024-08-16 11:50:29',0),
                                                                                  (1,5966,'2024-08-16 12:49:35','2024-08-16 12:49:35',1),
                                                                                  (1,5966,'2024-08-16 12:49:36','2024-08-16 12:49:36',1),
                                                                                  (1,5966,'2024-08-16 12:49:36','2024-08-16 12:49:39',1),
                                                                                  (1,5975,'2024-08-16 17:10:18','2024-08-16 17:10:23',1),
                                                                                  (1,5975,'2024-08-16 17:10:24','2024-08-16 17:10:25',1);
INSERT INTO hjj.follow (followeeId,followerId,createTime,updateTime,isDelete) VALUES
                                                                                  (1,5975,'2024-08-16 17:16:29','2024-08-16 17:16:37',1),
                                                                                  (1,5975,'2024-08-16 17:16:38','2024-08-16 17:16:38',0),
                                                                                  (1,5992,'2024-08-19 23:57:18','2024-08-19 23:57:18',0),
                                                                                  (1,6011,'2024-09-01 23:22:12','2024-09-01 23:22:12',0),
                                                                                  (6053,6052,'2024-09-15 10:05:55','2024-09-15 10:05:55',0),
                                                                                  (6052,6053,'2024-09-15 10:08:46','2024-09-15 10:08:46',0),
                                                                                  (6046,5936,'2024-09-19 11:39:57','2024-09-19 11:39:57',0),
                                                                                  (1,5949,'2024-09-23 21:28:31','2024-09-23 21:28:31',0),
                                                                                  (1,6073,'2024-09-24 16:43:20','2024-09-24 16:43:20',0),
                                                                                  (1,5996,'2024-09-24 22:15:27','2024-09-24 22:15:27',0);



INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (1,5862,'2024-07-17 22:34:23','2024-07-17 22:34:23',0),
                                                                            (5862,1,'2024-07-17 22:34:23','2024-07-17 22:34:23',0),
                                                                            (1,2,'2024-07-17 22:34:26','2024-07-17 22:34:26',0),
                                                                            (2,1,'2024-07-17 22:34:26','2024-07-17 22:34:26',0),
                                                                            (1,6,'2024-07-17 22:34:33','2024-07-17 22:34:33',0),
                                                                            (6,1,'2024-07-17 22:34:33','2024-07-17 22:34:33',0),
                                                                            (5881,5856,'2024-07-20 00:54:11','2024-07-20 00:54:11',0),
                                                                            (5856,5881,'2024-07-20 00:54:11','2024-07-20 00:54:11',0),
                                                                            (5882,1,'2024-07-20 15:30:52','2024-07-20 15:30:52',0),
                                                                            (1,5882,'2024-07-20 15:30:52','2024-07-20 15:30:52',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (1,7,'2024-07-20 21:03:07','2024-07-20 21:03:07',0),
                                                                            (7,1,'2024-07-20 21:03:07','2024-07-20 21:03:07',0),
                                                                            (1,5,'2024-07-21 10:21:51','2024-07-21 10:21:51',0),
                                                                            (5,1,'2024-07-21 10:21:51','2024-07-21 10:21:51',0),
                                                                            (5886,5879,'2024-07-23 14:21:28','2024-07-23 14:21:28',0),
                                                                            (5879,5886,'2024-07-23 14:21:28','2024-07-23 14:21:28',0),
                                                                            (5886,7,'2024-07-23 14:21:36','2024-07-23 14:21:36',0),
                                                                            (7,5886,'2024-07-23 14:21:36','2024-07-23 14:21:36',0),
                                                                            (5887,1,'2024-07-25 09:41:06','2024-07-25 09:41:06',0),
                                                                            (1,5887,'2024-07-25 09:41:06','2024-07-25 09:41:06',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5888,6,'2024-07-25 14:09:40','2024-07-25 14:09:40',0),
                                                                            (6,5888,'2024-07-25 14:09:40','2024-07-25 14:09:40',0),
                                                                            (5888,5,'2024-07-25 14:09:50','2024-07-25 14:09:50',0),
                                                                            (5,5888,'2024-07-25 14:09:50','2024-07-25 14:09:50',0),
                                                                            (1,5856,'2024-07-27 16:17:34','2024-07-27 16:17:34',0),
                                                                            (5856,1,'2024-07-27 16:17:34','2024-07-27 16:17:34',0),
                                                                            (5894,5861,'2024-07-29 16:55:40','2024-07-29 16:55:40',0),
                                                                            (5861,5894,'2024-07-29 16:55:40','2024-07-29 16:55:40',0),
                                                                            (5894,7,'2024-07-29 16:55:50','2024-07-29 16:55:50',0),
                                                                            (7,5894,'2024-07-29 16:55:50','2024-07-29 16:55:50',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5899,6,'2024-08-03 18:00:41','2024-08-03 18:00:41',0),
                                                                            (6,5899,'2024-08-03 18:00:41','2024-08-03 18:00:41',0),
                                                                            (1,5859,'2024-08-06 18:44:18','2024-08-06 18:44:18',0),
                                                                            (5859,1,'2024-08-06 18:44:18','2024-08-06 18:44:18',0),
                                                                            (5915,1,'2024-08-06 20:13:32','2024-08-06 20:13:32',0),
                                                                            (1,5915,'2024-08-06 20:13:32','2024-08-06 20:13:32',0),
                                                                            (5921,1,'2024-08-13 22:19:04','2024-08-13 22:19:04',0),
                                                                            (1,5921,'2024-08-13 22:19:04','2024-08-13 22:19:04',0),
                                                                            (5922,8,'2024-08-13 22:51:32','2024-08-13 22:51:32',0),
                                                                            (8,5922,'2024-08-13 22:51:32','2024-08-13 22:51:32',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5923,1,'2024-08-14 00:21:29','2024-08-14 00:21:29',0),
                                                                            (1,5923,'2024-08-14 00:21:29','2024-08-14 00:21:29',0),
                                                                            (5924,2,'2024-08-14 07:19:02','2024-08-14 07:19:02',0),
                                                                            (2,5924,'2024-08-14 07:19:02','2024-08-14 07:19:02',0),
                                                                            (1,9,'2024-08-14 07:24:45','2024-08-14 07:24:45',0),
                                                                            (9,1,'2024-08-14 07:24:45','2024-08-14 07:24:45',0),
                                                                            (5929,6,'2024-08-14 20:00:54','2024-08-14 20:00:54',0),
                                                                            (6,5929,'2024-08-14 20:00:54','2024-08-14 20:00:54',0),
                                                                            (5934,1,'2024-08-15 17:26:00','2024-08-15 17:26:00',0),
                                                                            (1,5934,'2024-08-15 17:26:00','2024-08-15 17:26:00',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5936,5879,'2024-08-15 22:28:17','2024-08-15 22:28:17',0),
                                                                            (5879,5936,'2024-08-15 22:28:17','2024-08-15 22:28:17',0),
                                                                            (5937,1,'2024-08-16 11:20:25','2024-08-16 11:20:25',0),
                                                                            (1,5937,'2024-08-16 11:20:25','2024-08-16 11:20:25',0),
                                                                            (5938,1,'2024-08-16 11:21:07','2024-08-16 11:21:07',0),
                                                                            (1,5938,'2024-08-16 11:21:07','2024-08-16 11:21:07',0),
                                                                            (5943,1,'2024-08-16 11:25:03','2024-08-16 11:25:03',0),
                                                                            (1,5943,'2024-08-16 11:25:03','2024-08-16 11:25:03',0),
                                                                            (5944,5,'2024-08-16 11:25:14','2024-08-16 11:25:14',0),
                                                                            (5,5944,'2024-08-16 11:25:14','2024-08-16 11:25:14',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5945,1,'2024-08-16 11:26:36','2024-08-16 11:26:36',0),
                                                                            (1,5945,'2024-08-16 11:26:36','2024-08-16 11:26:36',0),
                                                                            (5936,1,'2024-08-16 11:26:41','2024-08-16 11:26:41',0),
                                                                            (1,5936,'2024-08-16 11:26:41','2024-08-16 11:26:41',0),
                                                                            (5936,7,'2024-08-16 11:26:51','2024-08-16 11:26:51',0),
                                                                            (7,5936,'2024-08-16 11:26:51','2024-08-16 11:26:51',0),
                                                                            (5944,1,'2024-08-16 11:27:55','2024-08-16 11:27:55',0),
                                                                            (1,5944,'2024-08-16 11:27:55','2024-08-16 11:27:55',0),
                                                                            (5944,7,'2024-08-16 11:27:58','2024-08-16 11:27:58',0),
                                                                            (7,5944,'2024-08-16 11:27:58','2024-08-16 11:27:58',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5948,7,'2024-08-16 11:29:47','2024-08-16 11:29:47',0),
                                                                            (7,5948,'2024-08-16 11:29:47','2024-08-16 11:29:47',0),
                                                                            (5948,1,'2024-08-16 11:31:37','2024-08-16 11:31:37',0),
                                                                            (1,5948,'2024-08-16 11:31:37','2024-08-16 11:31:37',0),
                                                                            (5952,2,'2024-08-16 11:34:36','2024-08-16 11:34:36',0),
                                                                            (2,5952,'2024-08-16 11:34:36','2024-08-16 11:34:36',0),
                                                                            (5952,5856,'2024-08-16 11:35:10','2024-08-16 11:35:10',0),
                                                                            (5856,5952,'2024-08-16 11:35:10','2024-08-16 11:35:10',0),
                                                                            (5956,1,'2024-08-16 11:47:52','2024-08-16 11:47:52',0),
                                                                            (1,5956,'2024-08-16 11:47:52','2024-08-16 11:47:52',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5965,7,'2024-08-16 12:40:22','2024-08-16 12:40:22',0),
                                                                            (7,5965,'2024-08-16 12:40:22','2024-08-16 12:40:22',0),
                                                                            (5965,8,'2024-08-16 12:40:44','2024-08-16 12:40:44',0),
                                                                            (8,5965,'2024-08-16 12:40:44','2024-08-16 12:40:44',0),
                                                                            (5969,7,'2024-08-16 14:29:45','2024-08-16 14:29:45',0),
                                                                            (7,5969,'2024-08-16 14:29:45','2024-08-16 14:29:45',0),
                                                                            (5969,5929,'2024-08-16 14:30:23','2024-08-16 14:30:23',0),
                                                                            (5929,5969,'2024-08-16 14:30:23','2024-08-16 14:30:23',0),
                                                                            (5969,1,'2024-08-16 14:30:28','2024-08-16 14:30:28',0),
                                                                            (1,5969,'2024-08-16 14:30:28','2024-08-16 14:30:28',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5975,7,'2024-08-16 17:17:19','2024-08-16 17:17:19',0),
                                                                            (7,5975,'2024-08-16 17:17:19','2024-08-16 17:17:19',0),
                                                                            (5976,1,'2024-08-16 17:21:09','2024-08-16 17:21:09',0),
                                                                            (1,5976,'2024-08-16 17:21:09','2024-08-16 17:21:09',0),
                                                                            (5982,7,'2024-08-16 21:40:40','2024-08-16 21:40:40',0),
                                                                            (7,5982,'2024-08-16 21:40:40','2024-08-16 21:40:40',0),
                                                                            (5989,8,'2024-08-17 12:48:26','2024-08-17 12:48:26',0),
                                                                            (8,5989,'2024-08-17 12:48:26','2024-08-17 12:48:26',0),
                                                                            (5936,5862,'2024-08-17 16:17:03','2024-08-17 16:17:03',0),
                                                                            (5862,5936,'2024-08-17 16:17:03','2024-08-17 16:17:03',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5993,1,'2024-08-19 07:08:12','2024-08-19 07:08:12',0),
                                                                            (1,5993,'2024-08-19 07:08:12','2024-08-19 07:08:12',0),
                                                                            (5993,8,'2024-08-19 07:08:46','2024-08-19 07:08:46',0),
                                                                            (8,5993,'2024-08-19 07:08:46','2024-08-19 07:08:46',0),
                                                                            (5993,5,'2024-08-19 07:08:50','2024-08-19 07:08:50',0),
                                                                            (5,5993,'2024-08-19 07:08:50','2024-08-19 07:08:50',0),
                                                                            (5994,5858,'2024-08-19 11:51:58','2024-08-19 11:51:58',0),
                                                                            (5858,5994,'2024-08-19 11:51:58','2024-08-19 11:51:58',0),
                                                                            (5996,5878,'2024-08-19 23:47:05','2024-08-19 23:47:05',0),
                                                                            (5878,5996,'2024-08-19 23:47:05','2024-08-19 23:47:05',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5996,9,'2024-08-20 00:01:39','2024-08-20 00:01:39',0),
                                                                            (9,5996,'2024-08-20 00:01:39','2024-08-20 00:01:39',0),
                                                                            (5995,1,'2024-08-20 14:25:24','2024-08-20 14:25:24',0),
                                                                            (1,5995,'2024-08-20 14:25:24','2024-08-20 14:25:24',0),
                                                                            (5936,2,'2024-08-21 08:16:28','2024-08-21 08:16:28',0),
                                                                            (2,5936,'2024-08-21 08:16:28','2024-08-21 08:16:28',0),
                                                                            (6003,1,'2024-08-22 13:26:16','2024-08-22 13:26:16',0),
                                                                            (1,6003,'2024-08-22 13:26:16','2024-08-22 13:26:16',0),
                                                                            (6004,7,'2024-08-23 14:09:08','2024-08-23 14:09:08',0),
                                                                            (7,6004,'2024-08-23 14:09:08','2024-08-23 14:09:08',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (5936,5916,'2024-08-25 02:18:12','2024-08-25 02:18:12',0),
                                                                            (5916,5936,'2024-08-25 02:18:12','2024-08-25 02:18:12',0),
                                                                            (6008,8,'2024-08-29 14:46:29','2024-08-29 14:46:29',0),
                                                                            (8,6008,'2024-08-29 14:46:29','2024-08-29 14:46:29',0),
                                                                            (6009,1,'2024-08-29 17:05:38','2024-08-29 17:05:38',0),
                                                                            (1,6009,'2024-08-29 17:05:38','2024-08-29 17:05:38',0),
                                                                            (6011,7,'2024-09-01 23:20:43','2024-09-01 23:20:43',0),
                                                                            (7,6011,'2024-09-01 23:20:43','2024-09-01 23:20:43',0),
                                                                            (6011,8,'2024-09-02 09:20:43','2024-09-02 09:20:43',0),
                                                                            (8,6011,'2024-09-02 09:20:43','2024-09-02 09:20:43',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (6014,1,'2024-09-04 10:27:38','2024-09-04 10:27:38',0),
                                                                            (1,6014,'2024-09-04 10:27:38','2024-09-04 10:27:38',0),
                                                                            (6014,5881,'2024-09-04 10:28:06','2024-09-04 10:28:06',0),
                                                                            (5881,6014,'2024-09-04 10:28:06','2024-09-04 10:28:06',0),
                                                                            (6019,1,'2024-09-05 15:11:09','2024-09-05 15:11:09',0),
                                                                            (1,6019,'2024-09-05 15:11:09','2024-09-05 15:11:09',0),
                                                                            (6016,1,'2024-09-06 13:06:37','2024-09-06 13:06:37',0),
                                                                            (1,6016,'2024-09-06 13:06:37','2024-09-06 13:06:37',0),
                                                                            (5936,5857,'2024-09-06 21:57:47','2024-09-06 21:57:47',0),
                                                                            (5857,5936,'2024-09-06 21:57:47','2024-09-06 21:57:47',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (6023,5859,'2024-09-08 18:39:01','2024-09-08 18:39:01',0),
                                                                            (5859,6023,'2024-09-08 18:39:01','2024-09-08 18:39:01',0),
                                                                            (6023,5861,'2024-09-08 18:39:08','2024-09-08 18:39:08',0),
                                                                            (5861,6023,'2024-09-08 18:39:08','2024-09-08 18:39:08',0),
                                                                            (6027,6026,'2024-09-09 10:23:50','2024-09-09 10:23:50',0),
                                                                            (6026,6027,'2024-09-09 10:23:50','2024-09-09 10:23:50',0),
                                                                            (1,5892,'2024-09-10 00:00:27','2024-09-10 00:00:27',0),
                                                                            (5892,1,'2024-09-10 00:00:27','2024-09-10 00:00:27',0),
                                                                            (6027,2,'2024-09-10 10:38:09','2024-09-10 10:38:09',0),
                                                                            (2,6027,'2024-09-10 10:38:09','2024-09-10 10:38:09',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (6029,1,'2024-09-10 15:04:05','2024-09-10 15:04:05',0),
                                                                            (1,6029,'2024-09-10 15:04:05','2024-09-10 15:04:05',0),
                                                                            (6031,1,'2024-09-10 17:17:25','2024-09-10 17:17:25',0),
                                                                            (1,6031,'2024-09-10 17:17:25','2024-09-10 17:17:25',0),
                                                                            (6037,7,'2024-09-12 15:05:27','2024-09-12 15:05:27',0),
                                                                            (7,6037,'2024-09-12 15:05:27','2024-09-12 15:05:27',0),
                                                                            (6040,1,'2024-09-13 09:30:41','2024-09-13 09:30:41',0),
                                                                            (1,6040,'2024-09-13 09:30:41','2024-09-13 09:30:41',0),
                                                                            (6041,7,'2024-09-13 10:32:39','2024-09-13 10:32:39',0),
                                                                            (7,6041,'2024-09-13 10:32:39','2024-09-13 10:32:39',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (6045,7,'2024-09-13 16:54:13','2024-09-13 16:54:13',0),
                                                                            (7,6045,'2024-09-13 16:54:13','2024-09-13 16:54:13',0),
                                                                            (6047,7,'2024-09-14 10:45:42','2024-09-14 10:45:42',0),
                                                                            (7,6047,'2024-09-14 10:45:42','2024-09-14 10:45:42',0),
                                                                            (6048,5858,'2024-09-14 17:10:01','2024-09-14 17:10:01',0),
                                                                            (5858,6048,'2024-09-14 17:10:01','2024-09-14 17:10:01',0),
                                                                            (6054,7,'2024-09-17 00:43:08','2024-09-17 00:43:08',0),
                                                                            (7,6054,'2024-09-17 00:43:08','2024-09-17 00:43:08',0),
                                                                            (5936,5861,'2024-09-18 11:34:33','2024-09-18 11:34:33',0),
                                                                            (5861,5936,'2024-09-18 11:34:33','2024-09-18 11:34:33',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (6068,7,'2024-09-23 18:56:08','2024-09-23 18:56:08',0),
                                                                            (7,6068,'2024-09-23 18:56:08','2024-09-23 18:56:08',0),
                                                                            (6068,1,'2024-09-23 18:56:55','2024-09-23 18:56:55',0),
                                                                            (1,6068,'2024-09-23 18:56:55','2024-09-23 18:56:55',0),
                                                                            (5949,7,'2024-09-23 21:24:38','2024-09-23 21:24:38',0),
                                                                            (7,5949,'2024-09-23 21:24:38','2024-09-23 21:24:38',0),
                                                                            (6071,5856,'2024-09-24 09:50:25','2024-09-24 09:50:25',0),
                                                                            (5856,6071,'2024-09-24 09:50:25','2024-09-24 09:50:25',0),
                                                                            (6073,1,'2024-09-24 16:41:43','2024-09-24 16:41:43',0),
                                                                            (1,6073,'2024-09-24 16:41:43','2024-09-24 16:41:43',0);
INSERT INTO hjj.friend (userId,friendId,createTime,updateTime,isDelete) VALUES
                                                                            (1,5861,'2024-09-24 22:30:15','2024-09-24 22:30:15',0),
                                                                            (5861,1,'2024-09-24 22:30:15','2024-09-24 22:30:15',0),
                                                                            (6075,7,'2024-09-25 09:34:33','2024-09-25 09:34:33',0),
                                                                            (7,6075,'2024-09-25 09:34:33','2024-09-25 09:34:33',0),
                                                                            (6076,1,'2024-09-25 11:35:05','2024-09-25 11:35:05',0),
                                                                            (1,6076,'2024-09-25 11:35:05','2024-09-25 11:35:05',0);



INSERT INTO hjj.message (`type`,fromId,toId,text,avatarUrl,blogId,teamId,isRead,createTime,updateTime,isDelete) VALUES
                                                                                                                    (1,5921,1,'香香点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-08-13 22:21:42','2024-08-16 12:51:46',1),
                                                                                                                    (1,5921,1,'香香点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-08-13 22:21:43','2024-08-14 13:49:51',0),
                                                                                                                    (1,5922,1,'程序员夜幕点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-08-13 22:49:40','2024-08-14 13:49:51',0),
                                                                                                                    (1,5923,1,'夏天点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-08-14 00:23:10','2024-08-14 13:49:51',0),
                                                                                                                    (1,5942,1,'三十三点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 11:23:48','2024-08-16 12:51:48',1),
                                                                                                                    (1,5942,1,'三十三点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 11:23:49','2024-08-16 12:00:54',0),
                                                                                                                    (2,5943,5948,'飞翔666关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,0,'2024-08-16 11:36:53','2024-08-16 11:36:53',0),
                                                                                                                    (2,5953,1,'kuhakuu关注了您','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/09ba3e9f-17e6-41dc-a56b-73279d3687c3.jpg',NULL,NULL,1,'2024-08-16 11:42:42','2024-09-24 22:29:32',1),
                                                                                                                    (2,5956,1,'王积有关注了您','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/da22a7ff-2fda-4d44-b627-9fe464e2c5bc.jpg',NULL,NULL,1,'2024-08-16 11:50:27','2024-08-16 12:00:51',0),
                                                                                                                    (1,5966,1,'hary点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 12:49:02','2024-08-16 12:51:53',1);
INSERT INTO hjj.message (`type`,fromId,toId,text,avatarUrl,blogId,teamId,isRead,createTime,updateTime,isDelete) VALUES
                                                                                                                    (1,5966,1,'hary点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 12:49:07','2024-08-16 12:51:50',1),
                                                                                                                    (1,5966,1,'hary点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 12:49:09','2024-08-16 12:51:51',1),
                                                                                                                    (1,5966,1,'hary点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 12:49:10','2024-08-16 12:51:57',1),
                                                                                                                    (1,5966,1,'hary点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 12:49:12','2024-08-16 12:51:56',1),
                                                                                                                    (1,5966,1,'hary点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 12:49:13','2024-08-16 12:51:41',0),
                                                                                                                    (2,5966,1,'hary关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-08-16 12:49:35','2024-08-16 12:53:05',0),
                                                                                                                    (1,5967,1,'wyh点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-08-16 13:28:11','2024-08-16 15:29:13',0),
                                                                                                                    (1,5970,1,'jack点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-16 14:39:24','2024-08-16 15:29:13',0),
                                                                                                                    (2,5975,1,'qwe关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-08-16 17:10:18','2024-08-16 19:35:44',0),
                                                                                                                    (0,5975,1,'qwe收藏了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-08-16 17:16:45','2024-08-17 12:35:02',0);
INSERT INTO hjj.message (`type`,fromId,toId,text,avatarUrl,blogId,teamId,isRead,createTime,updateTime,isDelete) VALUES
                                                                                                                    (1,1,1,'burger点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/f944772f-d0bf-4884-ab32-75e8c7f32075.jpg',2,NULL,1,'2024-08-18 16:37:29','2024-08-25 14:13:21',0),
                                                                                                                    (0,5992,1,'ziio收藏了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-18 17:03:37','2024-08-25 14:13:23',0),
                                                                                                                    (1,5992,1,'ziio点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-18 17:03:37','2024-08-25 14:13:21',0),
                                                                                                                    (2,5992,1,'ziio关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-08-19 23:57:18','2024-08-25 14:13:11',0),
                                                                                                                    (1,5936,1,'admin点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-23 18:21:55','2024-09-10 00:00:10',1),
                                                                                                                    (0,5936,1,'admin收藏了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-23 18:21:56','2024-08-25 14:13:23',0),
                                                                                                                    (0,5992,1,'ziio收藏了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-08-27 23:25:36','2024-09-24 22:29:36',0),
                                                                                                                    (2,6011,1,'懒惰关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-09-01 23:22:12','2024-09-10 00:00:07',0),
                                                                                                                    (1,6017,1,'阿卡点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-09-05 10:23:17','2024-09-10 00:00:02',0),
                                                                                                                    (1,5936,1,'admin点赞了你的博客C1own','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',4,NULL,1,'2024-09-06 22:23:24','2024-09-10 00:00:02',0);
INSERT INTO hjj.message (`type`,fromId,toId,text,avatarUrl,blogId,teamId,isRead,createTime,updateTime,isDelete) VALUES
                                                                                                                    (1,6029,5948,'YeYe点赞了你的博客好厉害','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',3,NULL,0,'2024-09-09 19:12:45','2024-09-09 19:12:45',0),
                                                                                                                    (1,6029,1,'YeYe点赞了你的博客C1own','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',4,NULL,1,'2024-09-09 19:12:57','2024-09-10 00:00:02',0),
                                                                                                                    (0,6045,1,'444收藏了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-09-13 16:53:56','2024-09-24 22:29:36',0),
                                                                                                                    (1,6046,6046,'adminadmin点赞了你的博客厚米系统🫠','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',5,NULL,0,'2024-09-14 10:30:42','2024-09-14 10:30:42',0),
                                                                                                                    (0,6046,6046,'adminadmin收藏了你的博客厚米系统🫠','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',5,NULL,0,'2024-09-14 10:30:44','2024-09-14 10:30:44',0),
                                                                                                                    (1,6048,6046,'zxr123点赞了你的博客厚米系统🫠','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',5,NULL,0,'2024-09-14 17:10:37','2024-09-14 17:10:37',0),
                                                                                                                    (0,6048,6046,'zxr123收藏了你的博客厚米系统🫠','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',5,NULL,0,'2024-09-14 17:10:49','2024-09-14 17:10:49',0),
                                                                                                                    (2,6052,6053,'delete关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-09-15 10:05:55','2024-09-15 10:08:55',0),
                                                                                                                    (2,6053,6052,'delete1关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,0,'2024-09-15 10:08:46','2024-09-15 10:08:46',0),
                                                                                                                    (1,5936,1,'admin点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,1,'2024-09-17 13:24:22','2024-09-20 11:19:04',0);
INSERT INTO hjj.message (`type`,fromId,toId,text,avatarUrl,blogId,teamId,isRead,createTime,updateTime,isDelete) VALUES
                                                                                                                    (1,5936,1,'admin点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,1,'2024-09-18 11:35:09','2024-09-20 11:19:04',0),
                                                                                                                    (2,5936,6046,'admin关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,0,'2024-09-19 11:39:57','2024-09-19 11:39:57',0),
                                                                                                                    (1,6068,6046,'非凡点赞了你的博客厚米系统🫠','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/3e222172-82b8-43da-8e82-7d0a7aeb6a2f.png',5,NULL,0,'2024-09-23 18:55:30','2024-09-23 18:55:30',0),
                                                                                                                    (0,6068,6046,'非凡收藏了你的博客厚米系统🫠','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/3e222172-82b8-43da-8e82-7d0a7aeb6a2f.png',5,NULL,0,'2024-09-23 18:55:32','2024-09-23 18:55:32',0),
                                                                                                                    (2,5949,1,'那就叫小徐吧关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-09-23 21:28:31','2024-09-24 22:29:27',0),
                                                                                                                    (1,6073,6073,'xxxx点赞了你的博客12e1','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',6,NULL,0,'2024-09-24 16:43:16','2024-09-24 16:43:16',0),
                                                                                                                    (2,6073,1,'xxxx关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-09-24 16:43:20','2024-09-24 22:29:27',0),
                                                                                                                    (1,5996,1,'yjh点赞了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,0,'2024-09-24 21:46:43','2024-09-24 21:46:43',0),
                                                                                                                    (1,5996,6046,'yjh点赞了你的博客厚米系统🫠','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',5,NULL,0,'2024-09-24 21:54:43','2024-09-24 21:54:43',0),
                                                                                                                    (2,5996,1,'yjh关注了您','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',NULL,NULL,1,'2024-09-24 22:15:27','2024-09-24 22:29:27',0);
INSERT INTO hjj.message (`type`,fromId,toId,text,avatarUrl,blogId,teamId,isRead,createTime,updateTime,isDelete) VALUES
                                                                                                                    (1,5996,1,'yjh点赞了你的博客Spring Boot 引入 Guava Retry 实现重试机制','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',2,NULL,0,'2024-09-24 22:15:31','2024-09-24 22:15:31',0),
                                                                                                                    (0,5996,1,'yjh收藏了你的博客EasyExcel 初使用—— Java 实现几种写入 Excel 功能-CSDN博客','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,0,'2024-09-24 22:40:00','2024-09-24 22:40:00',0);



INSERT INTO hjj.team (teamName,description,maxNum,expireTime,userId,status,password,createTime,updateTime,isDelete) VALUES
                                                                                                                        ('IT之家','喜欢学IT就来吧',3,'2026-01-01 22:46:38',1,0,'','2024-07-17 22:46:43','2024-07-17 22:46:43',0),
                                                                                                                        ('秘密小屋','打工人的庇护所',3,'2026-05-01 22:47:13',1,2,'123','2024-07-17 22:47:19','2024-07-17 22:47:19',0),
                                                                                                                        ('boss','',10,'2027-01-01 14:09:03',5888,2,'000000','2024-07-25 14:09:20','2024-07-25 14:09:20',0),
                                                                                                                        ('1','1',3,'2029-01-01 22:20:13',5921,0,'','2024-08-13 22:20:15','2024-08-13 22:20:15',0),
                                                                                                                        ('ikun','小黑子',3,'2030-01-01 17:10:50',5934,0,'','2024-08-15 17:10:55','2024-08-15 17:10:55',0),
                                                                                                                        ('ppp','专业',6,'2031-01-01 11:30:25',5946,0,'','2024-08-16 11:30:29','2024-08-16 11:30:29',0),
                                                                                                                        ('IKUN','ikun',3,'2034-10-01 14:42:33',5970,0,'','2024-08-16 14:42:35','2024-08-16 14:42:35',0),
                                                                                                                        ('前端小分队','哈哈哈哈哈',3,'2025-01-01 21:24:46',5936,0,'','2024-08-18 21:24:49','2024-08-23 18:21:39',1),
                                                                                                                        ('teamA','',4,'2025-01-01 13:04:48',5992,0,'','2024-08-20 13:05:11','2024-08-20 13:05:11',0),
                                                                                                                        ('队伍q1','测试队伍1',3,'2027-03-01 12:45:30',5989,0,'','2024-08-21 18:49:26','2024-08-28 12:45:32',0);
INSERT INTO hjj.team (teamName,description,maxNum,expireTime,userId,status,password,createTime,updateTime,isDelete) VALUES
                                                                                                                        ('队伍q2','罚恶法但是',4,'2030-01-01 13:29:38',5989,2,'123456','2024-08-28 13:29:44','2024-08-28 13:29:44',0),
                                                                                                                        ('1212','1212',3,'2025-01-01 15:12:38',6019,0,'','2024-09-05 15:12:39','2024-09-05 15:12:39',0),
                                                                                                                        ('passion','passion',10,'2030-01-01 10:24:36',6046,0,'','2024-09-14 10:24:39','2024-09-14 10:24:39',0),
                                                                                                                        ('Java进行时','喜欢java的一起学习吧',6,'2024-12-08 16:48:59',6029,0,'','2024-09-23 16:49:06','2024-09-23 16:49:06',0),
                                                                                                                        ('xiaoxu','123',3,'2034-01-12 18:15:47',5949,0,'','2024-09-23 18:15:50','2024-09-23 18:15:50',0),
                                                                                                                        ('111','asd',5,'2034-12-02 03:35:24',6068,1,'','2024-09-24 03:35:46','2024-09-24 03:35:46',0),
                                                                                                                        ('王者来吗','嘻嘻嘻',8,'2026-02-05 22:46:00',5996,0,'','2024-09-24 22:46:01','2024-09-24 22:46:01',0);



INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('burger','burger','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/f944772f-d0bf-4884-ab32-75e8c7f32075.jpg',1,'我是汉堡，这个网站的站长，有问题欢迎加我微信或直接私聊我','b84de60a71368ddf3112f8b784502894','12345678232','123@qq.com',0,'2024-06-06 23:54:24','2024-09-25 18:46:02',0,0,'["嵌入式","男","c++"]',110.000000,23.000000,0,117,0,0,80),
                                                                                                                                                                                                                           ('17651','1765','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,'这不好说……','b84de60a71368ddf3112f8b784502894','1123443444','12335445@qq.com',0,'2024-06-06 23:55:01','2024-06-09 14:36:31',0,0,'["java","python","嵌入式","男"]',92.000000,25.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('dwidjwd','zhangjinhao','https://ts1.cn.mm.bing.net/th/id/R-C.342ab1cc3c7d69f4aad21c397cf2afd3?rik=WgpKA24b4Qg4tg&riu=http%3a%2f%2fimg.touxiangwu.com%2fuploads%2fallimg%2f2021122622%2fcmm0tnq35cb.jpg&ehk=extHR%2fx6xlt0dpzGH56bl6G%2bRiKdBlufJzxteIka3WI%3d&risl=&pid=ImgRaw&r=0',1,'嗨，是一个喜欢睡觉、学习、码代码的美男子','b84de60a71368ddf3112f8b784502894','12354451211','34345445@qq.com',0,'2024-06-09 14:27:00','2024-06-28 22:22:48',0,0,'["男","emo","内卷","大三","c++","java"]',12.000000,67.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('志强','zhiqiang','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,'你好呀','b84de60a71368ddf3112f8b784502894','18223484511','173635445@qq.com',0,'2024-06-09 14:31:11','2024-06-09 14:36:31',0,0,'["python","go","有对象","大四"]',123.000000,23.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('dwljdwd','haohaohao','https://ts1.cn.mm.bing.net/th/id/R-C.a3326dfea8ea5544ebd406b824137ee9?rik=c6C3lRiXIQoEYg&riu=http%3a%2f%2fimg.touxiangwu.com%2fzb_users%2fupload%2f2022%2f11%2f202211021667379370320460.jpg&ehk=VTjaG57lKTodjm5E3FWOUlJf%2fWxUbyZ8lw%2bMXV2END4%3d&risl=&pid=ImgRaw&r=0',0,'一枚爱吃的小女生','b84de60a71368ddf3112f8b784502894','133442221','2736474279@qq.com',0,'2024-06-09 14:38:20','2024-06-09 14:40:44',0,0,'["内卷","女","大二"]',45.000000,45.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('handsome','handsome','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,'我是 handsome','b84de60a71368ddf3112f8b784502894','1762233232','1364453434@qq.com',0,'2024-06-09 14:43:24','2024-06-09 14:45:34',0,0,'["java"]',45.000000,45.230000,0,0,0,0,0),
                                                                                                                                                                                                                           ('ppppppppp','ppppppppp','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'d090483c8aa6e41b745282ee6922c7b7',NULL,NULL,0,'2024-06-11 15:41:04','2024-08-13 14:59:26',0,0,'["大一"]',111.000000,9.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('云散','yunsan','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'65f098c4a9f256f6fb08455cd2382a80',NULL,NULL,0,'2024-07-09 10:23:48','2024-08-13 14:59:26',0,0,'["男","emo"]',120.430136,36.148099,0,0,0,0,0),
                                                                                                                                                                                                                           ('test','test','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b052dcf80de23690421e0e2dccf2e6c0',NULL,NULL,0,'2024-07-11 15:54:54','2024-08-13 14:59:26',0,0,'["go","男","单身","有对象","已婚","emo","内卷"]',120.000000,10.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('markxx','markxx','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'7e89390d088e3ae97612dd47af6e104e',NULL,NULL,0,'2024-07-11 16:43:00','2024-08-13 14:59:26',0,0,'["男","有对象","大一","java"]',0.000000,0.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('jiafeimao','qwer1234','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'3f7377617f6194be16b3b59417bbd0b6',NULL,NULL,0,'2024-07-12 08:43:52','2024-08-13 14:59:26',0,0,'["男"]',130.000000,30.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('kube','kube','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'615888c1890831391807b00c347a68d0',NULL,NULL,0,'2024-07-15 14:44:22','2024-08-13 14:59:26',0,0,'["java","go"]',118.000000,32.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('崔冰','19891214','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'4526a3f908d6e9ab871f214289680e43',NULL,NULL,0,'2024-07-16 11:03:33','2024-08-13 14:59:26',0,0,'["go"]',117.000000,34.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('leaveYoung','leaveYoung','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'975dd426ab690c7cbee0f076759f009f',NULL,NULL,0,'2024-07-16 17:05:13','2024-08-13 14:59:26',0,0,'["java","男","有对象"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('11','123456789','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-07-18 08:39:41','2024-08-13 14:59:26',0,0,'["java","大四","男","已婚"]',114.000000,30.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('wwl','13093291267','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'f333512a8002f07218bf2bc77bdc72b5',NULL,NULL,0,'2024-07-18 17:56:39','2024-08-13 14:59:26',0,0,'["java","大一","男","内卷","单身"]',120.271191,30.330866,0,0,0,0,0),
                                                                                                                                                                                                                           ('dogYupi','dogYupi','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-07-19 16:02:09','2024-08-13 14:59:25',0,0,'["java","大二","男","有对象"]',113.250000,20.153000,0,0,0,0,0),
                                                                                                                                                                                                                           ('xxx','13403818','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'71ae79384f552e7ca3b9df1a56a31748',NULL,NULL,0,'2024-07-20 00:53:22','2024-08-13 14:59:26',0,0,'["java","大三","男","有对象"]',120.000000,10.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('admin','16670008280','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6a5939e31476a43660b6ba1dcd15929e',NULL,NULL,0,'2024-07-20 15:24:25','2024-08-13 14:59:26',0,0,'["java","大三","男","单身"]',80.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('阿达是的','1019507955','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'e31d11ed97ffa419fc0a402f8cb092d4',NULL,NULL,0,'2024-07-21 18:18:19','2024-08-13 14:59:26',0,0,'["java"]',12.000000,12.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('草上飞','Qq123456','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6a43687d50ec7a407906b6cd9945aabb',NULL,NULL,0,'2024-07-23 14:19:55','2024-08-13 14:59:26',0,0,'["java"]',114.332810,23.008071,0,0,0,0,0),
                                                                                                                                                                                                                           ('23131','210303110201','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'0f2201f0ba63dcffb2814155b3779b2a',NULL,NULL,0,'2024-07-25 09:40:47','2024-08-13 14:59:26',0,0,'["go"]',123.000000,32.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('admin','123456qwe','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'4f98b97f10c495d9b05ff72cc1147cfa',NULL,NULL,0,'2024-07-25 14:06:55','2024-08-13 14:59:25',0,0,'["go","女","单身","大一"]',114.342530,30.499840,0,0,0,0,0),
                                                                                                                                                                                                                           ('咪咪','houmipipeixitong','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6614a1a7313a726cb2cdcb62f309612c',NULL,NULL,0,'2024-07-25 15:18:56','2024-08-13 14:59:26',0,0,'["java","python"]',117.120000,36.650000,0,0,0,0,0),
                                                                                                                                                                                                                           ('213121234','2131232131','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'19ee458cd0f156cb03ae63b14e61c83e',NULL,NULL,0,'2024-07-25 17:51:18','2024-08-13 14:59:26',0,0,'["python","大一","女","emo"]',122.000000,12.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('zzy','18365264626','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'37aa93dfd7adf149cd5532c34cdf2193',NULL,NULL,0,'2024-07-26 10:31:09','2024-08-13 14:59:26',0,0,'["男"]',0.000000,0.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('1111','1111','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'27470323a6e7fff59b629c4183e33d4e',NULL,NULL,0,'2024-07-26 17:25:14','2024-08-13 14:59:25',0,0,'["java","c++"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('test','test1234567','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'ce2dbc176a324e813a7f9960ce4a4a7f',NULL,NULL,0,'2024-07-26 22:54:33','2024-08-13 14:59:26',0,0,'["go"]',80.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('djdjd','dcfred','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'f7366b92e3ffc2f863cd9108201b47fd',NULL,NULL,0,'2024-07-29 16:54:56','2024-08-13 14:59:26',0,0,'["c++","研二","男","内卷"]',180.000000,60.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('11111','11111','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'59df2fb0ad6d5cb4ea76fd0d876d7c34',NULL,NULL,0,'2024-08-01 14:02:38','2024-08-13 14:59:25',0,0,'["java","大四","男","单身"]',11.000000,11.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('hamnmen','hamnmen','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'376a3bf0b3f029b89ae5703572eb0854',NULL,NULL,0,'2024-08-01 16:51:51','2024-08-13 14:59:26',0,0,'["java","大二","已婚","有对象","男"]',122.000000,22.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('cjx','cjx123456','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-03 16:42:46','2024-08-13 14:59:26',0,0,'["java","大三","男","已婚"]',150.000000,50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('啊惭愧啊','啊惭愧啊','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'82365691d38757c052fc240ce18d7cb1',NULL,NULL,0,'2024-08-03 17:59:41','2024-08-13 14:59:26',0,0,'["java","大三","emo"]',99.000000,50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('gujie1','jianggujie1','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'68fd12a0cbdc1fcb77a1da06c1aa570c',NULL,NULL,0,'2024-08-03 18:53:54','2024-08-13 14:59:25',0,0,'["java","go","大四","男","有对象"]',116.000000,40.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('ethanchan','chenxi','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'7cf9abb5f366e812837b4208023ed3e7',NULL,NULL,0,'2024-08-06 20:12:41','2024-08-13 14:59:25',0,0,'["go","大四","男","单身"]',-170.000000,-50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('11111111','adminadmin','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'27470323a6e7fff59b629c4183e33d4e',NULL,NULL,0,'2024-08-09 18:32:37','2024-08-13 14:59:26',0,0,'["go","大三","女","有对象"]',11.000000,11.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('msong','msong','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'84eeeef577a57ecb82fa71d4526a11ce',NULL,NULL,0,'2024-08-12 15:50:19','2024-08-13 14:59:26',0,0,'["java"]',108.940000,34.341101,0,0,0,0,0),
                                                                                                                                                                                                                           ('香香','xiangxiang','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'3110b265c5bbefe6ad82ba8ba638e239',NULL,NULL,0,'2024-08-13 22:18:33','2024-08-13 22:19:35',0,0,'["java","大四","男","单身","内卷"]',1.000000,1.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('程序员夜幕','codeyemu','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-13 22:48:46','2024-08-16 12:04:28',0,0,'["java"]',80.000000,80.000000,0,0,0,0,20),
                                                                                                                                                                                                                           ('夏天','1667837043','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-14 00:19:34','2024-08-14 00:19:34',0,0,'["java","大三","男","有对象"]',100.000000,50.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('啊啊啊','lufei','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-14 07:18:32','2024-08-14 07:19:40',0,0,'["java"]',60.000000,60.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('心火','xinhuo','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-14 11:01:06','2024-08-14 11:03:19',0,0,'["java","大三","男","单身"]',117.000000,39.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('user','02246a','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6a63825730690955002bee35d192cf7b',NULL,NULL,0,'2024-08-14 16:40:52','2024-08-14 16:40:52',0,0,'["java"]',113.180000,22.880000,0,0,0,0,0),
                                                                                                                                                                                                                           ('hql','hqlhhh','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-14 19:59:14','2024-08-14 19:59:14',0,0,'["java"]',1.000000,2.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('香香1','xiangxiang1','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'60fd6f640b6517036ca46a2fc9e86082',NULL,NULL,0,'2024-08-14 20:34:31','2024-08-14 20:34:31',0,0,'["java","大三","女","有对象"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('wind','1957334229','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'2f3c9dbb7b25269651b88e0e73665d95',NULL,NULL,0,'2024-08-14 21:09:08','2024-08-14 21:09:08',0,0,'["java","男","单身"]',113.000000,23.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('qimu','qimu','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-15 12:10:45','2024-08-15 12:14:22',0,0,'["java"]',2.000000,2.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('jojo','jojo','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6593d51f2927a4327318c2ef3958a2a5',NULL,NULL,0,'2024-08-15 16:00:44','2024-08-15 16:01:22',0,0,'["java","研三","男","单身","内卷"]',0.000000,0.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('xiaozuo','zuohongkuan','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'ad8f9d0840c30229c664a4e0524ce8f7',NULL,NULL,0,'2024-08-15 17:03:35','2024-08-15 17:03:35',0,0,'["java"]',112.530000,33.010000,0,0,0,0,0),
                                                                                                                                                                                                                           ('wdcla','wdcla','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b89dfc1c1dba2458a045e6e7537d079e',NULL,NULL,0,'2024-08-15 20:19:20','2024-08-15 20:19:20',0,0,'["java"]',78.000000,67.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('admin','admin','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-15 22:26:36','2024-09-26 10:30:17',0,0,'["java"]',0.000000,0.000000,0,0,0,0,40),
                                                                                                                                                                                                                           ('annian','annian','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 11:19:26','2024-08-16 11:19:26',0,0,'["java","大一","男","内卷"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('qqq','qwerasdf','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'3ac2711a9963457aef19080c53258613',NULL,NULL,0,'2024-08-16 11:20:45','2024-08-16 11:20:45',0,0,'["java","c++","go"]',10.000000,10.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('宿舍','2215127761','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'bb8fc89014a8d9798b91fefa02619766',NULL,NULL,0,'2024-08-16 11:22:47','2024-08-16 11:22:47',0,0,'["java"]',120.000000,60.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('三十三','1231','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 11:23:14','2024-08-16 11:23:14',0,0,'["嵌入式","go","java","c++","大一","男","单身"]',111.000000,22.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('飞翔666','feixiang','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-16 11:24:01','2024-08-16 11:24:01',0,0,'["java","大一","男","单身"]',90.000000,50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('123456','123456','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'1a41d0ce0480e0f32f580ea9bdcf2cfa',NULL,NULL,0,'2024-08-16 11:24:57','2024-08-16 11:24:57',0,0,'["java"]',52.000000,30.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('xulong','admintest','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,'',0,'2024-08-16 11:25:23','2024-08-16 11:29:57',0,0,'["java"]',90.000000,0.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('xiaohan','xiaohan','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/210dc21d-73c5-4bb7-9153-3134002bd7b8.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 11:27:51','2024-08-16 11:29:30',0,0,'["java","大三","男","单身","内卷"]',100.000000,50.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('美团外卖小哥','zcxzcxzcx','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 11:28:57','2024-09-24 21:45:57',0,0,'["java"]',60.000000,60.000000,0,111,0,0,10);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('那就叫小徐吧','xiaoxu','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'06c5d2cda08958120ebf57ba4285b556',NULL,NULL,0,'2024-08-16 11:29:56','2024-09-23 21:25:08',0,0,'["java","大三","男"]',119.203356,26.034314,0,0,0,0,30),
                                                                                                                                                                                                                           ('逆风微笑的代码狗','逆风微笑的代码狗','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'8372fe0674c523d18af60ae1f0b655cd',NULL,NULL,0,'2024-08-16 11:32:23','2024-08-16 11:32:23',0,0,'["java","大四","男","内卷"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('加油','yjh123456789','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'0ef3614d7ae57e71f947fa5cc7dc9eab',NULL,NULL,0,'2024-08-16 11:34:23','2024-08-16 11:34:23',0,0,'["java"]',100.000000,50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('kuhakuu','kuhakuu','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/09ba3e9f-17e6-41dc-a56b-73279d3687c3.jpg',1,NULL,'7d11f64c68a69633500eadeda725bf82',NULL,NULL,0,'2024-08-16 11:38:26','2024-08-16 11:41:45',0,0,'["有对象","男","大四","java"]',1.000000,1.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('ljh','88888888','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'628b42693993405b14fb2018464ad50a',NULL,NULL,0,'2024-08-16 11:38:29','2024-08-16 11:38:29',0,0,'["java"]',150.000000,58.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('1','123455','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',0,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 11:42:02','2024-08-16 11:46:24',0,0,'["java"]',108.947089,34.259365,0,0,0,0,10),
                                                                                                                                                                                                                           ('王积有','TY123','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/da22a7ff-2fda-4d44-b627-9fe464e2c5bc.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 11:47:35','2024-08-16 11:49:41',0,0,'["java","大三","男","单身","内卷"]',0.000000,0.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('qqa','qaq123446','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-16 12:07:02','2024-08-16 12:07:02',0,0,'["java"]',120.000000,10.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('qa','qaqa','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-16 12:08:36','2024-08-16 12:10:12',0,0,'["java"]',120.000000,46.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('vvvvvv','vvvvvv','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-16 12:12:35','2024-08-16 12:14:42',0,0,'["java"]',60.000000,60.000000,0,0,0,0,10);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('Ziiio','Ziio12345','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-16 12:36:12','2024-08-16 12:36:12',0,0,'["java","大一","单身"]',80.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('yh','3157904941','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'df69ba567ffd7737af67866aa769e146',NULL,NULL,0,'2024-08-16 12:39:51','2024-08-16 12:39:51',0,0,'["java"]',30.000000,77.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('hary','hary','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-16 12:47:20','2024-08-16 12:50:00',0,0,'["java","研一","男","内卷"]',0.000000,0.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('wyh','15396192563','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'14a5b0bdf66f85fd112fbc8ffacde6db',NULL,NULL,0,'2024-08-16 13:25:47','2024-08-16 13:27:14',0,0,'["java"]',10.000000,10.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('zhangsan','zhangsan','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 13:56:16','2024-08-16 13:56:16',0,0,'["java"]',100.000000,20.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('1','11111111','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'59140ede329308e19be5dac234957585',NULL,NULL,0,'2024-08-16 14:28:50','2024-08-16 14:28:50',0,0,'["java"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('jack','jack','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/e3275a1f-2e4b-4608-8602-204cf33b5462.jpg',1,NULL,'e12d07d53b7ca2f83ea3cd398bdd7032',NULL,NULL,0,'2024-08-16 14:38:48','2024-08-16 14:41:09',0,0,'["java"]',0.000000,0.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('gdgsdghgsd','gdgsdghgsd','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b111b152431571257903aba6d1201a7f',NULL,NULL,0,'2024-08-16 15:38:05','2024-08-16 15:38:05',0,0,'["java","男","单身"]',55.000000,55.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('蓝朽','lanxiu','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/b9c71748-6386-45b4-af16-0bc621d7aa88.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 17:04:29','2024-08-16 17:14:09',0,0,'["单身"]',12.000000,12.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('qwe','1210920202','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'69766ab76699c0a8603d68be5af90fe9',NULL,NULL,0,'2024-08-16 17:08:36','2024-08-16 17:09:11',0,0,'["java"]',150.000000,23.000000,0,0,0,0,10);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('李代辉','202383060033','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 17:15:25','2024-08-16 17:15:25',0,0,'["单身"]',18.000000,18.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('小D','lxcAdmin','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 17:46:28','2024-08-16 17:46:28',0,0,'["java"]',111.000000,11.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('刘六六','liuxu0906','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b3948e2186a16dfb40e79a37bb89a052',NULL,NULL,0,'2024-08-16 19:01:55','2024-08-16 19:03:20',0,0,'["java","大三","男","单身"]',128.000000,36.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('dapixiu','1870476411','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'a54c42ee0dfe91317ecbd7564f12cfd4',NULL,NULL,0,'2024-08-16 20:43:45','2024-08-16 20:43:45',0,0,'["java","大四","男","单身"]',80.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('忧郁的炸鱼薯条','zhang','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-16 21:39:58','2024-08-16 21:39:58',0,0,'["java","大三","男","单身"]',22.000000,33.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('ASDF','ASDF','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-17 01:04:49','2024-08-17 01:04:49',0,0,'["c++","大二","男","有对象"]',0.000000,0.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('jack','aaaaa','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-17 03:35:24','2024-08-29 02:30:52',0,0,'["java"]',111.000000,11.000000,0,0,0,0,20),
                                                                                                                                                                                                                           ('影','1911354806','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'8a9cf739be5da7d15f70646623d7149c',NULL,NULL,0,'2024-08-17 10:07:26','2024-08-17 10:09:15',0,0,'["java","大二","男","内卷"]',90.000000,50.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('张三','2926895420','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'8a9cf739be5da7d15f70646623d7149c',NULL,NULL,0,'2024-08-17 10:11:48','2024-08-17 10:11:48',0,0,'["java","大二","男","内卷"]',90.000000,50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('tslotfh','usernamey','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-17 12:40:09','2024-08-17 12:40:09',0,0,'["男","女"]',100.000000,80.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('263055','263055','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'3cbb931c68876d836fc2e27a2e06259b',NULL,NULL,0,'2024-08-17 21:49:24','2024-08-17 21:49:24',0,0,'["大四","java","单身","男"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('hh','xuhu','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-18 08:49:16','2024-08-18 08:49:16',0,0,'["java","大三","男","单身"]',45.000000,45.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('ziio','ziio1234','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-18 15:56:47','2024-08-25 21:40:38',0,0,'["java","大二","女","已婚","有对象","单身"]',0.000000,0.000000,0,0,0,0,20),
                                                                                                                                                                                                                           ('123','11111111111111111','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-19 07:06:13','2024-08-19 07:06:13',0,0,'["java"]',22.000000,40.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('123123123','123123123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'e12d07d53b7ca2f83ea3cd398bdd7032',NULL,NULL,0,'2024-08-19 11:51:21','2024-08-19 11:51:21',0,0,'["java"]',113.000000,28.210000,0,0,0,0,0),
                                                                                                                                                                                                                           ('王','1234','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-19 20:45:14','2024-08-19 20:45:14',0,0,'["java"]',123.000000,13.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('yjh','yjh123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-08-19 23:46:25','2024-09-24 21:47:48',0,0,'["java"]',100.000000,23.000000,0,0,0,0,30),
                                                                                                                                                                                                                           ('Hank','genghongjie','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'253ed3a4dbcf4181020f2c5183ffc728',NULL,NULL,0,'2024-08-20 10:45:20','2024-08-20 10:45:20',0,0,'["go","已婚","男"]',7.000000,7.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('yjh','yjh2003','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6c401d1824f9397ba9be002bdff1b4d4',NULL,NULL,0,'2024-08-22 08:15:10','2024-08-22 08:15:10',0,0,'["java"]',80.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('lpl123','1900130204','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'4ec98eeb4eee2d18ac712d654fd759a3',NULL,NULL,0,'2024-08-22 13:25:20','2024-08-22 13:25:20',0,0,'["java","大四","男","单身","emo","内卷"]',111.000000,56.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('ps','coderps123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'c8dc212dfecc086fac0810fbf42f55dd',NULL,NULL,0,'2024-08-23 14:04:59','2024-08-23 14:04:59',0,0,'["go","大四"]',116.410000,39.900000,0,0,0,0,0),
                                                                                                                                                                                                                           ('小小嘛喽','小小嘛喽','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',0,'buff 叠满','7d9da79cd8a79c46a005aa955acacde3',NULL,NULL,0,'2024-08-26 14:03:43','2024-08-26 14:09:22',0,0,'["java","c++","go","嵌入式","python","大一","大二","大三","大四","研一","研二","男","女","内卷","emo","已婚","有对象","单身"]',116.110000,39.440000,0,0,0,0,0),
                                                                                                                                                                                                                           ('gzxzxg','1907916094','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'53b302f4ff0570af198a0c3d40ec2fb7',NULL,NULL,0,'2024-08-28 20:43:28','2024-08-28 20:43:28',0,0,'["java","男","研三","单身"]',50.000000,50.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('卡芙卡','琴-qin','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-08-29 14:45:11','2024-08-29 14:45:11',0,0,'["java"]',3.000000,3.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('菠菜','ymym113','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'5734eef69c7f70d1340b8151927a63b8',NULL,NULL,0,'2024-08-29 17:05:11','2024-08-29 17:05:11',0,0,'["java","c++","go","嵌入式","python","研三","男","单身"]',66.000000,66.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('懒惰','lazy','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/f9b176b6-d87f-4a43-b5cf-19b5c6e75465.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc','02081191483',NULL,0,'2024-09-01 23:20:29','2024-09-01 23:23:18',0,0,'["java"]',99.000000,45.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('测试用户007001','13087650000','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'7d1e7265f480a2bdf6a9adbee8d384e9',NULL,NULL,0,'2024-09-04 10:26:16','2024-09-04 10:26:16',0,0,'["java"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('hxy','13697778238','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'3e6fc5a387ecb07193cf841639b0639d',NULL,NULL,0,'2024-09-04 15:26:59','2024-09-04 15:26:59',0,0,'["java","c++"]',1.000000,2.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('驴哥','hahahe','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-04 21:55:23','2024-09-04 21:55:23',0,0,'["java","大二","女","已婚"]',111.000000,11.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('阿卡','hwbtest','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-05 10:22:22','2024-09-05 10:27:04',0,0,'["java","大四","男","单身"]',12.000000,12.000000,0,0,0,0,10);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('zrinlive','zrinlive','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,'。。。','cfaa1ba6ec9e1bec947afd7f996bb6ba',NULL,NULL,0,'2024-09-05 14:56:14','2024-09-05 14:59:05',0,0,'["男","单身","python"]',120.719039,31.262042,0,0,0,0,10),
                                                                                                                                                                                                                           ('admin1','admin1','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'e12d07d53b7ca2f83ea3cd398bdd7032',NULL,NULL,0,'2024-09-05 15:10:50','2024-09-05 15:13:09',0,0,'["java"]',11.000000,11.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('11111111','1111111111111','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fc9bc66e1bea0754e895ca9b05e8b5b4',NULL,NULL,0,'2024-09-06 11:59:23','2024-09-06 11:59:23',0,0,'["java"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('lalala','hejiajun123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6b058f9c9e54d13a75257265363deaa5',NULL,NULL,0,'2024-09-06 15:09:15','2024-09-06 15:09:15',0,0,'["java","go","研一","女","emo"]',0.000000,0.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('黄金耳料','Vic123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-07 13:18:14','2024-09-07 13:18:14',0,0,'["java","大四","男","单身"]',80.000000,60.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('mahua','mahua','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-08 15:19:41','2024-09-08 15:21:49',0,0,'["java","大一","男","单身"]',120.000000,60.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('以晴','yiqin','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'93be3cdcd629d66cec0be699c090749b',NULL,NULL,0,'2024-09-09 10:18:40','2024-09-09 10:18:40',0,0,'["java","大一","男","单身"]',24.000000,24.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('以晴s','yiqins','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'26e31145d535e44ee9c4e29498df3588',NULL,NULL,0,'2024-09-09 10:22:11','2024-09-09 10:22:11',0,0,'["java","大一"]',24.000000,24.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('YeYe','leaf','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-09 19:10:57','2024-09-14 15:57:57',0,0,'["java"]',152.000000,45.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('jack','2878951910','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-10 16:23:03','2024-09-10 16:23:41',0,0,'["java","大三","男","有对象"]',0.000000,0.000000,0,0,0,0,10);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('songgt','songgt','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-10 17:16:53','2024-09-10 17:18:21',0,0,'["java","大四","男","已婚"]',0.000000,0.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('jack','may20242','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-11 16:38:30','2024-09-11 16:39:43',0,0,'["java","大三","男"]',0.000000,0.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('dsj','dsjdsj','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-12 09:41:34','2024-09-12 09:41:34',0,0,'["男"]',180.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('生理氯化钠','sllhdog1','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-12 12:55:09','2024-09-12 12:55:09',0,0,'["c++"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('tiani','2668309123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'27f4154ea1f8839178814aeef83236e8',NULL,NULL,0,'2024-09-12 15:01:45','2024-09-12 15:04:08',0,0,'["java","嵌入式"]',2.000000,2.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('wlh55','wlh55','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-13 09:30:24','2024-09-13 09:30:24',0,0,'["java","大三","女","已婚"]',23.000000,21.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('高桥凉介','高桥凉介','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'20d08a02fa42e622199949999a4981e9',NULL,NULL,0,'2024-09-13 10:30:59','2024-09-13 10:30:59',0,0,'["java","大四","男","单身"]',22.000000,22.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('hawk','hawk','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'4c68253dd9e530c91a0369872b57dbf3',NULL,NULL,0,'2024-09-13 14:59:34','2024-09-13 14:59:34',0,0,'["python"]',45.000000,45.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('444','4444','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'6f71e71c33070f42fda6481d1324ecf0',NULL,NULL,0,'2024-09-13 16:53:22','2024-09-13 16:54:26',0,0,'["go","c++"]',112.000000,44.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('adminadmin','adminadminadmin','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/c69cace9-3e12-4814-88dd-7539d3d404b6.jpg',1,NULL,'3925efcbfe9850b1f8a1aa9bbe59a3dc',NULL,NULL,0,'2024-09-14 10:22:12','2024-09-26 19:43:28',0,0,'["java","python","男","emo"]',121.497063,31.187198,1,120,0,0,20);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('无名','liushuo','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'647441e85feeb498e98e0f4a7775589b',NULL,NULL,0,'2024-09-14 10:45:19','2024-09-14 10:45:19',0,0,'["java","大一","男","有对象"]',160.000000,60.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('zxr123','zxr123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-14 17:09:20','2024-09-14 17:09:20',0,0,'["c++"]',123.000000,12.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('abc123','abc123','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'9b93a2a7cf81eb57bf5cc49e3005ca7a',NULL,NULL,0,'2024-09-14 17:10:25','2024-09-14 17:10:25',0,0,'["java"]',111.000000,11.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('delete','delete','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'519b8961080ee0855a23f544a3f2229a',NULL,NULL,0,'2024-09-15 10:01:21','2024-09-15 10:01:21',0,0,'["java","大一","女","单身"]',90.000000,45.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('delete1','delete1','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'519b8961080ee0855a23f544a3f2229a',NULL,NULL,0,'2024-09-15 10:03:21','2024-09-15 10:03:21',0,0,'["java","大一","男","单身"]',90.000000,45.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('wan666','wan666','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'35adc1104c1fac7a2d27359c9fe4915b',NULL,NULL,0,'2024-09-17 00:42:33','2024-09-17 00:42:33',0,0,'["java","大一","男","单身"]',30.000000,80.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('xuanyuan','xuanyuan','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-17 11:34:04','2024-09-17 11:34:04',0,0,'["java"]',0.000000,0.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('源sir','875730607','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'3e6fc5a387ecb07193cf841639b0639d',NULL,NULL,0,'2024-09-17 15:57:37','2024-09-17 15:57:37',0,0,'["大四","男","有对象","java"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('satata','satata','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-19 15:46:33','2024-09-19 15:46:33',0,0,'["python"]',83.000000,43.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('非凡','feifan','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/3e222172-82b8-43da-8e82-7d0a7aeb6a2f.png',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-23 18:49:27','2024-09-23 18:56:35',0,0,'["java"]',0.000000,0.000000,0,0,0,0,10);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
                                                                                                                                                                                                                           ('2233','233256','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'27470323a6e7fff59b629c4183e33d4e',NULL,NULL,0,'2024-09-23 23:32:09','2024-09-23 23:32:09',0,0,'["java"]',2.000000,2.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('Recal','1989424334','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'7f7581ff2c2b34668285020c606abfdf',NULL,NULL,0,'2024-09-24 04:15:52','2024-09-24 04:15:52',0,0,'["java","python","大四","男","单身"]',114.000000,30.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('liangbin','liangbin','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'c2d652ea3d3ae87a57be38589ccceb88',NULL,NULL,0,'2024-09-24 09:49:59','2024-09-24 09:49:59',0,0,'["java"]',-9.000000,45.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('yun138','yun138','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-24 16:15:37','2024-09-24 16:15:37',0,0,'["java"]',25.000000,25.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('xxxx','xxxx','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-24 16:41:14','2024-09-25 18:46:10',0,0,'["嵌入式","java","c++","go","python","研二"]',1.000000,1.000000,1,119,0,0,10),
                                                                                                                                                                                                                           ('ww','741852','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-24 20:22:21','2024-09-24 20:22:21',0,0,'["java"]',20.000000,20.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('Len','a0173248','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'59f3cdb10e0aa60f7effbf494ab5f123',NULL,NULL,0,'2024-09-25 09:32:33','2024-09-25 09:34:12',0,0,'["大四","男","emo","内卷"]',0.000000,0.000000,0,0,0,0,10),
                                                                                                                                                                                                                           ('222','2222','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'fb7df5c79a72303d1b14d2efe47d2cfc',NULL,NULL,0,'2024-09-25 11:34:35','2024-09-25 11:34:35',0,0,'["java","c++","go","python","嵌入式"]',20.000000,20.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('zhou3306','zhou3306','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-25 18:45:32','2024-09-25 18:45:32',0,0,'["java"]',1.000000,1.000000,0,0,0,0,0),
                                                                                                                                                                                                                           ('wqwe','homie111','https://www.keaitupian.cn/cjpic/frombd/0/253/936677050/470164789.jpg',1,NULL,'a7c469e8dc0369303b7be407475c5efc',NULL,NULL,0,'2024-09-26 14:10:22','2024-09-26 14:10:22',0,0,'["java","go","python","大二","男","已婚"]',113.000000,22.000000,0,0,0,0,0);
INSERT INTO hjj.`user` (username,userAccount,avatarUrl,gender,profile,userPassword,phone,email,userStatus,createTime,updateTime,isDelete,userRole,tags,longitude,dimension,blogNum,blogViewNum,followNum,fanNum,score) VALUES
    ('巴斯奎亚','Basukiya','https://hejiajun-img-bucket.oss-cn-wuhan-lr.aliyuncs.com/hm/8a37c451-a319-480f-9496-b0cd425f0ac7.png',1,NULL,'b84de60a71368ddf3112f8b784502894',NULL,NULL,0,'2024-09-26 19:43:06','2024-09-26 19:45:15',0,0,'["java","大四","男","emo","有对象","内卷"]',100.000000,45.000000,0,0,0,0,10);





INSERT INTO hjj.user_team (userId,teamId,joinTime,createTime,updateTime,isDelete) VALUES
                                                                                      (1,15,'2024-07-17 22:46:44','2024-07-17 22:46:43','2024-07-17 22:46:43',0),
                                                                                      (1,16,'2024-07-17 22:47:20','2024-07-17 22:47:19','2024-07-17 22:47:19',0),
                                                                                      (5879,15,'2024-07-18 17:57:12','2024-07-18 17:57:11','2024-07-18 17:57:11',0),
                                                                                      (5882,15,'2024-07-20 15:30:33','2024-07-20 15:30:33','2024-07-20 15:30:33',0),
                                                                                      (5883,15,'2024-07-21 18:18:48','2024-07-21 18:18:48','2024-07-21 18:18:48',0),
                                                                                      (5888,17,'2024-07-25 14:09:21','2024-07-25 14:09:20','2024-07-25 14:09:20',0),
                                                                                      (5921,18,'2024-08-13 22:20:16','2024-08-13 22:20:15','2024-08-13 22:20:15',0),
                                                                                      (5930,18,'2024-08-14 20:36:10','2024-08-14 20:36:10','2024-08-14 20:36:10',0),
                                                                                      (5931,18,'2024-08-14 21:11:13','2024-08-14 21:11:12','2024-08-14 21:11:12',0),
                                                                                      (5934,19,'2024-08-15 17:10:55','2024-08-15 17:10:55','2024-08-15 17:10:55',0);
INSERT INTO hjj.user_team (userId,teamId,joinTime,createTime,updateTime,isDelete) VALUES
                                                                                      (5937,19,'2024-08-16 11:20:13','2024-08-16 11:20:13','2024-08-16 11:20:13',0),
                                                                                      (5938,19,'2024-08-16 11:22:26','2024-08-16 11:22:26','2024-08-16 11:22:26',0),
                                                                                      (5946,20,'2024-08-16 11:30:29','2024-08-16 11:30:29','2024-08-16 11:30:29',0),
                                                                                      (5944,20,'2024-08-16 11:30:55','2024-08-16 11:30:54','2024-08-16 11:30:54',0),
                                                                                      (5952,20,'2024-08-16 11:34:48','2024-08-16 11:34:48','2024-08-16 11:34:48',0),
                                                                                      (5953,20,'2024-08-16 11:39:46','2024-08-16 11:39:46','2024-08-16 11:39:46',0),
                                                                                      (5955,20,'2024-08-16 11:43:54','2024-08-16 11:43:53','2024-08-16 11:43:53',0),
                                                                                      (5955,16,'2024-08-16 11:45:17','2024-08-16 11:45:16','2024-08-16 11:45:16',0),
                                                                                      (5956,20,'2024-08-16 11:48:43','2024-08-16 11:48:42','2024-08-16 11:48:42',0),
                                                                                      (5970,21,'2024-08-16 14:42:35','2024-08-16 14:42:35','2024-08-16 14:42:35',0);
INSERT INTO hjj.user_team (userId,teamId,joinTime,createTime,updateTime,isDelete) VALUES
                                                                                      (5970,16,'2024-08-16 14:43:18','2024-08-16 14:43:17','2024-08-16 14:43:17',0),
                                                                                      (5974,21,'2024-08-16 17:05:50','2024-08-16 17:05:49','2024-08-16 17:05:49',0),
                                                                                      (1,21,'2024-08-16 17:09:20','2024-08-16 17:09:19','2024-08-16 17:09:19',0),
                                                                                      (5936,22,'2024-08-18 21:24:49','2024-08-18 21:24:49','2024-08-23 18:21:39',1),
                                                                                      (5993,22,'2024-08-19 07:10:13','2024-08-19 07:10:13','2024-08-23 18:21:39',1),
                                                                                      (5992,23,'2024-08-20 13:05:12','2024-08-20 13:05:11','2024-08-20 13:05:11',0),
                                                                                      (5989,24,'2024-08-21 18:49:26','2024-08-21 18:49:26','2024-08-21 18:49:26',0),
                                                                                      (5989,23,'2024-08-21 18:58:43','2024-08-21 18:58:42','2024-08-28 09:32:55',1),
                                                                                      (5989,25,'2024-08-28 13:29:45','2024-08-28 13:29:44','2024-08-28 13:29:44',0),
                                                                                      (5992,24,'2024-08-29 00:01:57','2024-08-29 00:01:57','2024-08-29 00:01:57',0);
INSERT INTO hjj.user_team (userId,teamId,joinTime,createTime,updateTime,isDelete) VALUES
                                                                                      (6008,23,'2024-08-29 15:01:00','2024-08-29 15:01:00','2024-08-29 15:01:00',0),
                                                                                      (6011,23,'2024-09-01 23:21:23','2024-09-01 23:21:23','2024-09-01 23:21:23',0),
                                                                                      (6014,24,'2024-09-04 10:29:12','2024-09-04 10:29:12','2024-09-04 10:29:12',0),
                                                                                      (6016,23,'2024-09-04 23:14:31','2024-09-04 23:14:31','2024-09-04 23:14:31',0),
                                                                                      (6019,26,'2024-09-05 15:12:39','2024-09-05 15:12:39','2024-09-05 15:12:39',0),
                                                                                      (6023,26,'2024-09-08 19:26:02','2024-09-08 19:26:02','2024-09-08 19:26:07',1),
                                                                                      (6034,26,'2024-09-11 16:39:32','2024-09-11 16:39:31','2024-09-11 16:39:31',0),
                                                                                      (6037,26,'2024-09-12 15:02:50','2024-09-12 15:02:50','2024-09-12 15:02:50',0),
                                                                                      (6046,27,'2024-09-14 10:24:39','2024-09-14 10:24:39','2024-09-14 10:24:39',0),
                                                                                      (5949,27,'2024-09-14 16:05:23','2024-09-14 16:05:23','2024-09-14 16:05:23',0);
INSERT INTO hjj.user_team (userId,teamId,joinTime,createTime,updateTime,isDelete) VALUES
                                                                                      (6053,27,'2024-09-15 10:04:39','2024-09-15 10:04:39','2024-09-15 10:04:39',0),
                                                                                      (6052,27,'2024-09-15 10:05:38','2024-09-15 10:05:38','2024-09-15 10:05:38',0),
                                                                                      (5936,27,'2024-09-20 13:04:04','2024-09-20 13:04:03','2024-09-20 13:04:03',0),
                                                                                      (6029,28,'2024-09-23 16:49:07','2024-09-23 16:49:06','2024-09-23 16:49:06',0),
                                                                                      (5949,29,'2024-09-23 18:15:51','2024-09-23 18:15:50','2024-09-23 18:15:50',0),
                                                                                      (6068,30,'2024-09-24 03:35:47','2024-09-24 03:35:46','2024-09-24 03:35:46',0),
                                                                                      (6073,29,'2024-09-24 16:42:41','2024-09-24 16:42:41','2024-09-24 16:42:41',0),
                                                                                      (6073,28,'2024-09-24 16:42:42','2024-09-24 16:42:42','2024-09-24 16:42:42',0),
                                                                                      (6073,27,'2024-09-24 16:42:43','2024-09-24 16:42:43','2024-09-24 16:42:43',0),
                                                                                      (5996,29,'2024-09-24 21:55:50','2024-09-24 21:55:50','2024-09-24 21:55:50',0);
INSERT INTO hjj.user_team (userId,teamId,joinTime,createTime,updateTime,isDelete) VALUES
                                                                                      (5996,28,'2024-09-24 21:55:55','2024-09-24 21:55:55','2024-09-24 21:55:55',0),
                                                                                      (5996,27,'2024-09-24 21:56:16','2024-09-24 21:56:15','2024-09-24 21:56:15',0),
                                                                                      (5996,31,'2024-09-24 22:46:01','2024-09-24 22:46:01','2024-09-24 22:46:01',0),
                                                                                      (5936,31,'2024-09-25 17:58:05','2024-09-25 17:58:05','2024-09-25 17:58:05',0);
