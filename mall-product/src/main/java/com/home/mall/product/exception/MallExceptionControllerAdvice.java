package com.home.mall.product.exception;

import com.home.common.exception.BizErrorEnum;
import com.home.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: lyq
 * @createDate: 13/1/2023
 * @version: 1.0
 */
@Slf4j
//controllerAdvice就是用来处理异常的一个注解,并且可以指定要处理哪个包下面的异常
@RestControllerAdvice(basePackages = "com.home.mall.product.controller")
public class MallExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handlerValidException(MethodArgumentNotValidException e){
        //获取到异常
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        //对异常进行封装，得到最终的返回结果
        Map<String,String> errorMap=new HashMap<>();
        for (FieldError item : fieldErrors) {
            String field = item.getField();
            String message = item.getDefaultMessage();
            errorMap.put(field,message);
        }
        return R.error(BizErrorEnum.VALID_EXCEPTION.getCode(),BizErrorEnum.VALID_EXCEPTION.getMsg()).put("data",errorMap);
    }

    //其他异常的处理
    @ExceptionHandler(Throwable.class)
    public R handlerException(Throwable throwable){
        log.error(throwable.getLocalizedMessage());
        log.error(throwable.getCause().getMessage());
        return R.error(BizErrorEnum.VALID_EXCEPTION.getCode(),throwable.getMessage());
//        return R.error(BizErrorEnum.VALID_EXCEPTION.getCode(),BizErrorEnum.UNKNOWN_EXCEPTION.getMsg());
    }
}
