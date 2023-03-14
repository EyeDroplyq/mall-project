# mall-project
基于Spring Boot+Spring Cloud的分布式交易系统：校易通（校级比赛项目）
项目简介：
这个项目是我参加校级比赛的项目，这个项目的目的是让本学校内的同学能有一个自由交易的平台，可以将自己不用的闲置物品在这个平台上进行交易，而不用去各种交易群，这个网站和app的出现就是为了方便同一个学校的学生进行交易的。
1.技术选型:Spring Boot+Spring Cloud+ElasticSearch+Vue+Element UI+Redis+Mybatis+MySQL+MQ
2.根据业务需求抽取对应的VO和TO等
3.使用JUC并发编程实现了异步任务编排提高了业务实现的效率，大大提高了系统吞吐量
4.使用Spring Session解决了Session的持久化以及自定义Cookie来解决Cookie的作用域问题
5.后台系统使用JSR303进行数据校验，使得数据的保存更加安全和规范
6.使用ElasticSearch的DSL查询实现了数据检索功能
7.使用RabbitMQ的延时队列实现了订单的定时关单功能
8.使用 OAuth2.0协议整合微博登录、对密码使用加盐MD5算法加密保存
9.使用JMeter压测工具对每一个模块进行压力测试以及使用Java性能监控工具对JVM的参数进行监测
10.对JVM的参数-Xmn进行了调整，适当增加了新生代内存大小，使得系统性能更加优化
11.采用 Nginx  前端部署 和 反向代理 ，增强了软件系统的 安全性
