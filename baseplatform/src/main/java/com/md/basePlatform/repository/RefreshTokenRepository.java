package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * 刷新令牌数据访问接口
 */
@Mapper
public interface RefreshTokenRepository extends BaseMapper<RefreshToken> {
}