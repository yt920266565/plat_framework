package com.yanmade.plat.framework.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yanmade.plat.framework.entity.ApiResponse;
import com.yanmade.plat.framework.enums.ErrMsgEnum;
import com.yanmade.plat.framework.util.ApiResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 系统异常处理
     * 
     * @param e
     * @return
     */
    @ExceptionHandler
    public ApiResponse<Object> exceptionHandler(Exception e) {
        logger.error("", e);
        return ApiResponseUtil.exception(HttpStatus.INTERNAL_SERVER_ERROR, null, printStackTraceToString(e));
    }

    /**
     * 系统异常处理
     * 
     * @param e
     * @return
     */
    @ExceptionHandler(value = CustomException.class)
    public ApiResponse<Object> myExceptionHandler(CustomException e) {
        logger.error("", e);
        return ApiResponseUtil.failure(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getErrCode(), e.getErrMsg());
    }

    /**
     * 方法参数校验异常 Validate
     * 
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ApiResponse<Object> validateExceptionHandler(HttpServletRequest request, ConstraintViolationException e) {
        logger.error("", e);
        Set<ConstraintViolation<?>> set = e.getConstraintViolations();
        String code = "";
        String message = "";
        if (set != null) {
            for (ConstraintViolation<?> constraintViolation : set) {
                if (constraintViolation != null) {
                    // 取注解的message属性
                    code = constraintViolation.getMessage();
                    Path path = constraintViolation.getPropertyPath();

                    // 被验证的参数
                    String field = path.toString().split("\\.")[1];

                    // 请求参数的值
                    String value = request.getParameter(field);

                    // 传给前端的消息格式 非法参数：id=-1
                    message = ErrMsgEnum.parse(code).getErrMsg() + ":" + field + "=" + value;
                    break;
                }
            }
        }

        return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, code, message);
    }

    /**
     * Bean 校验异常 Validate
     * 
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResponse<Object> methodArgumentValidationHandler(HttpServletRequest request,
            MethodArgumentNotValidException ex) {

        logger.error("", ex);

        ObjectError error = ex.getBindingResult().getAllErrors().get(0);

        // 取注解的message属性
        String code = error.getDefaultMessage();

        DefaultMessageSourceResolvable resolvable = (DefaultMessageSourceResolvable) error.getArguments()[0];

        // 被验证的参数
        String field = resolvable.getDefaultMessage();

        // 请求参数的值
        String value = request.getParameter(field);

        // 传给前端的消息格式 非法参数：id=-1
        String message = ErrMsgEnum.parse(code).getErrMsg() + ":" + field + "=" + value;

        return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, code, message);
    }

    /**
     * 绑定异常
     * 
     * @param request
     * @param pe
     * @return
     */
    public ApiResponse<Object> bindException(HttpServletRequest request, BindException ex) {
        ObjectError error = ex.getAllErrors().get(0);

        // 取注解的message属性
        String code = error.getDefaultMessage();

        DefaultMessageSourceResolvable resolvable = (DefaultMessageSourceResolvable) error.getArguments()[0];

        // 被验证的参数
        String field = resolvable.getDefaultMessage();

        // 请求参数的值
        String value = request.getParameter(field);

        // 传给前端的消息格式 非法参数：id=-1
        String message = ErrMsgEnum.parse(code).getErrMsg() + ":" + field + "=" + value;

        return ApiResponseUtil.failure(HttpStatus.BAD_REQUEST, null, code, message);
    }

    /**
     * 异常转成字符串
     * 
     * @param t
     * @return
     */
    public String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

}
