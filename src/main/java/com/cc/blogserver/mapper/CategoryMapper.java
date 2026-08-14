package com.cc.blogserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.blogserver.domain.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
