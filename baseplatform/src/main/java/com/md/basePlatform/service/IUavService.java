package com.md.basePlatform.service;

import com.md.basePlatform.domain.PageResult;
import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.domain.UavForm;
import com.md.basePlatform.exception.DuplicateCodeException;
import com.md.basePlatform.exception.UavNotFoundException;

/**
 * 无人机业务接口。
 */
public interface IUavService {

    /**
     * 分页条件查询。
     *
     * @param keyword 关键字（编号/型号/厂商），可为空
     * @param status 状态，可为空
     * @param pageNum 页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<Uav> page(String keyword, String status, int pageNum, int pageSize);

    /**
     * 按 id 查询详情。
     *
     * @param id 主键
     * @return 实体
     * @throws UavNotFoundException 不存在时
     */
    Uav getById(long id);

    /**
     * 新建无人机。
     *
     * @param form 表单
     * @return 持久化后的实体
     * @throws DuplicateCodeException 编号已存在
     */
    Uav create(UavForm form);

    /**
     * 更新无人机。
     *
     * @param form 表单（含 id）
     * @return 更新后的实体
     * @throws UavNotFoundException 记录不存在
     * @throws DuplicateCodeException 编号与其他记录冲突
     */
    Uav update(UavForm form);

    /**
     * 删除无人机。
     *
     * @param id 主键
     * @throws UavNotFoundException 记录不存在
     */
    void delete(long id);
}
