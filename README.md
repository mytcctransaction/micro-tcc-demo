# Distributed TCC Transaction Framework - micro-tcc (1.2.5.RELEASE)

[![Maven](https://img.shields.io/badge/endpoint.svg?url=https://github.com/mytcctransaction/micro-tcc)](https://github.com/mytcctransaction/micro-tcc)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mytcctransaction/micro-tcc/master/LICENSE)

## Modules
1. micro-tcc-demo-common: *Distributed Transaction demo common*
2. micro-tcc-dubbo-*: *Dubbo demo*   
3. micro-tcc-springcloud-*: *SpringCloud demo*   
4. micro-tcc-ek:*Eureka Server*
## Summary
    micro-tcc 是基于Zookeeper（协调者）+Redis 分布式事务中间件，支持SpringCloud Feign、Dubbo、RestTemplate
    micro-tcc 支持事务同步和异步调用方式，发生异常的事务会定时自动恢复，如果超过最大恢复次数，建议手动恢复
    Zookeeper 作为分布式事务协调者，它负责协调各个子系统的事务状态和事务确认、提交、回滚
    redis 作为事务的存储方式
    代码完全开源，欢迎大家start！

## Start

### 项目配置

1，在各个项目的resources目录下，配置数据库连接，本项目采用mysql数据库

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

重试次数

transaction.recover.maxRetryCount=15

重试间隔，重试时间=重试间隔*重试次数

transaction.recover.recoverDuration=100

job cron表达式

transaction.recover.cronExpression=0 */2 * * * ?
 
### 使用

1，分别启动dubbo或者SpringCloud a、b、c 三个项目，SpringCloud还需要首先启动micro-tcc-ek 项目

启动类类似：DubboServiceAApplication... 

2，打开浏览器，输入如下地址：

http://127.0.0.1:8881/micro_tcc?value=1

查看数据库，会查看到有三条记录

3，模拟异常情况，浏览器打开如下地址

http://127.0.0.1:8881/micro_tcc?value=1&ex=1

查看数据库，发现没有一条记录

恭喜，测试成功！

## 注意要点

1，每个方法在加上@TccTransaction 同时一般也要加上@Transaction

2，方法都要抛出异常，不建议捕获异常，抛出异常tcc事务才能拦截到

3，confirmMethod、cancelMethod 必须在业务上保证幂等性，框架暂不实现幂等

4，confirmMethod 是异步执行，建议前端做异步处理。如果要同步处理结果，那可以不用confirmMethod方法，即是这个方法不做任何业务处理

框架以后会支持confirmMethod 同步处理

5，方法参数最好有一个全局唯一id，方便业务做幂等、查找数据等操作
 
6，zookeeper 生产环境必须做集群，而且起码3个节点以上

## The Authority
Website: [https://github.com/mytcctransaction/micro-tcc](https://github.com/mytcctransaction/micro-tcc)  
Statistics: [Leave your company messages](https://github.com/mytcctransaction/micro-tcc)  
QQ 群：246539015 (Hot) 
作者 QQ:306750639