package com.md.basePlatform.controller;

import com.md.basePlatform.common.ApiResponse;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for user: {}", request.getUsername());
        TokenResponse tokenResponse = userService.register(request);
        return ApiResponse.success("注册成功", tokenResponse);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ApiResponse<UserInfoResponse> updateProfile(
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserInfoResponse userInfo = userService.updateUserInfo(userId, request);
        return ApiResponse.success("更新成功", userInfo);
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @RequestBody Map<String, String> passwordData,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        String confirmPassword = passwordData.get("confirmPassword");
        userService.changePassword(userId, oldPassword, newPassword, confirmPassword);
        return ApiResponse.success("密码修改成功", null);
    }

    /**
     * 获取用户收货地址列表
     */
    @GetMapping("/addresses")
    public ApiResponse<List<AddressResponse>> getAddresses(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<AddressResponse> addresses = userService.getUserAddresses(userId);
        return ApiResponse.success(addresses);
    }

    /**
     * 添加收货地址
     */
    @PostMapping("/addresses")
    public ApiResponse<AddressResponse> addAddress(
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        AddressResponse address = userService.addAddress(userId, request);
        return ApiResponse.success("添加成功", address);
    }

    /**
     * 更新收货地址
     */
    @PutMapping("/addresses/{id}")
    public ApiResponse<AddressResponse> updateAddress(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody AddressRequest request,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        AddressResponse address = userService.updateAddress(userId, id, request);
        return ApiResponse.success("更新成功", address);
    }

    /**
     * 删除收货地址
     */
    @DeleteMapping("/addresses/{id}")
    public ApiResponse<Void> deleteAddress(
            @PathVariable(value = "id") Long id,
            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        userService.deleteAddress(userId, id);
        return ApiResponse.success("删除成功", null);
    }

    /**
     * 获取默认收货地址
     */
    @GetMapping("/addresses/default")
    public ApiResponse<AddressResponse> getDefaultAddress(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        AddressResponse address = userService.getDefaultAddress(userId);
        return ApiResponse.success(address);
    }
}
