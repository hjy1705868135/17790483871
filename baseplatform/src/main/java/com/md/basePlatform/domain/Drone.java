/**
 * 无人机实体类
 * 映射数据库 drone 表，用于存储无人机的所有属性信息
 * 采用标准的 JavaBean 设计模式
 */
package com.md.basePlatform.domain;

// 导入Hibernate Validation验证框架的注解
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

/**
 * 无人机实体类（Domain层）
 * 对应数据库中的 drone 表
 * 使用 JSR-303/JSR-380 验证注解进行参数校验
 */
public class Drone {

    /** 无人机ID，主键，自增 */
    private Long id;

    /** 无人机名称，必填，最大长度100字符 */
    @NotBlank(message = "名称不能为空")
    private String name;

    /** 无人机型号，必填，最大长度100字符 */
    @NotBlank(message = "型号不能为空")
    private String model;

    /** 制造商名称，可选，最大长度100字符 */
    private String manufacturer;

    /** 重量（kg），最小值0 */
    @DecimalMin(value = "0", message = "重量必须大于等于0")
    private Double weight;

    /** 最大飞行高度（米），最小值0 */
    @Min(value = 0, message = "最大高度必须大于等于0")
    private Integer maxAltitude;

    /** 最大续航时间（分钟），最小值0 */
    @Min(value = 0, message = "续航时间必须大于等于0")
    private Integer maxFlightTime;

    /** 最大速度（km/h），最小值0 */
    @DecimalMin(value = "0", message = "最大速度必须大于等于0")
    private Double maxSpeed;

    /** 状态：ACTIVE（正常运行）、INACTIVE（已停用） */
    private String status;

    /** 描述信息，可选 */
    private String description;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private String updateTime;

    // ==================== Getter 和 Setter 方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(Integer maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public Integer getMaxFlightTime() {
        return maxFlightTime;
    }

    public void setMaxFlightTime(Integer maxFlightTime) {
        this.maxFlightTime = maxFlightTime;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", weight=" + weight +
                ", maxAltitude=" + maxAltitude +
                ", maxFlightTime=" + maxFlightTime +
                ", maxSpeed=" + maxSpeed +
                ", status='" + status + '\'' +
                '}';
    }
}
