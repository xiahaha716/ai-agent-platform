package com.example.aiagentplatform.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage; // 1. 必须导入这个包

import dev.langchain4j.service.TokenStream; // ➕ 导入这个流式对象

@AiService
public interface MyAiAssistant {

    @SystemMessage("你是一个专业的 AI 助手。")
        // ⬇️ 注意：这里的返回值从 String 变成了 TokenStream
    TokenStream chat(@MemoryId String memoryId, @UserMessage String message);
}

