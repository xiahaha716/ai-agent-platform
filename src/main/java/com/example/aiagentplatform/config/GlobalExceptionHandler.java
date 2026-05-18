package com.example.aiagentplatform.config;

import com.example.aiagentplatform.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 拦截 controller 层抛出的所有 Exception 异常
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        // 记录真实的错误堆栈信息到控制台或日志文件，方便后端排查
        log.error("系统内部异常: ", e);

        // 给前端返回脱敏的、友好的 JSON 提示
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}
