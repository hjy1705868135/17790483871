项目开发需求具体如下：
1 技术栈
（1）系统环境
      Java EE 8
      Servlet 3.0
      Apache Maven 3
（2）主框架
      Spring Boot 2.2.x
      Spring Framework 5.2.x
      Apache Shiro 1.7
（3）持久层
      Apache MyBatis 3.5.x
      Hibernate Validation 6.0.x
      Alibaba Druid 1.2.x
（4）视图层
      Bootstrap 3.3.7
      Thymeleaf 3.0.x
2 支持SQLite，MySQL数据库，并可以自由切换；
3 业务功能：
   无人机信息管理，包括无人机信息的录入，查询，删除和修改，无人机的属性由AI自动生成；
4 基础功能设计上参考“RuoYi”系统；
5 服务层，数据操作层设计上接口和实现分别放在不同包；
6 拦截器：建立拦截器单独的包，将各类拦截器放到该包，执行时打印请求信息；
7 设计上尽量采用注解，减少对xml配置文件的依赖；
8 根据上面要求，还有遗漏的请补充。