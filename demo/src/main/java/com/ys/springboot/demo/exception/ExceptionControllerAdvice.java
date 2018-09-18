package com.ys.springboot.demo.exception;

import com.ys.springboot.demo.utils.ReturnCode;
import com.ys.springboot.demo.utils.ReturnData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 应用到所有@RequestMapping
     * 注解方法，在其执行之前初始化数据绑定器
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {}

    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     * @param model
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("author", "Magical Sam");
    }

    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public ReturnData errorHandler(Exception ex) {
        ReturnData returnData = new ReturnData();
        returnData.setCode(ReturnCode.SYSTEM_ERROR_CODE);
        returnData.setReturnMsg(ReturnCode.SYSTEM_ERROR_MSG);
        logger.error("系统错误：{}",ex.getMessage());
        return returnData;
    }

}
