package com.cc.blogserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cc.blogserver.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
