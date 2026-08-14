package com.cc.blogserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.blogserver.domain.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}
