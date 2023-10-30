# Testcontainers Dapr Module

This repository contains the Testcontainers Dapr Module. The `DaprContainer` allows you to set up Dapr for local development in your Java applications, providing by default an in-memory implementation of the Dapr APIs.

To use this Testcontainer module from your Java application you can add the following dependency to your Maven project: 

```xml
<dependency>
    <groupId>io.diagrid.dapr</groupId>
	<artifactId>testcontainers-dapr</artifactId>
	<version>0.10.x</version>
</dependency>
```

Alternatively, if you are using Spring Boot check the Spring Boot Starter located in this repository: [https://github.com/diagridio/spring-boot-starter-dapr](https://github.com/diagridio/spring-boot-starter-dapr)
