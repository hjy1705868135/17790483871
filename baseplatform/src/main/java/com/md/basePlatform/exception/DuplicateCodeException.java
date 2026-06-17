package com.md.basePlatform.exception;

/**
 * 无人机编号重复时抛出。
 */
public class DuplicateCodeException extends RuntimeException {

    /**
     * @param message 描述信息
     */
    public DuplicateCodeException(String message) {
        super(message);
    }
}
