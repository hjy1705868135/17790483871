package com.md.basePlatform.domain;

import java.util.List;

/**
 * 用户信息响应DTO
 */
public class UserInfoResponse {

    private Long id;
    private String username;
    private String nickname;
    private List<String> roles;
    private String createdAt;

    public UserInfoResponse() {}

    public UserInfoResponse(Long id, String username, String nickname, List<String> roles, String createdAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String nickname;
        private List<String> roles;
        private String createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder nickname(String nickname) { this.nickname = nickname; return this; }
        public Builder roles(List<String> roles) { this.roles = roles; return this; }
        public Builder createdAt(String createdAt) { this.createdAt = createdAt; return this; }

        public UserInfoResponse build() {
            return new UserInfoResponse(id, username, nickname, roles, createdAt);
        }
    }
}