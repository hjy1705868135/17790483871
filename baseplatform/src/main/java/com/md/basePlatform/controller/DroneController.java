/**
 * 无人机控制器（Controller层）
 * 处理无人机相关的REST API请求
 * 作为前后端交互的桥梁，接收HTTP请求并返回响应
 */
package com.md.basePlatform.controller;

import com.md.basePlatform.domain.Drone;
import com.md.basePlatform.service.DroneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 无人机控制器
 * 提供无人机CRUD操作的REST API接口
 * 使用@RestController注解标记，自动将返回值转换为JSON格式
 */
@RestController
@RequestMapping("/api/drones")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class DroneController {

    private static final Logger logger = LoggerFactory.getLogger(DroneController.class);

    @Autowired
    private DroneService droneService;

    /**
     * 获取所有无人机列表
     */
    @GetMapping
    public ResponseEntity<List<Drone>> getAllDrones() {
        logger.info("GET /api/drones - 获取所有无人机列表");
        List<Drone> drones = droneService.getAllDrones();
        return ResponseEntity.ok(drones);
    }

    /**
     * 根据ID获取单个无人机
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDroneById(@PathVariable Long id) {
        logger.info("GET /api/drones/{} - 获取无人机详情", id);
        Drone drone = droneService.getDroneById(id);
        if (drone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("无人机不存在");
        }
        return ResponseEntity.ok(drone);
    }

    /**
     * 创建新无人机
     */
    @PostMapping
    public ResponseEntity<?> createDrone(@Valid @RequestBody Drone drone, BindingResult bindingResult) {
        logger.info("POST /api/drones - 创建无人机: {}", drone.getName());
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        Drone createdDrone = droneService.save(drone);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDrone);
    }

    /**
     * 更新无人机信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDrone(@PathVariable Long id, @Valid @RequestBody Drone drone, BindingResult bindingResult) {
        logger.info("PUT /api/drones/{} - 更新无人机", id);
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        drone.setId(id);
        Drone updatedDrone = droneService.update(drone);
        if (updatedDrone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("无人机不存在");
        }
        return ResponseEntity.ok(updatedDrone);
    }

    /**
     * 删除无人机
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDrone(@PathVariable Long id) {
        logger.info("DELETE /api/drones/{} - 删除无人机", id);
        Drone drone = droneService.getDroneById(id);
        if (drone == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("无人机不存在");
        }
        droneService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据名称搜索无人机
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Drone>> searchByName(@RequestParam String name) {
        logger.info("GET /api/drones/search/name?name={} - 按名称搜索", name);
        List<Drone> drones = droneService.searchByName(name);
        return ResponseEntity.ok(drones);
    }

    /**
     * 根据型号搜索无人机
     */
    @GetMapping("/search/model")
    public ResponseEntity<List<Drone>> searchByModel(@RequestParam String model) {
        logger.info("GET /api/drones/search/model?model={} - 按型号搜索", model);
        List<Drone> drones = droneService.searchByModel(model);
        return ResponseEntity.ok(drones);
    }
}
