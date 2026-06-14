package com.md.basePlatform.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 为所有页面注入当前登录用户名（若有）。
 */
@Profile("!test")
@ControllerAdvice
public class LoginUserModelAdvice {

    /**
     * @return 登录名；未登录时为 {@code null}
     */
    @ModelAttribute("loginName")
    public String loginName() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            Object p = subject.getPrincipal();
            return p != null ? p.toString() : null;
        }
        return null;
    }
}
