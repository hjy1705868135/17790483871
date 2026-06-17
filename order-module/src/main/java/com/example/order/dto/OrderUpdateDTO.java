package com.example.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单更新DTO
 *
 * @author API Team
 * @version 1.0
 */
@Data
@Schema(description = "订单更新请求")
public class OrderUpdateDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50位")
    @Schema(description = "收货人姓名")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "收货人电话")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 255, message = "收货地址长度不能超过255位")
    @Schema(description = "收货地址")
    private String receiverAddress;

    @Size(max = 10, message = "邮政编码长度不能超过10位")
    @Schema(description = "邮政编码")
    private String receiverPostalCode;

    @Size(max = 500, message = "订单备注长度不能超过500位")
    @Schema(description = "订单备注")
    private String remark;
}
