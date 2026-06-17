/**
 * Shiro安全框架配置类
 * 配置Shiro的核心组件：安全管理器、过滤器、自定义Realm等
 * Shiro是一个强大且易用的Java安全框架，提供身份认证、授权等功能
 */
package com.md.basePlatform.config;

// 导入Shiro框架相关类
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
// 导入Spring框架相关注解
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 导入Java集合框架
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置类
 * 配置Shiro安全框架的核心组件
 * 使用@Configuration注解标记，Spring会自动扫描并加载配置
 */
@Configuration
public class ShiroConfig {

    /**
     * 创建自定义Realm
     * Realm是Shiro用于获取用户认证和授权信息的组件
     * 自定义Realm实现身份验证和权限验证逻辑
     * 
     * @return CustomRealm实例
     */
    @Bean
    public CustomRealm customRealm() {
        // 返回自定义Realm实例
        return new CustomRealm();
    }

    /**
     * 创建安全管理器（SecurityManager）
     * 安全管理器是Shiro的核心组件，负责管理所有安全操作
     * 
     * @return DefaultSecurityManager实例
     */
    @Bean
    public DefaultSecurityManager securityManager() {
        // 创建DefaultWebSecurityManager实例（专门用于Web应用）
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        
        // 设置自定义Realm，安全管理器通过Realm获取用户信息
        securityManager.setRealm(customRealm());
        
        // 返回安全管理器实例
        return securityManager;
    }

    /**
     * 创建Shiro过滤器工厂
     * 配置URL访问权限规则，决定哪些URL需要认证，哪些可以匿名访问
     * 
     * @param securityManager 安全管理器，由Spring自动注入
     * @return ShiroFilterFactoryBean实例
     */
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultSecurityManager securityManager) {
        // 创建ShiroFilterFactoryBean实例
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        
        // 设置安全管理器
        shiroFilter.setSecurityManager(securityManager);
        
        // 设置登录页面URL（当用户未登录时重定向到此页面）
        shiroFilter.setLoginUrl("/index.html");
        
        // 设置未授权页面URL（当用户无权限访问时重定向到此页面）
        shiroFilter.setUnauthorizedUrl("/index.html");
        
        // 配置URL权限规则
        // 使用LinkedHashMap保证顺序（Shiro按顺序匹配URL）
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        
        // 静态资源允许匿名访问（无需登录）
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/index.html", "anon");
        filterChainDefinitionMap.put("/app.js", "anon");
        
        // API接口允许匿名访问（演示系统，简化配置）
        filterChainDefinitionMap.put("/api/**", "anon");
        
        // 其他所有请求允许匿名访问（演示系统，简化配置）
        filterChainDefinitionMap.put("/**", "anon");
        
        // 将规则设置到过滤器工厂
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        
        // 返回过滤器工厂实例
        return shiroFilter;
    }
}
