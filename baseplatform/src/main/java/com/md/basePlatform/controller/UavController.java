package com.md.basePlatform.controller;

import com.md.basePlatform.domain.PageResult;
import com.md.basePlatform.domain.Uav;
import com.md.basePlatform.domain.UavForm;
import com.md.basePlatform.service.IUavService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 无人机管理（Thymeleaf 页面）。
 */
@Controller
@RequestMapping("/uav")
@Tag(name = "无人机管理", description = "无人机信息的增删改查页面")
public class UavController {

    private static final int DEFAULT_SIZE = 10;

    private final IUavService uavService;

    /**
     * @param uavService 业务服务
     */
    public UavController(IUavService uavService) {
        this.uavService = uavService;
    }

    /**
     * 分页列表与查询。
     *
     * @param keyword 关键字
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param model 模型
     * @return 列表页
     */
    @GetMapping
    @Operation(summary = "无人机列表")
    public String list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "" + DEFAULT_SIZE) int pageSize,
            Model model) {
        PageResult<Uav> page = uavService.page(keyword, status, pageNum, pageSize);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("status", status == null ? "" : status);
        return "uav/list";
    }

    /**
     * 新增页。
     *
     * @param model 模型
     * @return 表单页
     */
    @GetMapping("/add")
    @Operation(summary = "新增无人机页面")
    public String addForm(Model model) {
        if (!model.containsAttribute("uavForm")) {
            UavForm form = new UavForm();
            form.setStatus("ACTIVE");
            model.addAttribute("uavForm", form);
        }
        model.addAttribute("formTitle", "新增无人机");
        model.addAttribute("editMode", false);
        return "uav/form";
    }

    /**
     * 提交新增。
     *
     * @param form 表单
     * @param bindingResult 校验结果
     * @param redirect 重定向属性
     * @param model 模型（校验失败回显）
     * @return 重定向或表单页
     */
    @PostMapping
    @Operation(summary = "提交新增无人机")
    public String create(
            @Valid @ModelAttribute("uavForm") UavForm form,
            BindingResult bindingResult,
            RedirectAttributes redirect,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "新增无人机");
            model.addAttribute("editMode", false);
            return "uav/form";
        }
        Uav created = uavService.create(form);
        redirect.addFlashAttribute("successMessage", "创建成功，编号 " + created.getCode());
        return "redirect:/uav";
    }

    /**
     * 详情。
     *
     * @param id 主键
     * @param model 模型
     * @return 详情页
     */
    @GetMapping("/detail/{id}")
    @Operation(summary = "无人机详情")
    public String detail(@PathVariable("id") long id, Model model) {
        model.addAttribute("uav", uavService.getById(id));
        return "uav/detail";
    }

    /**
     * 编辑页。
     *
     * @param id 主键
     * @param model 模型
     * @return 表单页
     */
    @GetMapping("/edit/{id}")
    @Operation(summary = "编辑无人机页面")
    public String editForm(@PathVariable("id") long id, Model model) {
        Uav uav = uavService.getById(id);
        UavForm form = toForm(uav);
        model.addAttribute("uavForm", form);
        model.addAttribute("formTitle", "编辑无人机");
        model.addAttribute("editMode", true);
        return "uav/form";
    }

    /**
     * 提交更新。
     *
     * @param form 表单
     * @param bindingResult 校验结果
     * @param redirect 重定向属性
     * @param model 模型
     * @return 重定向或表单页
     */
    @PostMapping("/update")
    @Operation(summary = "提交更新无人机")
    public String update(
            @Valid @ModelAttribute("uavForm") UavForm form,
            BindingResult bindingResult,
            RedirectAttributes redirect,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "编辑无人机");
            model.addAttribute("editMode", true);
            return "uav/form";
        }
        uavService.update(form);
        redirect.addFlashAttribute("successMessage", "保存成功");
        return "redirect:/uav";
    }

    /**
     * 删除。
     *
     * @param id 主键
     * @param redirect 重定向属性
     * @return 重定向列表
     */
    @PostMapping("/remove/{id}")
    @Operation(summary = "删除无人机")
    public String remove(@PathVariable("id") long id, RedirectAttributes redirect) {
        uavService.delete(id);
        redirect.addFlashAttribute("successMessage", "已删除");
        return "redirect:/uav";
    }

    private static UavForm toForm(Uav uav) {
        UavForm f = new UavForm();
        f.setId(uav.getId());
        f.setCode(uav.getCode());
        f.setModel(uav.getModel());
        f.setManufacturer(uav.getManufacturer());
        f.setMaxFlightTimeMinutes(uav.getMaxFlightTimeMinutes());
        f.setMaxRangeKm(uav.getMaxRangeKm());
        f.setPayloadKg(uav.getPayloadKg());
        f.setMaxAltitudeMeters(uav.getMaxAltitudeMeters());
        f.setMaxSpeedKmh(uav.getMaxSpeedKmh());
        f.setBatteryCapacityMah(uav.getBatteryCapacityMah());
        f.setWeightKg(uav.getWeightKg());
        f.setCameraResolution(uav.getCameraResolution());
        f.setHasGps(uav.getHasGps());
        f.setHasObstacleAvoidance(uav.getHasObstacleAvoidance());
        f.setPurchaseDate(uav.getPurchaseDate() != null ? uav.getPurchaseDate().toString() : null);
        f.setWarrantyExpireDate(uav.getWarrantyExpireDate() != null ? uav.getWarrantyExpireDate().toString() : null);
        f.setSerialNumber(uav.getSerialNumber());
        f.setDepartment(uav.getDepartment());
        f.setApplicationField(uav.getApplicationField());
        f.setStatus(uav.getStatus());
        f.setRemark(uav.getRemark());
        return f;
    }
}
