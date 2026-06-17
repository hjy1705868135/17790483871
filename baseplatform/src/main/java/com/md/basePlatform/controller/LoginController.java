package com.md.basePlatform.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 登录与注销。
 */
@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    /**
     * 登录页。
     *
     * @return 视图名
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 根路径重定向到无人机列表页。
     *
     * @return 重定向
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/uav";
    }

    /**
     * 提交登录。
     *
     * @param username 用户名
     * @param password 密码
     * @param model 模型
     * @return 重定向或登录页
     */
    @PostMapping("/login")
    public String login(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(new UsernamePasswordToken(username, password));
            return "redirect:/uav";
        } catch (AuthenticationException ex) {
            log.debug("Login failed: {}", ex.getMessage());
            model.addAttribute("errorMessage", "用户名或密码错误");
            return "login";
        }
    }

    /**
     * 注销。
     *
     * @return 重定向登录页
     */
    @GetMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/login";
    }
}
