package com.yang.face.aop;

import com.yang.face.constant.enums.MessageEnum;
import com.yang.face.entity.show.MessageVO;
import com.yang.face.entity.show.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * @author yangyuyang
 * @date 2020/3/13 13:43
 */
//@RestControllerAdvice
public class GlobalExceptionHandle {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    public Response exceptionHandler(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder sOut = new StringBuilder("系统异常信息:" + e.getClass().getName() + "\r\n");
        // 打印堆栈信息 前 5 条
        for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
            sOut.append("\tat ").append(stackTrace[i]).append("\r\n");
        }
        logger.error("{}", sOut);
        return Response.show(new MessageVO(MessageEnum.FAIL, "系统错误"));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response validExceptionHandle(MethodArgumentNotValidException e) {
        //日志记录错误信息
        logger.error("传参异常-{}", Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
        //将错误信息返回给前台
        return Response.show(new MessageVO(MessageEnum.FAIL, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage()));
    }
}
