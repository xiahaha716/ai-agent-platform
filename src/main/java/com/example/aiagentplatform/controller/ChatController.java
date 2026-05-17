package com.example.aiagentplatform.controller;

import com.example.aiagentplatform.service.MyAiAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MyAiAssistant assistant;

    // ➕ 新增：专门用于流式对话的接口
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
}




