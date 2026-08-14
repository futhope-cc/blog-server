package com.cc.blogserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.blogserver.domain.FileRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {
}
