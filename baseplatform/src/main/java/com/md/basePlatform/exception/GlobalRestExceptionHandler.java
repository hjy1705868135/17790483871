package com.md.basePlatform.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局 REST API 异常处理（JSON 响应模式）。
 */
@RestControllerAdvice(basePackages = "com.md.basePlatform.controller")
public class GlobalRestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    /**
     * 资源不存在。
     *
     * @param ex 异常
     * @return JSON 响应
     */
    @ExceptionHandler(UavNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(UavNotFoundException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    /**
     * 编号重复等业务冲突。
     *
     * @param ex 异常
     * @return JSON 响应
     */
    @ExceptionHandler(DuplicateCodeException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateCodeException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse("DUPLICATE_CODE", ex.getMessage()));
    }

    /**
     * 表单校验失败。
     *
     * @param ex 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("参数校验失败");
        log.warn(msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse("VALIDATION_ERROR", msg));
    }

    /**
     * 非法参数。
     *
     * @param ex 异常
     * @return JSON 响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    /**
     * 其它未预期异常。
     *
     * @param ex 异常
     * @return JSON 响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception ex) {
        log.error("Unhandled error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse("INTERNAL_ERROR", "服务暂时不可用，请稍后重试。"));
    }

    private Map<String, Object> buildErrorResponse(String code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}