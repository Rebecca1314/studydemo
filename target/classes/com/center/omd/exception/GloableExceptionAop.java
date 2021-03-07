package com.center.omd.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//@RestControllerAdvice
@ControllerAdvice
public class GloableExceptionAop {
    /**
     * 拦截运行时异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String runTimeException(){
        return "运行时出现异常----------相当于aop捕获异常";
    }


}
