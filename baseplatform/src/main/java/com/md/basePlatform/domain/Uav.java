package com.md.basePlatform.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 无人机实体，对应表 {@code uav}。
 */
public class Uav {

    private Long id;
    private String code;
    private String model;
    private String manufacturer;
    private Integer maxFlightTimeMinutes;
    private Integer maxRangeKm;
    private Integer payloadKg;
    private Integer maxAltitudeMeters;
    private Integer maxSpeedKmh;
    private Integer batteryCapacityMah;
    private Double weightKg;
    private String cameraResolution;
    private Boolean hasGps;
    private Boolean hasObstacleAvoidance;
    private LocalDate purchaseDate;
    private LocalDate warrantyExpireDate;
    private String serialNumber;
    private String department;
    private String applicationField;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 无人机编号（唯一）
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 无人机编号
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return 型号
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model 型号
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return 厂商
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer 厂商
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * @return 最大续航（分钟）
     */
    public Integer getMaxFlightTimeMinutes() {
        return maxFlightTimeMinutes;
    }

    /**
     * @param maxFlightTimeMinutes 最大续航（分钟）
     */
    public void setMaxFlightTimeMinutes(Integer maxFlightTimeMinutes) {
        this.maxFlightTimeMinutes = maxFlightTimeMinutes;
    }

    /**
     * @return 最大航程（km）
     */
    public Integer getMaxRangeKm() {
        return maxRangeKm;
    }

    /**
     * @param maxRangeKm 最大航程（km）
     */
    public void setMaxRangeKm(Integer maxRangeKm) {
        this.maxRangeKm = maxRangeKm;
    }

    /**
     * @return 载重（kg）
     */
    public Integer getPayloadKg() {
        return payloadKg;
    }

    /**
     * @param payloadKg 载重（kg）
     */
    public void setPayloadKg(Integer payloadKg) {
        this.payloadKg = payloadKg;
    }

    /**
     * @return 最大飞行高度（米）
     */
    public Integer getMaxAltitudeMeters() {
        return maxAltitudeMeters;
    }

    /**
     * @param maxAltitudeMeters 最大飞行高度（米）
     */
    public void setMaxAltitudeMeters(Integer maxAltitudeMeters) {
        this.maxAltitudeMeters = maxAltitudeMeters;
    }

    /**
     * @return 最大飞行速度（km/h）
     */
    public Integer getMaxSpeedKmh() {
        return maxSpeedKmh;
    }

    /**
     * @param maxSpeedKmh 最大飞行速度（km/h）
     */
    public void setMaxSpeedKmh(Integer maxSpeedKmh) {
        this.maxSpeedKmh = maxSpeedKmh;
    }

    /**
     * @return 电池容量（mAh）
     */
    public Integer getBatteryCapacityMah() {
        return batteryCapacityMah;
    }

    /**
     * @param batteryCapacityMah 电池容量（mAh）
     */
    public void setBatteryCapacityMah(Integer batteryCapacityMah) {
        this.batteryCapacityMah = batteryCapacityMah;
    }

    /**
     * @return 机身重量（kg）
     */
    public Double getWeightKg() {
        return weightKg;
    }

    /**
     * @param weightKg 机身重量（kg）
     */
    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    /**
     * @return 摄像头分辨率
     */
    public String getCameraResolution() {
        return cameraResolution;
    }

    /**
     * @param cameraResolution 摄像头分辨率
     */
    public void setCameraResolution(String cameraResolution) {
        this.cameraResolution = cameraResolution;
    }

    /**
     * @return 是否有GPS功能
     */
    public Boolean getHasGps() {
        return hasGps;
    }

    /**
     * @param hasGps 是否有GPS功能
     */
    public void setHasGps(Boolean hasGps) {
        this.hasGps = hasGps;
    }

    /**
     * @return 是否有避障功能
     */
    public Boolean getHasObstacleAvoidance() {
        return hasObstacleAvoidance;
    }

    /**
     * @param hasObstacleAvoidance 是否有避障功能
     */
    public void setHasObstacleAvoidance(Boolean hasObstacleAvoidance) {
        this.hasObstacleAvoidance = hasObstacleAvoidance;
    }

    /**
     * @return 购买日期
     */
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * @param purchaseDate 购买日期
     */
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * @return 保修到期日期
     */
    public LocalDate getWarrantyExpireDate() {
        return warrantyExpireDate;
    }

    /**
     * @param warrantyExpireDate 保修到期日期
     */
    public void setWarrantyExpireDate(LocalDate warrantyExpireDate) {
        this.warrantyExpireDate = warrantyExpireDate;
    }

    /**
     * @return 序列号
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @param serialNumber 序列号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * @return 所属部门
     */
    public String getDepartment() {
        return department;
    }

    /**
     * @param department 所属部门
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * @return 应用领域
     */
    public String getApplicationField() {
        return applicationField;
    }

    /**
     * @param applicationField 应用领域
     */
    public void setApplicationField(String applicationField) {
        this.applicationField = applicationField;
    }

    /**
     * @return 状态（如 ACTIVE、INACTIVE、MAINTENANCE）
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status 状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
