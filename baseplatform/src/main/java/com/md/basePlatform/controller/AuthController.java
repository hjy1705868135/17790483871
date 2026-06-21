package com.md.basePlatform.controller;

import com.md.basePlatform.common.ApiResponse;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for user: {}", loginRequest.getUsername());
        TokenResponse tokenResponse = authService.login(loginRequest);
        return ApiResponse.success("登录成功", tokenResponse);
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request");
        TokenResponse tokenResponse = authService.refreshToken(request);
        return ApiResponse.success("令牌刷新成功", tokenResponse);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest request) {
        String refreshToken = request.getHeader("X-Refresh-Token");
        authService.logout(refreshToken);
        return ApiResponse.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserInfoResponse userInfo = authService.getCurrentUserInfo(userId);
        return ApiResponse.success(userInfo);
    }
}