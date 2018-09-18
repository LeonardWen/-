package com.ys.springboot.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations={"classpath:spring/spring-activiti.xml"})
public class ConfigClass {


}
