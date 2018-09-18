package com.ys.springboot.demo.config;

import com.ys.springboot.demo.interceptor.LoginIntercepter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

public class CustomWebMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginIntercepter()).addPathPatterns("/**")
                 .excludePathPatterns("/login")
                 .excludePathPatterns("/authImg")
                 .excludePathPatterns("/plugins/bootstrap/bootstrap.css")
                 .excludePathPatterns("/css/style_v1.css")
                 .excludePathPatterns("/loginvalidate");  //对来自/user/** 这个链接来的请求进行拦截
    }


}
