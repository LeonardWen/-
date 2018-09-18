package com.ys.springboot.demo.utils;

import java.io.Serializable;

public class ReturnData implements Serializable {

    private int code;

    private String returnMsg;

    private Object data;

    public ReturnData() {
    }

    public ReturnData(int code, String returnMsg, Object data) {
        this.code = code;
        this.returnMsg = returnMsg;
        this.data = data;
    }

    /**
     * 返回成功
     * @return
     */
    public static  ReturnData returnSuccess(Object data ){
        return new ReturnData(ReturnCode.SUCCESS_CODE,ReturnCode.SUCCESS_MSG,data);
    }

    /**
     * 系统错误
     * @return
     */
    public static  ReturnData returnSystemError(){
        return new ReturnData(ReturnCode.SYSTEM_ERROR_CODE,ReturnCode.SYSTEM_ERROR_MSG,null);
    }

    /**
     * 参数有误
     * @return
     */
    public static ReturnData returnParmeterError(){
        return new ReturnData(ReturnCode.PARAMETER_ERROR_CODE,ReturnCode.PARAMETER_ERROR_MSG,null);
    }

    /**
     * 参数有误
     * @param errorMsg
     * @return
     */
    public static ReturnData returnParmeterError(String errorMsg){
        return new ReturnData(ReturnCode.PARAMETER_ERROR_CODE,errorMsg,null);
    }

    /**
     * 业务出错
     * @return
     */
    public static ReturnData returnServiceError(){
        return new ReturnData(ReturnCode.PARAMETER_ERROR_CODE,ReturnCode.PARAMETER_ERROR_MSG,null);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
