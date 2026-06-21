package com.md.basePlatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.md.basePlatform.config.JwtTokenProvider;
import com.md.basePlatform.domain.*;
import com.md.basePlatform.exception.AuthenticationException;
import com.md.basePlatform.exception.OrderException;
import com.md.basePlatform.repository.AddressRepository;
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
 * 用户服务
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AddressRepository addressRepository,
                      RefreshTokenRepository refreshTokenRepository, JwtTokenProvider jwtTokenProvider,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 用户注册
     */
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.info("Register request for user: {}", request.getUsername());

        // 检查密码确认
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException("两次密码输入不一致");
        }

        // 检查用户名是否已存在
        User existingUser = userRepository.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, request.getUsername())
                        .eq(User::getDeleted, 0)
        );

        if (existingUser != null) {
            throw new AuthenticationException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRoles("USER");
        user.setStatus("ACTIVE");
        user.setFailedAttempts(0);
        user.setDeleted(0);

        userRepository.insert(user);
        log.info("User registered successfully: {}", user.getUsername());

        // 生成token
        List<String> roles = Arrays.asList("USER");
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // 保存refresh token
        saveRefreshToken(user.getId(), refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserInfoResponse updateUserInfo(Long userId, UpdateUserRequest request) {
        User user = userRepository.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new AuthenticationException("用户不存在");
        }

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        userRepository.updateById(user);
        log.info("User info updated: {}", userId);

        return buildUserInfoResponse(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        User user = userRepository.selectById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new AuthenticationException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthenticationException("原密码错误");
        }

        // 确认新密码
        if (!newPassword.equals(confirmPassword)) {
            throw new AuthenticationException("两次密码输入不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.updateById(user);
        log.info("Password changed for user: {}", userId);
    }

    /**
     * 获取用户地址列表
     */
    public List<AddressResponse> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getDeleted, 0)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreatedAt)
        );

        return addresses.stream().map(this::buildAddressResponse).toList();
    }

    /**
     * 添加收货地址
     */
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        // 如果设为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            List<Address> existingAddresses = addressRepository.selectList(
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId, userId)
                            .eq(Address::getDeleted, 0)
            );
            for (Address addr : existingAddresses) {
                addr.setIsDefault(0);
                addressRepository.updateById(addr);
            }
        }

        Address address = new Address();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);
        address.setDeleted(0);

        addressRepository.insert(address);
        log.info("Address added for user: {}, addressId: {}", userId, address.getId());

        return buildAddressResponse(address);
    }

    /**
     * 更新收货地址
     */
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.selectById(addressId);
        if (address == null || address.getDeleted() == 1) {
            throw new OrderException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new OrderException("无权限修改此地址");
        }

        // 如果设为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            List<Address> existingAddresses = addressRepository.selectList(
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId, userId)
                            .eq(Address::getDeleted, 0)
                            .ne(Address::getId, addressId)
            );
            for (Address addr : existingAddresses) {
                addr.setIsDefault(0);
                addressRepository.updateById(addr);
            }
        }

        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetailAddress(request.getDetailAddress());
        address.setPostalCode(request.getPostalCode());
        address.setIsDefault(Boolean.TRUE.equals(request.getIsDefault()) ? 1 : 0);

        addressRepository.updateById(address);
        log.info("Address updated for user: {}, addressId: {}", userId, addressId);

        return buildAddressResponse(address);
    }

    /**
     * 删除收货地址
     */
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.selectById(addressId);
        if (address == null || address.getDeleted() == 1) {
            throw new OrderException("地址不存在");
        }
        if (!address.getUserId().equals(userId)) {
            throw new OrderException("无权限删除此地址");
        }

        address.setDeleted(1);
        addressRepository.updateById(address);
        log.info("Address deleted for user: {}, addressId: {}", userId, addressId);
    }

    /**
     * 获取默认收货地址
     */
    public AddressResponse getDefaultAddress(Long userId) {
        Address address = addressRepository.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, 1)
                        .eq(Address::getDeleted, 0)
        );

        if (address == null) {
            // 如果没有默认地址，返回第一个
            address = addressRepository.selectOne(
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId, userId)
                            .eq(Address::getDeleted, 0)
                            .orderByDesc(Address::getCreatedAt)
            );
        }

        return address != null ? buildAddressResponse(address) : null;
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(refreshToken);
        tokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));
        tokenEntity.setRevoked(0);
        tokenEntity.setDeleted(0);
        refreshTokenRepository.insert(tokenEntity);
    }

    private UserInfoResponse buildUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(Arrays.asList(user.getRoles().split(",")))
                .createdAt(user.getCreatedAt().toString())
                .build();
    }

    private AddressResponse buildAddressResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setUserId(address.getUserId());
        response.setReceiverName(address.getReceiverName());
        response.setPhone(address.getPhone());
        response.setProvince(address.getProvince());
        response.setCity(address.getCity());
        response.setDistrict(address.getDistrict());
        response.setDetailAddress(address.getDetailAddress());
        response.setPostalCode(address.getPostalCode());
        response.setIsDefault(address.getIsDefault() == 1);
        response.setCreatedAt(address.getCreatedAt() != null ? address.getCreatedAt().toString() : null);
        return response;
    }
}
