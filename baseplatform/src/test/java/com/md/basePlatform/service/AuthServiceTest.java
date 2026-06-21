package com.md.basePlatform.service;

import com.md.basePlatform.config.JwtTokenProvider;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.exception.AuthenticationException;
import com.md.basePlatform.exception.TokenInvalidException;
import com.md.basePlatform.exception.UserLockedException;
import com.md.basePlatform.repository.RefreshTokenRepository;
import com.md.basePlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private LoginRequest loginRequest;
    private static final Long USER_ID = 1L;
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String ACCESS_TOKEN = "access-token-mock";
    private static final String REFRESH_TOKEN = "refresh-token-mock";

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(USER_ID)
                .username(USERNAME)
                .password("encoded-password")
                .nickname("测试用户")
                .roles("USER")
                .status("ACTIVE")
                .failedAttempts(0)
                .lockedUntil(null)
                .deleted(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        loginRequest = LoginRequest.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    // ============ 登录测试 ============

    @Test
    void should_LoginSuccessfully_When_CredentialsAreValid() {
        // Given
        when(userRepository.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches(PASSWORD, mockUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(eq(USER_ID), eq(USERNAME), any())).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.generateRefreshToken(USER_ID)).thenReturn(REFRESH_TOKEN);
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(1800L);

        // When
        TokenResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(response.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(1800L);

        verify(userRepository).updateById(any(User.class));
        verify(refreshTokenRepository).insert(any(RefreshToken.class));
    }

    @Test
    void should_ThrowAuthenticationException_When_UserNotFound() {
        // Given
        when(userRepository.selectOne(any())).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    void should_ThrowAuthenticationException_When_PasswordIsWrong() {
        // Given
        when(userRepository.selectOne(any())).thenReturn(mockUser);
        when(passwordEncoder.matches(PASSWORD, mockUser.getPassword())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户名或密码错误");

        verify(userRepository).updateById(any(User.class));
    }

    @Test
    void should_ThrowUserLockedException_When_AccountIsLocked() {
        // Given
        User lockedUser = User.builder()
                .id(USER_ID).username(USERNAME).password("encoded-password")
                .roles("USER").status("LOCKED")
                .failedAttempts(5)
                .lockedUntil(LocalDateTime.now().plusMinutes(10))
                .deleted(0)
                .build();

        when(userRepository.selectOne(any())).thenReturn(lockedUser);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(UserLockedException.class)
                .hasMessageContaining("账户已锁定");
    }

    @Test
    void should_ThrowAuthenticationException_When_AccountIsDisabled() {
        // Given
        User disabledUser = User.builder()
                .id(USER_ID).username(USERNAME).password("encoded-password")
                .roles("USER").status("DISABLED")
                .failedAttempts(0).lockedUntil(null).deleted(0)
                .build();

        when(userRepository.selectOne(any())).thenReturn(disabledUser);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("账户已被禁用");
    }

    // ============ 刷新令牌测试 ============

    @Test
    void should_RefreshTokenSuccessfully_When_TokenIsValid() {
        // Given
        RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
                .refreshToken(REFRESH_TOKEN).build();

        RefreshToken tokenEntity = RefreshToken.builder()
                .id(1L).userId(USER_ID).token(REFRESH_TOKEN)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(0).deleted(0)
                .build();

        when(jwtTokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
        when(refreshTokenRepository.selectOne(any())).thenReturn(tokenEntity);
        when(userRepository.selectById(USER_ID)).thenReturn(mockUser);
        when(jwtTokenProvider.generateAccessToken(eq(USER_ID), eq(USERNAME), any())).thenReturn("new-access-token");
        when(jwtTokenProvider.generateRefreshToken(USER_ID)).thenReturn("new-refresh-token");
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(1800L);

        // When
        TokenResponse response = authService.refreshToken(refreshRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

        verify(refreshTokenRepository).updateById(any(RefreshToken.class));
    }

    @Test
    void should_ThrowTokenInvalidException_When_TokenIsInvalid() {
        // Given
        RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
                .refreshToken("invalid-token").build();

        when(jwtTokenProvider.validateToken("invalid-token")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshRequest))
                .isInstanceOf(TokenInvalidException.class)
                .hasMessageContaining("刷新令牌无效或已过期");
    }

    @Test
    void should_ThrowTokenInvalidException_When_TokenIsRevoked() {
        // Given
        RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
                .refreshToken(REFRESH_TOKEN).build();

        when(jwtTokenProvider.validateToken(REFRESH_TOKEN)).thenReturn(true);
        when(refreshTokenRepository.selectOne(any())).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(refreshRequest))
                .isInstanceOf(TokenInvalidException.class)
                .hasMessageContaining("刷新令牌不存在或已撤销");
    }

    // ============ 登出测试 ============

    @Test
    void should_LogoutSuccessfully_When_TokenExists() {
        // Given
        RefreshToken tokenEntity = RefreshToken.builder()
                .id(1L).userId(USER_ID).token(REFRESH_TOKEN)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(0).deleted(0)
                .build();

        when(refreshTokenRepository.selectOne(any())).thenReturn(tokenEntity);

        // When
        authService.logout(REFRESH_TOKEN);

        // Then
        verify(refreshTokenRepository).updateById(any(RefreshToken.class));
    }

    @Test
    void should_NotThrowException_When_LogoutWithNullToken() {
        // When & Then - should not throw
        authService.logout(null);
        verify(refreshTokenRepository, never()).updateById(any(RefreshToken.class));
    }

    // ============ 获取用户信息测试 ============

    @Test
    void should_GetUserInfoSuccessfully_When_UserExists() {
        // Given
        when(userRepository.selectById(USER_ID)).thenReturn(mockUser);

        // When
        UserInfoResponse response = authService.getCurrentUserInfo(USER_ID);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(USER_ID);
        assertThat(response.getUsername()).isEqualTo(USERNAME);
        assertThat(response.getNickname()).isEqualTo("测试用户");
    }

    @Test
    void should_ThrowAuthenticationException_When_UserNotExists() {
        // Given
        when(userRepository.selectById(USER_ID)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> authService.getCurrentUserInfo(USER_ID))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("用户不存在");
    }
}
