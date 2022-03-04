package com.atguigu.yygh.common.handler;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 全局异常处理类
 */
@Slf4j
//@ControllerAdvice + @ResponseBody
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一返回一致的返回结果： 全局统一异常处理
     *
     * @param ex 异常对象
     */
    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception ex) {
        // 实际开发中，一般会把异常记录到日志中
        log.error("出现全局异常了，异常信息为: " + ex.getMessage());
        return R.error().message("出现异常了!");
    }

    /**
     * 处理RuntimeException异常 运行时异常
     *
     * @param ex 异常对象
     */
    @ExceptionHandler(value = RuntimeException.class)
    public R handlerRuntimeException(RuntimeException ex) {
        // 实际开发中，一般会把异常记录到日志中
        log.error("出现运行时异常了，异常信息为: " + ex.getMessage());
        return R.error().message("出现运行时异常了!");
    }

    /**
     * 处理SQLException异常 SQL异常
     *
     * @param ex 异常对象
     */
    @ExceptionHandler(value = SQLException.class)
    public R handlerSQLException(SQLException ex) {
        // 实际开发中，一般会把异常记录到日志中
        log.error("出现SQL异常了，异常信息为: " + ex.getMessage());
        return R.error().message("出现SQL异常了!");
    }

    /**
     * 处理ArithmeticException异常 数学异常
     *
     * @param ex 异常对象
     */
    @ExceptionHandler(value = ArithmeticException.class)
    public R handlerArithmeticException(ArithmeticException ex) {
        // 实际开发中，一般会把异常记录到日志中
        log.error("出现数学异常了，异常信息为: " + ex.getMessage());
        return R.error().message("出现数学异常了!");
    }

    /**
     * 处理自定义异常
     *
     * @param ex 异常对象
     */
    @ExceptionHandler(value = YYGHException.class)
    public R handlerYYGHException(YYGHException ex) {
        // 实际开发中，一般会把异常记录到日志中
        log.error("出现自定义异常了，异常信息为: " + ex.getMessage());
        return R.error().message(ex.getMessage()).code(ex.getCode());
    }

}
