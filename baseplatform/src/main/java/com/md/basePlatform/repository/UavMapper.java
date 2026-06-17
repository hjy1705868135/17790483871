package com.md.basePlatform.repository;

import com.md.basePlatform.domain.Uav;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 无人机 MyBatis Mapper。
 */
@Mapper
public interface UavMapper {

    /**
     * 插入一条无人机记录。
     *
     * @param uav 实体
     * @return 影响行数
     */
    int insert(Uav uav);

    /**
     * 按主键更新。
     *
     * @param uav 实体
     * @return 影响行数
     */
    int update(Uav uav);

    /**
     * 按主键删除。
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(@Param("id") long id);

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 实体或 {@code null}
     */
    Uav findById(@Param("id") long id);

    /**
     * 条件统计总数（分页用）。
     *
     * @param keyword 关键字（编号/型号/厂商模糊），可为空
     * @param status 状态精确匹配，可为空
     * @return 总数
     */
    long countSearch(@Param("keyword") String keyword, @Param("status") String status);

    /**
     * 条件分页查询。
     *
     * @param keyword 关键字，可为空
     * @param status 状态，可为空
     * @param offset 偏移
     * @param limit 条数
     * @return 列表
     */
    List<Uav> searchPage(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 统计编号出现次数（新建校验）。
     *
     * @param code 编号
     * @return 数量
     */
    int countByCode(@Param("code") String code);

    /**
     * 统计编号出现次数（排除指定 id，用于更新校验）。
     *
     * @param code 编号
     * @param id 排除的主键
     * @return 数量
     */
    int countByCodeExcludeId(@Param("code") String code, @Param("id") long id);
}
