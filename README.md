# 欢迎一一起来学，一起交流

> 点击下面的链接，成为仓库成员

https://gitee.com/crazymaker/crazy-springcloud/invite_link?invite=975433315f080451b6f69f066a74ff6cb5ae2fc946cdbcb930bab53ef47d10669aa05c155a0274b863ce7e16acfba504

---

<font   size=3 face="黑体"  > <font   size=3 face="黑体"  > 疯狂创客圈 Java 高并发【 亿级流量聊天室实战】实战系列 【[博客园总入口](https://www.cnblogs.com/crazymakercircle/p/9904544.html) 】</font >

<font   size=3 face="黑体"  > 架构师成长+面试必备之 高并发基础书籍 【[Netty Zookeeper Redis 高并发实战](https://www.cnblogs.com/crazymakercircle/p/11397271.html) 】</font >

------
# 前言

**Crazy-SpringCloud 微服务脚手架 &视频介绍**：

Crazy-SpringCloud 微服务脚手架，是为  Java 微服务开发 入门者 准备的 学习和开发脚手架。并配有一系列的使用教程和视频，大致如下：

**高并发 环境搭建** 图文教程和演示视频，陆续上线：

| 中间件                          | 链接地址                                                     |
| ------------------------------- | ------------------------------------------------------------ |
| Linux  Redis 安装（带视频）     | [Linux Redis 安装（带视频）](https://www.cnblogs.com/crazymakercircle/p/11985983.html) |
| Linux  Zookeeper 安装（带视频） | [Linux Zookeeper 安装, 带视频](https://www.cnblogs.com/crazymakercircle/p/12006500.html) |
| Windows  Redis 安装（带视频）   | [Windows  Redis 安装（带视频） ](https://www.cnblogs.com/crazymakercircle/p/11973314.html) |
| RabbitMQ  离线安装（带视频）    | [RabbitMQ  离线安装（带视频） ](https://www.cnblogs.com/crazymakercircle/p/11992763.html) |
| ElasticSearch 安装, 带视频      | [ElasticSearch 安装, 带视频 ](https://www.cnblogs.com/crazymakercircle/p/12001292.html) |
| Nacos  安装（带视频）           | [Nacos  安装（带视频） ](https://www.cnblogs.com/crazymakercircle/p/11992539.html) |

**Crazy-SpringCloud 微服务脚手架**   图文教程和演示视频，陆续上线：

| 组件                                          | 链接地址                                                     |
| --------------------------------------------- | ------------------------------------------------------------ |
| Eureka                                        | [Eureka 入门，带视频](https://www.cnblogs.com/crazymakercircle/p/12043538.html) |
| SpringCloud Config                            | [springcloud Config 入门，带视频](https://www.cnblogs.com/crazymakercircle/p/12043604.html) |
| spring security                               | [spring security 原理+实战](https://www.cnblogs.com/crazymakercircle/p/12040402.html) |
| Spring Session                                | [SpringSession 独立使用](https://www.cnblogs.com/crazymakercircle/p/12038664.html) |
| **分布式 session  基础**                      | [RedisSession （自定义）](https://www.cnblogs.com/crazymakercircle/p/12038208.html) |
| **重点： springcloud 开发脚手架**             | [springcloud 开发脚手架](https://www.cnblogs.com/crazymakercircle/p/12041568.html) |
| SpingSecurity + SpringSession 死磕 （写作中） | [SpingSecurity + SpringSession 死磕](https://www.cnblogs.com/crazymakercircle/p/12037584.html) |

小视频以及所需工具的**百度网盘链接**，请参见 [疯狂创客圈 高并发社群 博客](https://www.cnblogs.com/crazymakercircle/p/9904544.html) 



## 1  Crazy-SpringCloud 微服务脚手架之： Spring Cloud 、Spring Boot版本选项

Spring Cloud是基于Spring Boot构建的，其版本也是有对应关系的，在构建项目时，注意版本之间的对应关系，版本对不上会有问题。

| Spring Cloud | Spring Boot |
| ------------ | ----------- |
| Camden       | 1.4.x       |
| Dalston      | 1.5.x       |
| Edgware      | 1.5.x       |
| Finchley     | 2.0.x       |
| Greenwich    | 2.1.x       |
| Hoxton       | 2.2.x       |

Spring Cloud 包含了一系列的子组件，如 Spring Cloud Config、Spring Cloud Netflix、Spring Cloud Openfeign等，为了防止与这些子组件的版本号混淆，Spring Cloud 的版本号全部使用英文单词形式命名。具体来说 Spring Cloud 的版本号使用了英国伦敦地铁站的名称来命名，并按字母 A-Z 次序发布版本，其第一个版本叫做 "Angel"，第二个版本叫做"Brixton" ，依次类推。另外，在解决了一个严重的 BUG 后，Spring Cloud 会发布一个 "service Release" 版本（小版本），简称 SRX 版本，其中X是顺序的编号， 比如 "Finchley.SR4" 是 Finchley 版本的第 4 个小版本。

很多时候，大家做技术选型时，非常喜欢选用最高版本来开发，但是对于 Spring 全家桶的选择来说，高版本不一定是最佳选择。比如，目前最高的 Spring Cloud Hoxton 版本是基于 Spring Boot 2.2 构建，Spring Boot 2.2 又基于 Spring Framework 5.2 构建，也就是说，这是一次整体的大版本的升级。大家的在项目上都会用到非常多、非常多的第三方组件，总会有一些组件没有来得及做配套升级而不能兼容 Spring Boot 2.2 或 Spring Framework 5.2，如果贸然整体升级，会给项目开发带来各种各样的莫名奇妙的疑难杂症、甚至是线上的潜在 Bug。除此之外，Spring Cloud 高版本大量推荐了不少自家新组件，但是这些新组件没有经过大规模的使用，其功能尚待完善，以负载均衡组件为例，Spring Cloud Hoxton 所推荐的自家组件 spring-cloud-loadbalancer 在功能上和 ribbon 相比，就弱得非常、非常多。

实际上，Spring Cloud Finchley 到 Greenwich 版本的升级很小，可以说微乎其微，主要是做了 JAVA 11 的兼容，而生产场景中 Java 8 才是各大项目的选择主流，毕竟，Java 11 （2019年4月之后的升级补丁）已经不完全免费 。当然， Java 8 和 Java 11 一样，2019年4月之后的补丁版本也面临收费问题，但是 Java 8 自 2014 年 3 月 18 发布起，截止目前已经被使用和维护了很多年，已经是非常的成熟和稳定了。

综上所述，本书推荐并选用 Spring Cloud Finchley  作为学习、研究、和使用的版本，并且推荐使用的子版本为 Finchley.SR4 。具体的 Maven 依赖坐标如下：

```
   <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Finchley.SR4</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>2.0.8.RELEASE</version>
                <scope>import</scope>
                <type>pom</type>
            </dependency>
        </dependencies>
    </dependencyManagement>
```



## 2   crazy-springcloud 微服务开发脚手架

无论是单体应用还是分布式应用，如果从零开始，都会有很多基础性的、重复性的工作需要做，比如用户认证，比如 session 管理等等，有了开发脚手架，这块基础工作就可以省去，直接按照脚手架的规范进行业务模块的开发即可。

笔者看了开源平台的不少开源的脚手架，很少是适用于直接拿来做业务模块开发的，要么封装的过于重量级而不好解耦，要么就是业务模块分包不清晰而不方便开发，所以，本着简洁和清晰的原则，疯狂创客圈推出了自己的微服务开发脚手架 crazy-springcloud，其大致的模块和功能如下图所示。


![1584492399393](https://img-blog.csdnimg.cn/20200318141858534.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NyYXp5bWFrZXJjaXJjbGU=,size_16,color_FFFFFF,t_70)

这里对疯狂创客圈  crazy-springcloud 微服务开发脚手架的模块分包进行说明，具体如下：

```
crazymaker-server     --  根项目
│  ├─cloud-center     --  微服务的基础设施中心
│  │  ├─cloud-eureka      --  注册中心
│  │  ├─cloud-config      --  配置中心
│  │  ├─cloud-zuul        --  网关服务
│  │  ├─cloud-zipkin      -- 监控中心
│  ├─crazymaker-base  -- 公共基础依赖模块
│  │  ├─base-common     -- 普通的公共依赖，如 utils 类的公共方法
│  │  ├─base-redis      -- 公共的 redis 操作模块 
│  │  ├─base-zookeeper  -- 公共的 zookeeper 操作模块
│  │  ├─base-session    -- 分布式 session 模块
│  │  ├─base-auth       -- 基于 JWT + SpringSecurity 的用户凭证与认证模块
│  │  ├─base-runtime    -- 各 provider 的运行时公共依赖，装配的一些通用 Spring IOC Bean 实例
│  ├─crazymaker-uaa   --业务模块: 用户认证与授权
│  │  ├─uaa-api        -- 用户 DTO、Constants 等
│  │  ├─uaa-client     --  用户服务的 Feign 远程客户端
│  │  ├─uaa-provider   -- 用户认证与权限的核心实现，包含controller 层、service层、dao层的代码实现
│  ├─crazymaker-seckill  --业务模块: 秒杀练习
│  │  ├─seckill-api        -- 秒杀 DTO、Constants 等
│  │  ├─seckill-client     -- 秒杀服务的 Feign 远程调用模块
│  │  ├─seckill-provider   -- 秒杀服务核心实现，包含controller层、service层、dao层的代码实现
│  ├─crazymaker-demo    --业务模块: 练习演示
│  │  ├─demo-api        -- 演示模块的 DTO、Constants 等
│  │  ├─demo-client     -- 演示模块的 Feign 远程调用模块
│  │  ├─demo-provider   -- 演示模块的核心实现，包含controller层、service层、dao层的代码实现
```

接下来，对业务模块的分包规范，做一个说明。在业务模块如何分包的问题上，实际上大部分企业都有自己的统一规范，这里从职责清晰、方便维护、能快速定位代码的角度出发，将 crazy-springcloud 微服务开发脚手架的每一个业务模块，分成了  {module}-api、 {module}-client、 {module}-provider 三个子模块，三个子模块的具体介绍如下：

（1）  {module}-api 子模块定义了一些公共的 Constants 业务常量和 DTO 传输对象，该子模块既被业务模块内部依赖，也会被使用该业务模块的外部所依赖；

（2）  {module}-client 子模块定义了一些被外部模块所依赖的 Feign 远程调用客户类，该子模块是专供外部的模块，不能被内部子模块依赖；

（3）  {module}-provider 子模块是整个业务模块的核心，也是一个能够独立启动、运行的服务提供者应用，该模块包含涉及到业务逻辑的 controller层、service层、dao层的完整代码实现。

 crazy-springcloud 微服务开发脚手架在以下几个维度进行了弱化：

（1）有关部署，没有使用 docker 容器而是使用 shell 脚本，对容器进行了弱化。有多方面的原因：一是本脚手架初心是学习，所使用的部署方式为 shell 脚本而不是 docker，方便大家学习 shell 命令和脚本；二是 Java 和 docker 其实整合得很好，可以稍加配置就一键发布，找点资料就可以掌握； 三是生产环境的部署、甚至是整个自动化构建和部署的工作，实际上属于运维工作，都有专门的运维岗位人员去完成，而部署的核心，任然是 shell脚本。

（2）有关监控，没有对链路监控、JVM性能指标、断路器监控做专门的封装和介绍。有多方面的原因：一是监控的软件太多，如果介绍太全，篇幅又不够，介绍太少又不是大家所用到的； 二是其实都是软件的操作说明，原理性的内容比较少，使用视频的形式比文字形式知识传递的效果会更好。疯狂创客圈后续会推出一些微服务监控的视频，请大家关注社群博客。反过来说，对于一个编程高手来说，如果了解 Spring Cloud 核心原理，那些监控组件基本上都是一碟小菜。



# SpringCloud 微服务开发涉及到的中间件

SpringCloud 微服务开发和自验证过程中，大致会涉及到的基础中间件如下：

（1）ZooKeeper

ZooKeeper 是一个分布式的、开放源码的分布式协调应用程序，是大数据框架 Hadoop 和 Hbase的重要组件。在分布式应用中，它能够高可用的提供很多保障数据一致性的能力：分布式锁、选主、分布式命名服务等。crazy-springcloud 脚手架中，高性能的分布式 ID 生成用到了 ZooKeeper 。 有关 ZooKeeper 的原理和使用，请参见《Netty Zookeeper Redis 高并发实战》一书。

（2）Redis

Redis 是一个高性能的缓存数据库。在高并发的场景下，Redis 可以对关系数据库起到很好的补充作用，对提高应用的并发能力和响应速度，可以说举足轻重和至关重要。crazy-springcloud 脚手架中，高性能的分布式 Session 用到了  Redis。  有关 Redis 的原理和使用，请参见《Netty Zookeeper Redis 高并发实战》一书。

（3）Eureka

Eureka 是 Netflix 开发的服务注册和发现框架，本身是一个基于REST的服务，主要用于定位运行在AWS（Amazon 云）的中间层服务，以达到负载均衡和中间层服务故障转移的目的。SpringCloud将它集成在其子项目spring-cloud-netflix中，以实现 SpringCloud 的服务发现功能。

最后，介绍一下开发和自验证过程中，需要启动的依赖服务。

（4）Spring Cloud Config

Spring Cloud Config 是 Spring Cloud全家桶中最早的配置中心，虽然在生产场景中，很多的企业已经使用 Nacos或者 Consul 整合了配置中心功能，但是 Config 依然适用于 Spring Cloud 项目，通过简单的配置即可使用。

（5） Zuul

Zuul 是 Netflix 开源的内部网关，可以和 Eureka、Ribbon、Hystrix 等组件配合使用，Spring Cloud 对Zuul 进行了整合与增强，使用其作为微服务内部网关，负责对给集群内部各个 provider 服务提供者进行 RPC 路由和请求过滤。

（6）Nginx/Openresty

Nginx 是一个高性能的 HTTP 和反向代理 web服务器，是由伊戈尔·赛索耶夫为俄罗斯访问量第二的 Rambler.ru 站点开发web服务器。Nginx 源代码以类 BSD 许可证的形式发布，其第一个公开版本 0.1.0 发布于2004年10月4日，2011年6月1日其1.0.4 版本发布。Nginx  因高稳定性、丰富的功能集、内存消耗少、并发能力强的而闻名全球，目前得到非常广泛的使用，比如说百度、京东、新浪、网易、腾讯、淘宝等都是其用户。OpenResty  是一个基于 Nginx 与 Lua 的高性能 Web 平台，其内部集成了大量精良的 Lua 库、第三方模块以及大多数的依赖项。用于方便地搭建能够处理超高并发、扩展性极高的动态 Web 应用、Web 服务和动态网关。

以上中间件的配置端口，以及部分中间件的安装和使用视频，大致如下表所示。

| 中间件            | 端口 | 安装和使用视频 |
| ------------------ | ---- | ------------------------------------------------------------ |
| Redis              | 6379 | [Linux Redis 安装视频](https://www.cnblogs.com/crazymakercircle/p/11985983.html) |
| zookeeper          | 2181 | [Linux Zookeeper 安装视频](https://www.cnblogs.com/crazymakercircle/p/12006500.html) |
| RabbitMQ       | 3306 | [Linux Zookeeper 安装视频](https://www.cnblogs.com/crazymakercircle/p/12006500.html) |
| cloud-eureka       | 7777 | [Eureka 使用视频](https://www.cnblogs.com/crazymakercircle/p/12043538.html) |
| Spring Cloud Config | 7788 | [springcloud Config  使用视频](https://www.cnblogs.com/crazymakercircle/p/12043604.html) |
| Zuul                | 7799 |                                                              |
| Nginx/Openresty     | 80   ||



# SpringCloud 微服务开发和自验证环境

首先介绍一下开发和自验证的系统选型。

对于大部分的开发人员来说，学习和开发都使用 了 windows 环境，在这种情形下，强烈建议使用虚拟机装载 centos 作为自验证环境，为啥要推荐 centos 呢？

（1）提前暴露生产环境下的问题。 基本上90%以上的生产环境上 Java 应用，都使用的是 Linux环境（如 centos）来部署。使用 centos 作为自验证环境，可以提前暴露在生产环境下的潜在问题，避免在 windows 下没有问题的程序，一旦部署到生产环境就出问题（笔者亲历）；

（2）学习 shell 命令和脚本。在生产环境定位和解决问题，需要用到基础的 shell 命令和脚本；另外， shell 命令和脚本也是 Java 程序员必知必会的面试题。而使用 centos 作为自验证环境，能方便大家学习 shell 命令和脚本。

当然，可以借助一些工具提高开发效率，比如可以通过 vmware  tools 进行 windows 和 centos  之间的工程目录文件夹共享，这一点，在某些所见即所得的开发（比如 Lua 脚本）的开发和调试时，非常方便。 

SpringCloud 微服务开发和自验证环境的设置，涉及到两个方面：

（1) 中间件相关的环境变量配置

（2）主机名称的配置

首先，介绍一下中间件相关的环境变量配置。

对于中间件（含 Eureka、Redis、Mysql等）相关的IP地址、端口、用户账号等信息，一般都直接在应用配置文件中明文编码，其实这些信息建议尽量通过操作系统环境变量的方式进行配置。例如，在 bootstrap.yml 中要对 Eureka 的 IP 进行配置，可以使用环境变量 EUREKA_ZONE_HOST ，具体如下：

这些环境变量，包含 Eureka、Redis、RabbitMq 等服务器的IP地址。如果在bootstrap.yml中要对 Eureka 进行配置，可以使用环境变量 EUREKA_ZONE_HOST ，具体如下：

```
eureka:
  client:
    serviceUrl:
      defaultZone: http://${EUREKA_ZONE_HOST:localhost}:7777/eureka/
```

上面的写法，通过${EUREKA_ZONE_HOST} 表达式去获取 Eureka 的IP地址。并且，环境变量后面跟着一个冒号和一个默认值，表示如果当前系统环境变量中 EUREKA_ZONE_HOST 为空时，就会使用默认值 localhost 来填充了。

通过环境变量配置中间件的信息，有什么好处呢？一是可以使得配置的切换（如IP切换），多了一层灵活性。二是可以做到不用在配置文件总明文编码一些密码之类的敏感信息，多了一层安全性。

crazy-springcloud 微服务开发脚手架用到的环境变量较多，以 centos 的 /etc/profile 的内容为例，大致如下 ：

```
export DB_HOST=192.168.233.128
export REDIS_HOST=192.168.233.128
export EUREKA_ZONE_HOST=192.168.233.128
export RABBITMQ_HOST=192.168.233.128
export ZOOKEEPER_HOSTS=192.168.233.128
```

192.168.233.128 是 笔者的自验证环境 centos  虚拟机 IP， Zookeeper、Redis 、Eureka、Mysql、Nginx 都跑在这台虚拟机上，大家在使用跑 crazy-springcloud 微服务开发脚手架之前，需要进行对应的更改。

然后，再介绍一下有关主机名称的配置。

由于笔者在调试过程中，使用直接访问 IP 时使用 Fiddler 工具抓包和查看报文不方便，所以将 IP 地址在 hosts 文件中，都映射成了主机名称，大致会用到了主机名称如下：

```

127.0.0.1  crazydemo.com 
127.0.0.1  file.crazydemo.com
127.0.0.1  admin.crazydemo.com
127.0.0.1  xxx.crazydemo.com

192.168.233.128  eureka.server
192.168.233.128  zuul.server
192.168.233.128  nginx.server
192.168.233.128  admin.nginx.server
```



# 8.{Crazy-SpringCloud 微服务开发脚手架} 部署视频

| 组件                                          | 链接地址                                                     |
| --------------------------------------------- | ------------------------------------------------------------ |
| Eureka                                        | [Eureka 入门，带视频](https://www.cnblogs.com/crazymakercircle/p/12043538.html) |
| SpringCloud Config                            | [springcloud Config 入门，带视频](https://www.cnblogs.com/crazymakercircle/p/12043604.html) |
| spring security                               | [spring security 原理+实战](https://www.cnblogs.com/crazymakercircle/p/12040402.html) |
| Spring Session                                | [SpringSession 独立使用](https://www.cnblogs.com/crazymakercircle/p/12038664.html) |
| **分布式 session  基础**                      | [RedisSession （自定义）](https://www.cnblogs.com/crazymakercircle/p/12038208.html) |
| **重点： springcloud 开发脚手架**             | [springcloud 开发脚手架](https://www.cnblogs.com/crazymakercircle/p/12041568.html) |
| SpingSecurity + SpringSession 死磕 （写作中） | [SpingSecurity + SpringSession 死磕](https://www.cnblogs.com/crazymakercircle/p/12037584.html) |
| crazymaker-uaa                               | ....ing                                                      |
| crazymaker-seckill                            | ....ing                                                      |
| cloud-zuul                                    | ....ing                                                      |

小视频以及所需工具的**百度网盘链接**，请参见 [疯狂创客圈 高并发社群 博客](https://www.cnblogs.com/crazymakercircle/p/9904544.html) 





## 控制台界面

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200121144259991.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2NyYXp5bWFrZXJjaXJjbGU=,size_16,color_FFFFFF,t_70)


具体，请关注  **Java 高并发研习社群** 【[博客园 总入口](https://www.cnblogs.com/crazymakercircle/p/9904544.html) 】

---

最后，介绍一下疯狂创客圈：**疯狂创客圈，一个Java 高并发研习社群** 【[博客园 总入口](https://www.cnblogs.com/crazymakercircle/p/9904544.html) 】

疯狂创客圈，倾力推出：**面试必备 + 面试必备 + 面试必备** 的基础原理+实战 书籍 《[Netty Zookeeper Redis 高并发实战](https://www.cnblogs.com/crazymakercircle/p/11397271.html)》 

![img](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9pbWcyMDE4LmNuYmxvZ3MuY29tL2Jsb2cvMTQ4NTM5OC8yMDE5MDgvMTQ4NTM5OC0yMDE5MDgyMjIyNTE1NjQyNy05NTY0MjQxMjQuanBn?x-oss-process=image/format,png)

------

##  疯狂创客圈 Java 死磕系列

+ **Java (Netty) 聊天程序【 亿级流量】实战 开源项目实战** 

- **Netty 源码、原理、JAVA NIO 原理**
- **Java 面试题 一网打尽**

- 疯狂创客圈 [**【 博客园 总入口 】**](https://www.cnblogs.com/crazymakercircle/p/9904544.html)
