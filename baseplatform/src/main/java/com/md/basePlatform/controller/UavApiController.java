package com.md.basePlatform.controller;

import com.md.basePlatform.domain.PageResult;
import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.domain.UavForm;
import com.md.basePlatform.service.IUavService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 无人机管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/uav")
@Tag(name = "无人机管理", description = "无人机信息的增删改查接口")
public class UavApiController {

    private final IUavService uavService;

    public UavApiController(IUavService uavService) {
        this.uavService = uavService;
    }

    /**
     * 分页查询无人机列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询", description = "根据条件分页查询无人机列表")
    public ResponseEntity<PageResult<Uav>> list(
            @Parameter(description = "页码（从1开始）") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        PageResult<Uav> result = uavService.page(keyword, null, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 根据ID查询无人机详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询详情", description = "根据ID查询无人机详细信息")
    public ResponseEntity<Uav> detail(@Parameter(description = "无人机ID") @PathVariable Long id) {
        Uav uav = uavService.getById(id);
        return ResponseEntity.ok(uav);
    }

    /**
     * 新增无人机
     */
    @PostMapping
    @Operation(summary = "新增无人机", description = "创建新的无人机记录")
    public ResponseEntity<Uav> create(@Valid @RequestBody UavForm form) {
        Uav uav = uavService.create(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(uav);
    }

    /**
     * 更新无人机信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新无人机", description = "更新指定ID的无人机信息")
    public ResponseEntity<Uav> update(
            @Parameter(description = "无人机ID") @PathVariable Long id,
            @Valid @RequestBody UavForm form) {
        form.setId(id);
        Uav uav = uavService.update(form);
        return ResponseEntity.ok(uav);
    }

    /**
     * 删除无人机
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除无人机", description = "删除指定ID的无人机记录")
    public ResponseEntity<Void> remove(@Parameter(description = "无人机ID") @PathVariable Long id) {
        uavService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
