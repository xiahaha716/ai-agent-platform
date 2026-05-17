package com.example.aiagentplatform.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("chat_history")
public class ChatHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sessionId;
    private String msgRole;
    private String content;
    private LocalDateTime createTime;
    
    // 将数据库实体转换为 LangChain4j 的 ChatMessage
    public ChatMessage toChatMessage() {
        if ("USER".equalsIgnoreCase(msgRole) || "user".equalsIgnoreCase(msgRole)) {
            return UserMessage.from(content);
        } else {
            return AiMessage.from(content);
        }
    }
    
    // 从 LangChain4j 的 ChatMessage 创建数据库实体
    public static ChatHistory fromChatMessage(String sessionId, ChatMessage message) {
        ChatHistory history = new ChatHistory();
        history.setSessionId(sessionId);
        history.setMsgRole(message.type().name());
        history.setContent(message.text());
        history.setCreateTime(LocalDateTime.now());
        return history;
    }
}

