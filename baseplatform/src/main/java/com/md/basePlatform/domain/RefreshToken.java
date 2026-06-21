package com.md.basePlatform.domain;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 刷新令牌实体类
 */
@TableName("t_refresh_token")
public class RefreshToken {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expiryDate;
    private Integer revoked;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic
    private Integer deleted;

    public RefreshToken() {}

    public RefreshToken(Long id, Long userId, String token, LocalDateTime expiryDate, Integer revoked,
                       LocalDateTime createdAt, LocalDateTime updatedAt, Integer deleted) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public Integer getRevoked() { return revoked; }
    public void setRevoked(Integer revoked) { this.revoked = revoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String token;
        private LocalDateTime expiryDate;
        private Integer revoked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer deleted;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder expiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; return this; }
        public Builder revoked(Integer revoked) { this.revoked = revoked; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder deleted(Integer deleted) { this.deleted = deleted; return this; }

        public RefreshToken build() {
            return new RefreshToken(id, userId, token, expiryDate, revoked, createdAt, updatedAt, deleted);
        }
    }
}