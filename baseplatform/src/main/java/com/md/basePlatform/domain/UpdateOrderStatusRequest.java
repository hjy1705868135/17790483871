package com.md.basePlatform.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新订单状态请求DTO
 */
public class UpdateOrderStatusRequest {

    /**
     * 订单状态
     */
    @NotBlank(message = "订单状态不能为空")
    private String status;

    /**
     * 备注说明
     */
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(String status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String status;
        private String remark;

        public Builder status(String status) { this.status = status; return this; }
        public Builder remark(String remark) { this.remark = remark; return this; }

        public UpdateOrderStatusRequest build() {
            return new UpdateOrderStatusRequest(status, remark);
        }
    }
}