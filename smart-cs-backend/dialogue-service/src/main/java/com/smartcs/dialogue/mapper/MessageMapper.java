package com.smartcs.dialogue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcs.dialogue.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
