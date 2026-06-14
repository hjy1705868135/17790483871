package com.example.order.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 全局统一响应结果
 *
 * @author API Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 创建成功结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 创建成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(HttpStatus.OK.value(), "操作成功", data, LocalDateTime.now(), null);
    }

    /**
     * 创建成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(HttpStatus.OK.value(), message, data, LocalDateTime.now(), null);
    }

    /**
     * 创建失败结果
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null, LocalDateTime.now(), null);
    }

    /**
     * 创建失败结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null, LocalDateTime.now(), null);
    }

    /**
     * 创建业务异常结果
     */
    public static <T> Result<T> businessError(BusinessException e) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null, LocalDateTime.now(), null);
    }

    /**
     * 创建验证异常结果
     */
    public static <T> Result<T> validationError(String message) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), message, null, LocalDateTime.now(), null);
    }
}
