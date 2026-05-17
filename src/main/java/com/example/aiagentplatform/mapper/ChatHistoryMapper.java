package com.example.aiagentplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiagentplatform.entity.ChatHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatHistoryMapper extends BaseMapper<ChatHistory> {
    
    @Select("SELECT * FROM chat_history WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatHistory> selectListBySessionId(String sessionId);
    
    @Delete("DELETE FROM chat_history WHERE session_id = #{sessionId}")
    void deleteBySessionId(String sessionId);
}

