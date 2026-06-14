/**
 * 无人机实体类
 * 映射数据库 drone 表，用于存储无人机的所有属性信息
 * 采用标准的 JavaBean 设计模式，包含私有字段、getter/setter 方法和 toString 方法
 */
package com.md.basePlatform.entity;

// 导入Hibernate Validation验证框架的注解
import javax.validation.constraints.NotBlank;  // 用于非空验证
import javax.validation.constraints.DecimalMin;  // 用于最小值验证（小数）
import javax.validation.constraints.Min;  // 用于最小值验证（整数）

/**
 * 无人机实体类
 * 对应数据库中的 drone 表，每个字段对应表中的一列
 * 使用 JSR-303/JSR-380 验证注解进行参数校验
 */
public class Drone {

    /**
     * 无人机ID，主键，自增
     * 数据库中对应 id 列，类型为 INTEGER
     */
    private Long id;

    /**
     * 无人机名称，必填字段，最大长度100字符
     * 使用 @NotBlank 注解确保非空，message 指定验证失败时的错误提示信息
     * 数据库中对应 name 列，类型为 VARCHAR(100)
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 无人机型号，必填字段，最大长度100字符
     * 使用 @NotBlank 注解确保非空
     * 数据库中对应 model 列，类型为 VARCHAR(100)
     */
    @NotBlank(message = "型号不能为空")
    private String model;

    /**
     * 制造商名称，可选字段，最大长度100字符
     * 数据库中对应 manufacturer 列，类型为 VARCHAR(100)
     */
    private String manufacturer;

    /**
     * 重量（单位：千克），最小值为0
     * 使用 @DecimalMin 注解确保数值大于等于0
     * 数据库中对应 weight 列，类型为 DECIMAL(10,2)
     */
    @DecimalMin(value = "0", message = "重量必须大于等于0")
    private Double weight;

    /**
     * 最大飞行高度（单位：米），最小值为0
     * 使用 @Min 注解确保整数值大于等于0
     * 数据库中对应 max_altitude 列，类型为 INTEGER
     */
    @Min(value = 0, message = "最大高度必须大于等于0")
    private Integer maxAltitude;

    /**
     * 最大续航时间（单位：分钟），最小值为0
     * 使用 @Min 注解确保整数值大于等于0
     * 数据库中对应 max_flight_time 列，类型为 INTEGER
     */
    @Min(value = 0, message = "续航时间必须大于等于0")
    private Integer maxFlightTime;

    /**
     * 最大速度（单位：公里/小时），最小值为0
     * 使用 @DecimalMin 注解确保数值大于等于0
     * 数据库中对应 max_speed 列，类型为 DECIMAL(10,1)
     */
    @DecimalMin(value = "0", message = "最大速度必须大于等于0")
    private Double maxSpeed;

    /**
     * 状态：ACTIVE（正常运行）、INACTIVE（已停用）
     * 数据库中对应 status 列，类型为 VARCHAR(20)，默认值为 'ACTIVE'
     */
    private String status;

    /**
     * 描述信息，可选字段
     * 数据库中对应 description 列，类型为 TEXT，可存储较长文本
     */
    private String description;

    /**
     * 创建时间，记录数据插入时间
     * 数据库中对应 create_time 列，类型为 TIMESTAMP
     */
    private String createTime;

    /**
     * 更新时间，记录数据最后修改时间
     * 数据库中对应 update_time 列，类型为 TIMESTAMP
     */
    private String updateTime;

    // ==================== Getter 和 Setter 方法 ====================
    // Getter方法用于获取属性值，Setter方法用于设置属性值
    // 遵循 JavaBean 命名规范：getXxx() 和 setXxx()

    /**
     * 获取无人机ID
     * @return 无人机ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置无人机ID
     * @param id 无人机ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取无人机名称
     * @return 无人机名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置无人机名称
     * @param name 无人机名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取无人机型号
     * @return 无人机型号
     */
    public String getModel() {
        return model;
    }

    /**
     * 设置无人机型号
     * @param model 无人机型号
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 获取制造商名称
     * @return 制造商名称
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * 设置制造商名称
     * @param manufacturer 制造商名称
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * 获取重量（kg）
     * @return 重量值
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * 设置重量（kg）
     * @param weight 重量值
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * 获取最大飞行高度（米）
     * @return 最大飞行高度
     */
    public Integer getMaxAltitude() {
        return maxAltitude;
    }

    /**
     * 设置最大飞行高度（米）
     * @param maxAltitude 最大飞行高度
     */
    public void setMaxAltitude(Integer maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    /**
     * 获取最大续航时间（分钟）
     * @return 续航时间
     */
    public Integer getMaxFlightTime() {
        return maxFlightTime;
    }

    /**
     * 设置最大续航时间（分钟）
     * @param maxFlightTime 续航时间
     */
    public void setMaxFlightTime(Integer maxFlightTime) {
        this.maxFlightTime = maxFlightTime;
    }

    /**
     * 获取最大速度（km/h）
     * @return 最大速度
     */
    public Double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * 设置最大速度（km/h）
     * @param maxSpeed 最大速度
     */
    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * 获取状态
     * @return 状态值（ACTIVE/INACTIVE）
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     * @param status 状态值（ACTIVE/INACTIVE）
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取描述信息
     * @return 描述信息
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述信息
     * @param description 描述信息
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取创建时间
     * @return 创建时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     * @param createTime 创建时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     * @return 更新时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     * @param updateTime 更新时间字符串（格式：yyyy-MM-dd HH:mm:ss）
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 重写 toString 方法，便于日志输出和调试
     * 返回对象的字符串表示，包含所有属性值
     * @return 无人机对象的字符串表示
     */
    @Override
    public String toString() {
        return "Drone{" +
                "id=" + id +                                    // 拼接ID
                ", name='" + name + '\'' +                      // 拼接名称
                ", model='" + model + '\'' +                    // 拼接型号
                ", manufacturer='" + manufacturer + '\'' +      // 拼接制造商
                ", weight=" + weight +                          // 拼接重量
                ", maxAltitude=" + maxAltitude +                // 拼接最大高度
                ", maxFlightTime=" + maxFlightTime +            // 拼接续航时间
                ", maxSpeed=" + maxSpeed +                      // 拼接最大速度
                ", status='" + status + '\'' +                  // 拼接状态
                '}';
    }
}