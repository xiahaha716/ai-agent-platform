package com.example.aiagentplatform.controller;

import com.example.aiagentplatform.common.Result; // 确保你的 Result 类在这个包下
import com.example.aiagentplatform.service.MyAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = "application/json;charset=utf-8")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MyAiAssistant assistant;

    // 保留原样：专门用于流式对话的接口 (SseEmitter 不走 Result 包装)
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam("message") String message) {
        // 1. 创建一个 SseEmitter 对象，超时时间设为 3 分钟 (180000毫秒)
        SseEmitter emitter = new SseEmitter(180000L);

        // 2. 调用 AI 的流式接口
        assistant.chat("1", message)
                .onNext(token -> {
                    // 每当大模型蹦出一个字 (token)，就立刻通过 emitter 推送给前端
                    try {
                        emitter.send(token);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onComplete(response -> {
                    // AI 说完了，主动关闭连接
                    emitter.complete();
                })
                .onError(error -> {
                    // 出错了，把错误信息丢给前端
                    emitter.completeWithError(error);
                })
                .start(); // 别忘了点火启动！

        return emitter; // 立刻把这个“通道”对象返回给前端，不要等 AI 思考
    }

    // ➕ 新增：常规的非流式接口，用于测试 Result<T> 和全局异常拦截
    @GetMapping("/chat/history")
    public Result<List<String>> getChatHistory(@RequestParam(value = "sessionId", required = false) String sessionId) {

        // 【异常测试触发点】
        // 如果前端没有传 sessionId，或者传了 "error"，我们手动抛出一个异常
        // 此时控制台会打印错误日志，而前端会收到 GlobalExceptionHandler 包装好的 500 JSON，而不是 Tomcat 报错页面
        if (sessionId == null || "error".equals(sessionId)) {
            throw new IllegalArgumentException("Session ID 不合法，触发异常拦截测试！");
        }

        // 【正常业务逻辑】
        // 模拟从数据库 (MyPersistentChatMemoryStore) 获取到的历史记录
        List<String> mockHistory = Arrays.asList(
                "User: 帮我查一下Lumina图书馆的借阅规则",
                "AI: 好的，根据图书馆规章制度，每次最多借阅 5 本图书..."
        );

        // 规范返回：使用 Result.success() 包装真实数据
        return Result.success(mockHistory);
    }
}
