# Distributed TCC Transaction Framework - micro-tcc (1.3.5.RELEASE)

[![Maven](https://img.shields.io/badge/endpoint.svg?url=https://github.com/mytcctransaction/micro-tcc)](https://github.com/mytcctransaction/micro-tcc)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mytcctransaction/micro-tcc/master/LICENSE)

# Project
**micro-tcc** 项目链接  （框架核心源码）

https://github.com/mytcctransaction/micro-tcc

_Modules_
1. micro-tcc-tc: *Distributed Transaction Client*
2. micro-tcc-common: *Commons*   

**micro-tcc-demo** 项目链接  （框架使用例子）

https://github.com/mytcctransaction/micro-tcc-demo

_Modules_

1. micro-tcc-demo-common: *Distributed Transaction demo common*
2. micro-tcc-dubbo-*: *Dubbo demo*   
3. micro-tcc-springcloud-*: *SpringCloud demo*   
4. micro-tcc-ek:*Eureka Server*

## Summary
    micro-tcc 是基于Zookeeper（协调者）+Redis 分布式事务中间件，支持SpringCloud 、Dubbo、RestTemplate
    micro-tcc 支持事务同步和异步调用方式，发生异常的事务会定时自动恢复，如果超过最大恢复次数，建议手动恢复
    Zookeeper 作为分布式事务协调者，它负责协调各个子系统的事务状态和事务确认、提交、回滚
    redis 作为事务日志的存储方式
    代码完全开源，欢迎大家start！
    
## Start
### 主要流程

 设计流程图：

 ![](https://github.com/mytcctransaction/micro-tcc-demo/blob/master/micro-tcc-demo-common/src/main/resources/img/tcc-flow.jpg)
 
 拦截器会拦截所有业务方法，把参与同一个事务的微服务加入事务组
 
 全局事务ID 基于UUID 生成，通过拦截器会在所有微服务中传播
 
 事务在Try 阶段没有发生异常，Zookeeper通知事务参与者执行Confirm 方法
 
 事务在Confirm 阶段没有异常，则这个事务结束
 
 事务在Confirm阶段发生异常，Zookeeper 通知事务参与者执行Rollback 方法
 
 事务Rollback 阶段发生异常，不做任何操作，防止进入死循环里，后续异常事务通过定时任务恢复
 
### 项目配置

1，首先在pom 文件添加以下依赖包：
  
<dependency>

    <groupId>com.github.mytcctransaction</groupId>

    <artifactId>micro-tcc-tc</artifactId>
    
    <version>1.3.5</version>
 
</dependency>


其次在各个项目的resources目录下，配置数据库连接，本项目采用mysql数据库

spring.datasource.driver-class-name=com.mysql.jdbc.Driver

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/micro-tcc?characterEncoding=UTF-8&serverTimezone=UTC

spring.datasource.username=abc1

spring.datasource.password=123456

2，数据库建表脚本

在common项目的resources目录下micro-tcc.sql文件，新建一个micro-tcc数据库，并执行它

3，配置zookeeper、redis连接

micro.tcc.coordinator.ip=127.0.0.1:2181

dubbo项目还需要配置

dubbo.registry.address=127.0.0.1:2181

dubbo.scan.basePackages=org.micro.tcc.demo.dubbo

dubbo.consumer.filter=dubboConsumerContextFilter

dubbo.provider.filter=dubboProviderContextFilter

配置redis连接

spring.redis.host: 127.0.0.1

spring.redis.port: 6379

spring.redis.timeout: 10000

4，micro-tcc 事务使用配置

4.1 在SpringBoot 启动类加上@EnableMicroTccTransaction 注解，表示使用micro-tcc管理分布式事务，如下面：

@EnableMicroTccTransaction

public class SpringServiceAApplication {

    public static void main(String[] args) throws Exception {
    
        SpringApplication.run(SpringServiceAApplication.class, args);
        
    }
    
}

4.2 在service 要进行事务管理的方法上加上

 @TccTransaction(confirmMethod = "confirmMethod",cancelMethod = "cancelMethod")
 
 @TccTransaction ：表示开启tcc事务
 
 confirmMethod ：确认方法
 
 cancelMethod ：取消方法
 
 那么在该service类里应该加上confirmMethod、cancelMethod，如下面：
 
 public void confirmMethod(args...){}
 
### 事务恢复配置

最大重试次数

micro.tcc.transaction.recover.maxRetryCount=15

重试间隔

micro.tcc.transaction.recover.recoverDuration=100

重试时间=重试间隔*重试次数

job cron表达式

micro.tcc.transaction.recover.cronExpression=0 */2 * * * ?
 
### 使用

1，分别启动dubbo或者SpringCloud a、b、c 三个项目，SpringCloud还需要首先启动micro-tcc-ek 项目

启动类类似：DubboServiceAApplication... 

2，打开浏览器，输入如下地址：

http://127.0.0.1:8881/micro_tcc?value=1

查看数据库，会查看到有三条记录

3，模拟异常情况，浏览器打开如下地址

http://127.0.0.1:8881/micro_tcc?value=1&ex=1

查看数据库，发现没有一条记录

恭喜，测试成功！欢迎大家加群交流

## 分布式事务最终一致性机制

Micro-tcc 在执行Confirm、Rollback方法前都会预先在redis记录预存事务日志，并把事务进行序列化保存

执行完 Confirm、Rollback方法成功后会执行在redis删除预存事务日志 ，不成功不会删除事务日志

如果Confirm、Rollback方法发生异常，定时器会查找没有删除的事务日志，并反序列化事务对象，执行Confirm、Rollback方法进行恢复事务

如果恢复超过一定次数（次数可配置），则建议人工手动恢复，中间件不会再执行恢复

## 性能测试

机器配置：

cpu：Inter Core i5-8600 @ 3.10GHZ

内存： 16 GB

设备有限只有一台机子做测试，这台机子安装了mysql、zookeeper、redis、JMeter

测试里所有服务器也是运行在这台机子上

JMeter 线程设置：并发20个线程，持续运行2分钟，观察TPS

1，完全不启用事务的情况下，关闭log日志，测试结果

 ![](https://github.com/mytcctransaction/micro-tcc-demo/blob/master/micro-tcc-demo-common/src/main/resources/img/ori.png)

发现事务TPS在210 左右

2,使用micro-tcc 进行分布式事务管理，关闭log日志，测试结果

 ![](https://github.com/mytcctransaction/micro-tcc-demo/blob/master/micro-tcc-demo-common/src/main/resources/img/new.png)
 
 发现事务TPS在110 左右

3，结论：未启用分布式事务，也没有做任何事务补偿情况下TPS是210，如果手动做了事务补偿之类操作，这个值会更低，估计TPS值在170左右

下降率公式：下降率=（未启tcc事务TPS - 启用tcc事务TPS）/ 未启tcc事务TPS * 100%

得出总体下降率在40%左右

## 注意要点

1，每个方法在加上@TccTransaction 同时一般也要加上@Transaction

2，方法都要抛出异常，不建议捕获异常，抛出异常tcc事务才能拦截到

3，confirmMethod、cancelMethod 必须在业务上保证幂等性，框架暂不实现幂等

4，confirmMethod 是异步执行，建议前端做异步处理。如果要同步处理结果，那可以不用confirmMethod方法，即是这个方法不做任何业务处理

5，方法参数最好有一个全局唯一id，方便业务做幂等、查找数据等操作
 
6，zookeeper 生产环境必须做集群，而且起码3个节点以上

## The Authority
Website: [https://github.com/mytcctransaction/micro-tcc](https://github.com/mytcctransaction/micro-tcc)  
Statistics: [Leave your company messages](https://github.com/mytcctransaction/micro-tcc)  
QQ 群：246539015 (Hot) 
作者 QQ:306750639