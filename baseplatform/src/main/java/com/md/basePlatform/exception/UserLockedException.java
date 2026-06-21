package com.md.basePlatform.exception;

/**
 * 用户已锁定异常
 */
public class UserLockedException extends RuntimeException {

    public UserLockedException(String message) {
        super(message);
    }
}