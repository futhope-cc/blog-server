package com.cc.blogserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.blogserver.domain.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
