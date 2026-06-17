/**
 * 无人机服务实现类
 * 实现DroneService接口定义的业务逻辑方法
 * 作为Service层的实现，负责调用Repository层进行数据访问
 */
package com.md.basePlatform.service.impl;

import com.md.basePlatform.domain.Drone;
import com.md.basePlatform.repository.DroneMapper;
import com.md.basePlatform.service.DroneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 无人机服务实现类
 * 提供无人机业务逻辑处理的具体实现
 * 使用@Service注解标记，Spring会自动扫描并注册为Bean
 */
@Service
public class DroneServiceImpl implements DroneService {

    private static final Logger logger = LoggerFactory.getLogger(DroneServiceImpl.class);

    @Autowired
    private DroneMapper droneMapper;

    @Override
    public List<Drone> getAllDrones() {
        logger.debug("获取所有无人机列表");
        return droneMapper.findAll();
    }

    @Override
    public Drone getDroneById(Long id) {
        logger.debug("根据ID获取无人机: id={}", id);
        return droneMapper.findById(id);
    }

    @Override
    public List<Drone> searchByName(String name) {
        logger.debug("根据名称搜索无人机: name={}", name);
        if (name == null || name.trim().isEmpty()) {
            return getAllDrones();
        }
        return droneMapper.findByName("%" + name + "%");
    }

    @Override
    public List<Drone> searchByModel(String model) {
        logger.debug("根据型号搜索无人机: model={}", model);
        if (model == null || model.trim().isEmpty()) {
            return getAllDrones();
        }
        return droneMapper.findByModel("%" + model + "%");
    }

    @Override
    @Transactional
    public Drone save(Drone drone) {
        logger.debug("保存无人机: {}", drone.getName());
        if (drone.getStatus() == null || drone.getStatus().isEmpty()) {
            drone.setStatus("ACTIVE");
        }
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
        String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        drone.setCreateTime(now);
        drone.setUpdateTime(now);
        droneMapper.insert(drone);
        logger.info("无人机保存成功: id={}, name={}", drone.getId(), drone.getName());
        return drone;
    }

    @Override
    @Transactional
    public Drone update(Drone drone) {
        logger.debug("更新无人机: id={}, name={}", drone.getId(), drone.getName());
        String now = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        drone.setUpdateTime(now);
        int rows = droneMapper.update(drone);
        if (rows > 0) {
            logger.info("无人机更新成功: id={}", drone.getId());
            return droneMapper.findById(drone.getId());
        } else {
            logger.warn("无人机更新失败，未找到记录: id={}", drone.getId());
            return null;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        logger.debug("删除无人机: id={}", id);
        int rows = droneMapper.deleteById(id);
        if (rows > 0) {
            logger.info("无人机删除成功: id={}", id);
        } else {
            logger.warn("无人机删除失败，未找到记录: id={}", id);
        }
    }

    @Override
    public int count() {
        return droneMapper.count();
    }
}
