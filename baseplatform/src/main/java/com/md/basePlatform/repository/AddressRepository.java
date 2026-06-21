package com.md.basePlatform.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.md.basePlatform.domain.Address;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址仓库
 */
@Mapper
public interface AddressRepository extends BaseMapper<Address> {
}
