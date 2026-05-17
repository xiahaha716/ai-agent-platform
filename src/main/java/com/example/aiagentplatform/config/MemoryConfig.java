package com.example.aiagentplatform.config;

import com.example.aiagentplatform.store.MyPersistentChatMemoryStore;
import com.example.aiagentplatform.mapper.ChatHistoryMapper;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoryConfig {

    @Bean
    public ChatMemoryProvider chatMemoryProvider(ChatHistoryMapper chatHistoryMapper) {
        // 现在，每个 Session 都会关联一个 PersistentChatMemoryStore
        return sessionId -> MessageWindowChatMemory.builder()
                .id(sessionId)
                .maxMessages(10)
                .chatMemoryStore(new MyPersistentChatMemoryStore(chatHistoryMapper, sessionId.toString()))
                .build();
    }
}
