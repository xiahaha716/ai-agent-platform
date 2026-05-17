package com.example.aiagentplatform.store;

import com.example.aiagentplatform.entity.ChatHistory;
import com.example.aiagentplatform.mapper.ChatHistoryMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.util.List;
import java.util.stream.Collectors;

public class MyPersistentChatMemoryStore implements ChatMemoryStore {

    private final ChatHistoryMapper chatHistoryMapper;
    private final String sessionId;

    public MyPersistentChatMemoryStore(ChatHistoryMapper chatHistoryMapper, String sessionId) {
        this.chatHistoryMapper = chatHistoryMapper;
        this.sessionId = sessionId;
    }

    // 核心：AI 说话前，从数据库加载历史记录
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        System.out.println(">>> [正在从数据库读取记忆], SessionID: " + sessionId);
        // 这里需要你根据自己的实体类字段调整查询逻辑
        return chatHistoryMapper.selectListBySessionId(sessionId).stream()
                .map(ChatHistory::toChatMessage) // 将你的实体类转为 LangChain4j 的消息对象
                .collect(Collectors.toList());
    }

    // 核心：AI 说完话，把新消息存进数据库
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        System.out.println(">>> [正在保存记忆到数据库], SessionID: " + sessionId);
        // 逻辑：清空该 session 旧数据，存入当前所有消息（这是最简单稳妥的做法）
        chatHistoryMapper.deleteBySessionId(sessionId);
        for (ChatMessage message : messages) {
            ChatHistory entity = ChatHistory.fromChatMessage(sessionId, message);
            chatHistoryMapper.insert(entity);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        chatHistoryMapper.deleteBySessionId(sessionId);
    }
}
