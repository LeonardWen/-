package com.ys.springboot.demo.utils;

public class ReturnCode {

    public final static Integer SUCCESS_CODE = 0;

    public final static Integer SYSTEM_ERROR_CODE = 9999; //系统错误

    public final static Integer SERVICE_ERROR_CODE = -1;//业务错误

    public final static Integer PARAMETER_ERROR_CODE = -2; //参数有误


    public final static String SUCCESS_MSG = "返回成功！";

    public final static String SYSTEM_ERROR_MSG = "系统错误！";

    public final  static String SERVICE_ERROR_MSG = "业务错误！";

    public final static String PARAMETER_ERROR_MSG = "参数有误！";


}
