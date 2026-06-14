package com.md.basePlatform.exception;

/**
 * 无人机记录不存在时抛出。
 */
public class UavNotFoundException extends RuntimeException {

    /**
     * @param message 描述信息
     */
    public UavNotFoundException(String message) {
        super(message);
    }
}
