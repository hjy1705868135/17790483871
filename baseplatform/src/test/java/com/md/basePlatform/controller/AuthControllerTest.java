package com.md.basePlatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器测试
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private TokenResponse tokenResponse;
    private RefreshTokenRequest refreshTokenRequest;
    private UserInfoResponse userInfoResponse;

    @BeforeEach
    void setUp() {
        loginRequest = LoginRequest.builder()
                .username("test@example.com")
                .password("password123")
                .build();

        tokenResponse = TokenResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .expiresIn(1800L)
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken("refresh-token")
                .build();

        userInfoResponse = UserInfoResponse.builder()
                .id(1L)
                .username("test@example.com")
                .nickname("测试用户")
                .roles(Arrays.asList("USER"))
                .createdAt(LocalDateTime.now().toString())
                .build();
    }

    @Test
    void should_LoginSuccessfully_When_CredentialsAreValid() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(1800));
    }

    @Test
    void should_ReturnBadRequest_When_LoginRequestIsInvalid() throws Exception {
        // Given
        LoginRequest invalidRequest = LoginRequest.builder()
                .username("") // Invalid: empty username
                .password("123") // Invalid: password too short
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_RefreshTokenSuccessfully_When_TokenIsValid() throws Exception {
        // Given
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(tokenResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("令牌刷新成功"))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    void should_LogoutSuccessfully_When_RefreshTokenIsValid() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .header("X-Refresh-Token", "refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }

    @Test
    @WithMockUser(username = "1")
    void should_GetCurrentUserInfoSuccessfully_When_UserIsAuthenticated() throws Exception {
        // Given
        when(authService.getCurrentUserInfo(1L)).thenReturn(userInfoResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/auth/me")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("test@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"))
                .andExpect(jsonPath("$.data.roles").isArray());
    }

    @Test
    void should_ReturnUnauthorized_When_TryingToAccessProtectedEndpointWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/auth/me")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}