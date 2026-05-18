package com.example.aiagentplatform.service;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LibraryTools {

    private static final Logger log = LoggerFactory.getLogger(LibraryTools.class);

    /**
     * @Tool 注解里面的字符串就是 Prompt 的一部分！
     * 它告诉大模型：这个工具是干什么用的，在什么场景下需要调用它。
     * 大模型会根据用户的提问，自主决定是否触发这个方法。
     */
    @Tool("查询指定用户的当前图书借阅记录。当用户询问自己借了什么书、应还日期等借阅信息时，必须调用此工具。")
    public String getUserBorrowRecord(String userId) {
        // 当大模型决定调用工具时，会自动提取聊天上下文中的 userId 并传给这个方法
        log.info("⚡️ 触发 Function Calling! 大模型提取到的 userId: {}", userId);

        // 这里在真实的生产环境中，应该是通过 MyBatis-Plus 去查 MySQL，
        // 或者通过 OpenFeign 去调用借阅微服务的 RPC 接口。
        // 为了快速跑通，我们先写死模拟数据：
        if ("1001".equals(userId)) {
            return "用户 1001 当前借阅了 2 本书：\n" +
                    "1. 《Effective Java (第三版)》，应还日期：2026-06-01\n" +
                    "2. 《深入理解计算机系统》，应还日期：2026-06-15";
        }

        return "未查询到用户 " + userId + " 的借阅记录，或者用户 ID 错误。";
    }
}
