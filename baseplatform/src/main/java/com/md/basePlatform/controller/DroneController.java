/**
 * 无人机控制器（Controller层）
 * 处理无人机相关的REST API请求
 * 作为前后端交互的桥梁，接收HTTP请求并返回响应
 */
package com.md.basePlatform.controller;

// 导入无人机实体类
import com.md.basePlatform.entity.Drone;
// 导入无人机服务接口
import com.md.basePlatform.service.DroneService;
// 导入SLF4J日志框架
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// 导入Spring框架相关注解和类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// 导入Java标准库
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 无人机控制器
 * 提供无人机CRUD操作的REST API接口
 * 使用@RestController注解标记，自动将返回值转换为JSON格式
 * 使用@RequestMapping注解指定基础路径为/api/drones
 */
@RestController
@RequestMapping("/api/drones")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class DroneController {

    /**
     * 日志记录器实例
     * 使用LoggerFactory根据类名创建Logger对象
     * 用于记录请求日志和操作日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DroneController.class);

    /**
     * 无人机服务接口
     * 使用@Autowired注解自动注入Service实例
     * Controller层通过Service层访问业务逻辑
     */
    @Autowired
    private DroneService droneService;

    /**
     * 获取所有无人机列表
     * HTTP方法：GET
     * 请求路径：/api/drones
     * 
     * @return 返回包含所有无人机的ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<Drone>> getAllDrones() {
        // 记录请求日志
        logger.info("GET /api/drones - 获取所有无人机列表");
        
        // 调用Service层方法获取所有无人机
        List<Drone> drones = droneService.getAllDrones();
        
        // 返回200 OK状态码和无人机列表
        return ResponseEntity.ok(drones);
    }

    /**
     * 根据ID获取单个无人机
     * HTTP方法：GET
     * 请求路径：/api/drones/{id}
     * 
     * @param id 无人机ID（路径参数）
     * @return 返回无人机对象或404错误信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDroneById(@PathVariable Long id) {
        // 记录请求日志，包含ID参数
        logger.info("GET /api/drones/{} - 获取无人机详情", id);
        
        // 调用Service层方法根据ID查询
        Drone drone = droneService.getDroneById(id);
        
        // 判断是否找到无人机
        if (drone == null) {
            // 未找到，返回404 Not Found状态码和错误消息
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("无人机不存在");
        }
        
        // 找到，返回200 OK状态码和无人机对象
        return ResponseEntity.ok(drone);
    }

    /**
     * 创建新无人机
     * HTTP方法：POST
     * 请求路径：/api/drones
     * 
     * @param drone         无人机对象（请求体）
     * @param bindingResult 验证结果对象（用于获取参数校验错误）
     * @return 返回创建成功的无人机或400错误信息
     */
    @PostMapping
    public ResponseEntity<?> createDrone(@Valid @RequestBody Drone drone, BindingResult bindingResult) {
        // 记录请求日志，包含无人机名称
        logger.info("POST /api/drones - 创建无人机: {}", drone.getName());
        
        // 检查参数验证结果
        if (bindingResult.hasErrors()) {
            // 验证失败，构建错误信息Map
            Map<String, String> errors = new HashMap<>();
            
            // 遍历所有字段错误，将字段名和错误消息存入Map
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            
            // 返回400 Bad Request状态码和错误信息
            return ResponseEntity.badRequest().body(errors);
        }
        
        // 验证通过，调用Service层保存无人机
        Drone createdDrone = droneService.save(drone);
        
        // 返回201 Created状态码和创建成功的无人机对象
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDrone);
    }

    /**
     * 更新无人机信息
     * HTTP方法：PUT
     * 请求路径：/api/drones/{id}
     * 
     * @param id            无人机ID（路径参数）
     * @param drone         无人机对象（请求体）
     * @param bindingResult 验证结果对象
     * @return 返回更新后的无人机或错误信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDrone(@PathVariable Long id, @Valid @RequestBody Drone drone, BindingResult bindingResult) {
        // 记录请求日志，包含ID参数
        logger.info("PUT /api/drones/{} - 更新无人机", id);
        
        // 检查参数验证结果
        if (bindingResult.hasErrors()) {
            // 验证失败，构建错误信息Map
            Map<String, String> errors = new HashMap<>();
            
            // 遍历所有字段错误
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            
            // 返回400 Bad Request状态码和错误信息
            return ResponseEntity.badRequest().body(errors);
        }
        
        // 设置无人机ID（确保更新的是指定ID的记录）
        drone.setId(id);
        
        // 调用Service层更新无人机
        Drone updatedDrone = droneService.update(drone);
        
        // 判断是否更新成功
        if (updatedDrone == null) {
            // 未找到对应记录，返回404 Not Found状态码
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("无人机不存在");
        }
        
        // 更新成功，返回200 OK状态码和更新后的无人机对象
        return ResponseEntity.ok(updatedDrone);
    }

    /**
     * 删除无人机
     * HTTP方法：DELETE
     * 请求路径：/api/drones/{id}
     * 
     * @param id 无人机ID（路径参数）
     * @return 返回空响应或错误信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDrone(@PathVariable Long id) {
        // 记录请求日志，包含ID参数
        logger.info("DELETE /api/drones/{} - 删除无人机", id);
        
        // 先检查无人机是否存在
        Drone drone = droneService.getDroneById(id);
        
        if (drone == null) {
            // 未找到，返回404 Not Found状态码
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("无人机不存在");
        }
        
        // 调用Service层删除无人机
        droneService.delete(id);
        
        // 返回204 No Content状态码（无内容响应）
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据名称搜索无人机
     * HTTP方法：GET
     * 请求路径：/api/drones/search/name?name=关键词
     * 
     * @param name 名称关键词（查询参数）
     * @return 返回匹配的无人机列表
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Drone>> searchByName(@RequestParam String name) {
        // 记录请求日志，包含名称参数
        logger.info("GET /api/drones/search/name?name={} - 按名称搜索", name);
        
        // 调用Service层方法按名称搜索
        List<Drone> drones = droneService.searchByName(name);
        
        // 返回200 OK状态码和搜索结果
        return ResponseEntity.ok(drones);
    }

    /**
     * 根据型号搜索无人机
     * HTTP方法：GET
     * 请求路径：/api/drones/search/model?model=关键词
     * 
     * @param model 型号关键词（查询参数）
     * @return 返回匹配的无人机列表
     */
    @GetMapping("/search/model")
    public ResponseEntity<List<Drone>> searchByModel(@RequestParam String model) {
        // 记录请求日志，包含型号参数
        logger.info("GET /api/drones/search/model?model={} - 按型号搜索", model);
        
        // 调用Service层方法按型号搜索
        List<Drone> drones = droneService.searchByModel(model);
        
        // 返回200 OK状态码和搜索结果
        return ResponseEntity.ok(drones);
    }
}