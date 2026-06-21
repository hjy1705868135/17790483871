package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {
}