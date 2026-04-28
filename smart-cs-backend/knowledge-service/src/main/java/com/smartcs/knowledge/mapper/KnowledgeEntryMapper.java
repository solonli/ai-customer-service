package com.smartcs.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcs.knowledge.entity.KnowledgeEntry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeEntryMapper extends BaseMapper<KnowledgeEntry> {
}
