package com.ys.springboot.demo.Controller;


import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/ys")
public class HelloWordController {

    @RequestMapping(value = "/hello",method =  RequestMethod.GET)
    @ResponseBody
    public String  sayHello(HttpServletRequest request){
        return "Hello Word";
    }

}
