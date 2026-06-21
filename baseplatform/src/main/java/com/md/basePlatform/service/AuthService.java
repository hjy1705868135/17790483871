package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.md.basePlatform.config.JwtTokenProvider;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.exception.AuthenticationException;
import com.md.basePlatform.exception.TokenInvalidException;
import com.md.basePlatform.exception.UserLockedException;
import com.md.basePlatform.repository.RefreshTokenRepository;
import com.md.basePlatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 认证服务
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                      JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;

    /**
     * 用户登录
     */
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        User user = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, loginRequest.getUsername())
                        .eq(User::getDeleted, 0)
        );

        if (user == null) {
            throw new AuthenticationException("用户名或密码错误");
        }

        checkAccountStatus(user);

        if (isAccountLocked(user)) {
            throw new UserLockedException("账户已锁定，请" + getRemainingLockTime(user) + "分钟后重试");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            handleFailedLogin(user);
            throw new AuthenticationException("用户名或密码错误");
        }

        resetFailedAttempts(user);

        List<String> roles = Arrays.asList(user.getRoles().split(","));
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        saveRefreshToken(user.getId(), refreshToken);

        log.info("User logged in successfully: {}", user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    /**
     * 刷新令牌
     */
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new TokenInvalidException("刷新令牌无效或已过期");
        }

        RefreshToken tokenEntity = refreshTokenRepository.selectOne(
                new LambdaQueryWrapper<RefreshToken>()
                        .eq(RefreshToken::getToken, refreshToken)
                        .eq(RefreshToken::getRevoked, 0)
                        .eq(RefreshToken::getDeleted, 0)
        );

        if (tokenEntity == null) {
            throw new TokenInvalidException("刷新令牌不存在或已撤销");
        }

        if (tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            revokeRefreshToken(tokenEntity);
            throw new TokenInvalidException("刷新令牌已过期");
        }

        User user = userRepository.selectById(tokenEntity.getUserId());
        if (user == null || user.getDeleted() == 1) {
            throw new TokenInvalidException("用户不存在");
        }

        List<String> roles = Arrays.asList(user.getRoles().split(","));
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        revokeRefreshToken(tokenEntity);
        saveRefreshToken(user.getId(), newRefreshToken);

        log.info("Token refreshed successfully for user: {}", user.getUsername());

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    /**
     * 用户登出
     */
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            RefreshToken tokenEntity = refreshTokenRepository.selectOne(
                    new LambdaQueryWrapper<RefreshToken>()
                            .eq(RefreshToken::getToken, refreshToken)
                            .eq(RefreshToken::getRevoked, 0)
                            .eq(RefreshToken::getDeleted, 0)
            );

            if (tokenEntity != null) {
                revokeRefreshToken(tokenEntity);
                log.info("User logged out successfully");
            }
        }
    }

    /**
     * 获取当前用户信息
     */
    public UserInfoResponse getCurrentUserInfo(Long userId) {
        User user = userRepository.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new AuthenticationException("用户不存在");
        }

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(Arrays.asList(user.getRoles().split(",")))
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    private void checkAccountStatus(User user) {
        if ("DISABLED".equals(user.getStatus())) {
            throw new AuthenticationException("账户已被禁用");
        }

        if ("LOCKED".equals(user.getStatus()) && isAccountLocked(user)) {
            throw new UserLockedException("账户已锁定，请" + getRemainingLockTime(user) + "分钟后重试");
        }
    }

    private void handleFailedLogin(User user) {
        int failedAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(failedAttempts);

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            user.setStatus("LOCKED");
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
            log.warn("User account locked due to too many failed attempts: {}", user.getUsername());
        }

        userRepository.updateById(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLockedUntil(null);
        if ("LOCKED".equals(user.getStatus())) {
            user.setStatus("ACTIVE");
        }
        userRepository.updateById(user);
    }

    private boolean isAccountLocked(User user) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }

    private long getRemainingLockTime(User user) {
        if (user.getLockedUntil() == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes();
    }

    private void saveRefreshToken(Long userId, String token) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .token(token)
                .expiryDate(expiryDate)
                .revoked(0)
                .build();

        refreshTokenRepository.insert(refreshToken);
    }

    private void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(1);
        refreshTokenRepository.updateById(refreshToken);
    }
}