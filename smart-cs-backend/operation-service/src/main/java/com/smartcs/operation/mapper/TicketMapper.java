package com.smartcs.operation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartcs.operation.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
