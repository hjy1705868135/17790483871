/**
 * 无人机服务接口（Service层）
 * 定义无人机业务逻辑操作的契约
 * 采用接口与实现分离的设计模式，降低耦合度
 */
package com.md.basePlatform.service;

// 导入无人机实体类
import com.md.basePlatform.entity.Drone;

// 导入Java集合框架的List接口
import java.util.List;

/**
 * 无人机服务接口
 * 提供无人机业务逻辑处理的方法定义
 * Service层作为业务逻辑层，负责处理数据校验、事务管理和业务规则
 */
public interface DroneService {

    /**
     * 获取所有无人机列表
     * 
     * @return 返回包含所有无人机的List集合
     */
    List<Drone> getAllDrones();

    /**
     * 根据ID获取单个无人机
     * 
     * @param id 无人机的唯一标识ID
     * @return 返回匹配的无人机对象，若不存在则返回null
     */
    Drone getDroneById(Long id);

    /**
     * 根据名称搜索无人机
     * 支持模糊搜索，不区分大小写
     * 
     * @param name 名称关键词
     * @return 返回匹配条件的无人机List集合
     */
    List<Drone> searchByName(String name);

    /**
     * 根据型号搜索无人机
     * 支持模糊搜索，不区分大小写
     * 
     * @param model 型号关键词
     * @return 返回匹配条件的无人机List集合
     */
    List<Drone> searchByModel(String model);

    /**
     * 保存无人机（新增操作）
     * 自动设置创建时间和默认状态
     * 
     * @param drone 要保存的无人机对象
     * @return 返回保存后的无人机对象（包含自动生成的ID）
     */
    Drone save(Drone drone);

    /**
     * 更新无人机信息
     * 自动更新修改时间
     * 
     * @param drone 包含更新数据的无人机对象（必须包含ID）
     * @return 返回更新后的无人机对象，若记录不存在则返回null
     */
    Drone update(Drone drone);

    /**
     * 删除无人机
     * 根据ID删除对应的记录
     * 
     * @param id 要删除的无人机ID
     */
    void delete(Long id);

    /**
     * 获取无人机总数
     * 
     * @return 返回无人机的总数量
     */
    int count();
}