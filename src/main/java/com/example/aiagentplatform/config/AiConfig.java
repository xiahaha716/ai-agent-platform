package com.example.aiagentplatform.config;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AiConfig {

    // 精准读取 yaml 中的 langchain4j -> open-ai -> chat-model -> api-key
    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    // 精准读取 yaml 中的 langchain4j -> open-ai -> chat-model -> base-url
    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    // 如果后续需要动态读取模型名称，也可以选配这一行
    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Bean
    public StreamingChatLanguageModel streamingChatModel() {
        // 创建流式输出引擎，直接喂入从 yaml 读取到的变量
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName) // 使用从 yaml 读取到的 "deepseek-chat"
                .timeout(Duration.ofSeconds(60)) // 限制单次网络请求超时时间为 60 秒
                .build();
    }
}
