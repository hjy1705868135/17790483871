/**
 * 无人机服务实现类
 * 实现DroneService接口定义的业务逻辑方法
 * 作为Service层的实现，负责调用Mapper层进行数据访问
 */
package com.md.basePlatform.service.impl;

// 导入无人机实体类
import com.md.basePlatform.entity.Drone;
// 导入无人机Mapper接口
import com.md.basePlatform.mapper.DroneMapper;
// 导入无人机服务接口
import com.md.basePlatform.service.DroneService;
// 导入SLF4J日志框架
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// 导入Spring依赖注入注解
import org.springframework.beans.factory.annotation.Autowired;
// 导入Spring Service注解
import org.springframework.stereotype.Service;
// 导入Spring事务管理注解
import org.springframework.transaction.annotation.Transactional;

// 导入Java集合框架的List接口
import java.util.List;

/**
 * 无人机服务实现类
 * 提供无人机业务逻辑处理的具体实现
 * 使用@Service注解标记，Spring会自动扫描并注册为Bean
 */
@Service
public class DroneServiceImpl implements DroneService {

    /**
     * 日志记录器实例
     * 使用LoggerFactory根据类名创建Logger对象
     * 用于记录业务操作日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DroneServiceImpl.class);

    /**
     * 无人机数据访问接口（Mapper层）
     * 使用@Autowired注解自动注入Mapper实例
     */
    @Autowired
    private DroneMapper droneMapper;

    /**
     * 获取所有无人机列表
     * 
     * @return 无人机列表
     */
    @Override
    public List<Drone> getAllDrones() {
        // 记录调试日志
        logger.debug("获取所有无人机列表");
        // 调用Mapper层方法查询所有记录
        return droneMapper.findAll();
    }

    /**
     * 根据ID获取无人机
     * 
     * @param id 无人机ID
     * @return 无人机对象
     */
    @Override
    public Drone getDroneById(Long id) {
        // 记录调试日志，包含ID参数
        logger.debug("根据ID获取无人机: id={}", id);
        // 调用Mapper层方法根据ID查询
        return droneMapper.findById(id);
    }

    /**
     * 根据名称搜索无人机
     * 
     * @param name 名称关键词
     * @return 无人机列表
     */
    @Override
    public List<Drone> searchByName(String name) {
        // 记录调试日志，包含名称参数
        logger.debug("根据名称搜索无人机: name={}", name);
        
        // 检查关键词是否为空
        if (name == null || name.trim().isEmpty()) {
            // 如果关键词为空，返回所有无人机
            return getAllDrones();
        }
        
        // 调用Mapper层方法进行模糊查询（添加%通配符）
        return droneMapper.findByName("%" + name + "%");
    }

    /**
     * 根据型号搜索无人机
     * 
     * @param model 型号关键词
     * @return 无人机列表
     */
    @Override
    public List<Drone> searchByModel(String model) {
        // 记录调试日志，包含型号参数
        logger.debug("根据型号搜索无人机: model={}", model);
        
        // 检查关键词是否为空
        if (model == null || model.trim().isEmpty()) {
            // 如果关键词为空，返回所有无人机
            return getAllDrones();
        }
        
        // 调用Mapper层方法进行模糊查询（添加%通配符）
        return droneMapper.findByModel("%" + model + "%");
    }

    /**
     * 保存无人机（新增）
     * 使用@Transactional注解标记事务，确保数据一致性
     * 
     * @param drone 无人机对象
     * @return 保存后的无人机对象
     */
    @Override
    @Transactional
    public Drone save(Drone drone) {
        // 记录调试日志，包含无人机名称
        logger.debug("保存无人机: {}", drone.getName());
        
        // 设置默认状态：如果状态为空，设置为ACTIVE（正常运行）
        if (drone.getStatus() == null || drone.getStatus().isEmpty()) {
            drone.setStatus("ACTIVE");
        }
        
        // 设置默认数值：如果数值字段为空，设置为0
        if (drone.getWeight() == null) {
            drone.setWeight(0.0);
        }
        if (drone.getMaxAltitude() == null) {
            drone.setMaxAltitude(0);
        }
        if (drone.getMaxFlightTime() == null) {
            drone.setMaxFlightTime(0);
        }
        if (drone.getMaxSpeed() == null) {
            drone.setMaxSpeed(0.0);
        }
        
        // 自动设置创建和更新时间
        // 使用SimpleDateFormat格式化当前时间为"yyyy-MM-dd HH:mm:ss"格式
        String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        drone.setCreateTime(now);
        drone.setUpdateTime(now);
        
        // 执行插入操作
        droneMapper.insert(drone);
        
        // 记录成功日志，包含ID和名称
        logger.info("无人机保存成功: id={}, name={}", drone.getId(), drone.getName());
        
        // 返回保存后的对象（包含自动生成的ID）
        return drone;
    }

    /**
     * 更新无人机
     * 使用@Transactional注解标记事务，确保数据一致性
     * 
     * @param drone 无人机对象
     * @return 更新后的无人机对象
     */
    @Override
    @Transactional
    public Drone update(Drone drone) {
        // 记录调试日志，包含ID和名称
        logger.debug("更新无人机: id={}, name={}", drone.getId(), drone.getName());
        
        // 自动更新修改时间
        String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        drone.setUpdateTime(now);
        
        // 执行更新操作，获取受影响的行数
        int rows = droneMapper.update(drone);
        
        // 根据受影响行数判断是否更新成功
        if (rows > 0) {
            // 更新成功，记录日志
            logger.info("无人机更新成功: id={}", drone.getId());
            // 返回更新后的完整对象（重新查询确保获取最新数据）
            return droneMapper.findById(drone.getId());
        } else {
            // 更新失败（未找到对应记录），记录警告日志
            logger.warn("无人机更新失败，未找到记录: id={}", drone.getId());
            return null;
        }
    }

    /**
     * 删除无人机
     * 使用@Transactional注解标记事务，确保数据一致性
     * 
     * @param id 无人机ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        // 记录调试日志，包含ID
        logger.debug("删除无人机: id={}", id);
        
        // 执行删除操作，获取受影响的行数
        int rows = droneMapper.deleteById(id);
        
        // 根据受影响行数判断是否删除成功
        if (rows > 0) {
            // 删除成功，记录日志
            logger.info("无人机删除成功: id={}", id);
        } else {
            // 删除失败（未找到对应记录），记录警告日志
            logger.warn("无人机删除失败，未找到记录: id={}", id);
        }
    }

    /**
     * 获取无人机总数
     * 
     * @return 无人机数量
     */
    @Override
    public int count() {
        // 调用Mapper层方法统计总数
        return droneMapper.count();
    }
}