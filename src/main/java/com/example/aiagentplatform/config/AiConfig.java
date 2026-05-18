package com.example.aiagentplatform.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import java.io.File;
import java.time.Duration;

@Configuration
public class AiConfig {

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.chat-model.model-name:deepseek-chat}")
    private String modelName;

    // 【之前写的】1. 注册流式对话模型引擎
    @Bean
    public StreamingChatLanguageModel streamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(60))
                .build();
    }

    // 【今晚的重头戏】2. 注册 RAG 知识检索器 (ContentRetriever)
    @Bean
    public ContentRetriever contentRetriever() throws Exception {
        // 核心步骤 1：利用 Java IO 和 Tika 解析器，加载我们在 resources 下写的本地文件
        File file = ResourceUtils.getFile("classpath:library_rules.txt");
        Document document = FileSystemDocumentLoader.loadDocument(file.toPath(), new ApacheTikaDocumentParser());

        // 核心步骤 2：加载刚刚好不容易下载完的“本地轻量级向量模型”
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();

        // 核心步骤 3：初始化一个轻量级的内存向量数据库
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 核心步骤 4：构建数据摄入管道（Ingestor）
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                // 面试高频点：按 300 字切块，并且保留 50 字的重叠（Overlap），防止一句话被从中间劈开导致语义丢失
                .documentSplitter(DocumentSplitters.recursive(300, 50))
                .embeddingModel(embeddingModel) // 用本地模型把文字变成向量
                .embeddingStore(embeddingStore) // 存入数据库
                .build();

        // 轰隆隆...执行切分和存库！
        ingestor.ingest(document);

        // 核心步骤 5：封装检索器，交给 Spring 管理
        // Spring Boot 检测到这个 Bean 后，会自动把它装配给你的 @AiService
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2) // 每次回答前，最多去库里捞取最相关的 2 句话
                .minScore(0.5) // 相似度及格线，低于 0.5 匹配度的废话不要
                .build();
    }
}
