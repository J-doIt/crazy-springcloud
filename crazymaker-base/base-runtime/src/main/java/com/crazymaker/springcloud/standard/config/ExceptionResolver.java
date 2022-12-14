package com.crazymaker.springcloud.standard.config;


import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.result.RestOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeoutException;

/**
 * ExceptionResolver
 */
@Slf4j
@RestControllerAdvice
public class ExceptionResolver
{



    /**
     * 其他异常
     */
    private static final String OTHER_EXCEPTION_MESSAGE = "其他异常";
    /**
     * 业务异常
     */
    private static final String BUSINESS_EXCEPTION_MESSAGE = "业务异常";


    /**
     * 业务异常处理
     *
     * @param request 请求体
     * @param e       {@link BusinessException}
     * @return RestOut
     */
    @Order(1)
    // 以json格式返回
    @ResponseBody
    // 捕获Exception类型的异常
    @ExceptionHandler(BusinessException.class)
    // 自定义浏览器返回状态码
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestOut<String> businessException(HttpServletRequest request, BusinessException e)
    {
        log.info(BUSINESS_EXCEPTION_MESSAGE + ":" + e.getErrMsg());
        return RestOut.error(e.getErrMsg());
    }


    @Order(2)
    // 捕获Exception类型的异常
    @ExceptionHandler(value = Exception.class)
    // 自定义浏览器返回状态码
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestOut<String> defaultException(Exception ex)
    {
        log.error("系统内部异常: ", ex);
        if (ex instanceof MethodArgumentNotValidException)
        {
            StringBuilder sb = new StringBuilder();
            ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors()
                    .forEach(fe -> sb.append(",").append(fe.getField()).append(": ").append(fe.getDefaultMessage()));
            sb.delete(0, 1);
            return RestOut.error(RestOut.STATUS_ERROR, sb.toString());
        } else if (ex instanceof BindException)
        {
            StringBuilder sb = new StringBuilder();
            ((BindException) ex).getBindingResult().getFieldErrors()
                    .forEach(fe -> sb.append(",").append(fe.getField()).append(": ").append(fe.getDefaultMessage()));
            sb.delete(0, 1);
            return RestOut.error(RestOut.STATUS_ERROR, sb.toString());

        } else if (ex instanceof IllegalAccessException)
        {
            return RestOut.error(RestOut.STATUS_ERROR, "访问权限不够！"+ex.getMessage());
        } else if (ex instanceof TimeoutException)
        {
            return RestOut.error(RestOut.STATUS_ERROR, "访问超时！"+ex.getMessage());
        } else if (ex instanceof IllegalStateException)
        {
            return RestOut.error(RestOut.STATUS_ERROR, "状态非法！"+ex.getMessage());
        } else if (ex instanceof DataIntegrityViolationException)
        {
            return RestOut.error(RestOut.STATUS_ERROR, "数据非法！"+ex.getMessage());
        } else
        {

//            return Result.error(GlobalStatusEnum.SYS_ERROR.getCode(), GlobalStatusEnum.SYS_ERROR.getDesc());
            return RestOut.error(RestOut.STATUS_ERROR, ex.getMessage());
        }
    }
}
