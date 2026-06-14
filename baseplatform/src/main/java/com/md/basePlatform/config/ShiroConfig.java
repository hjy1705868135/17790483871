package com.md.basePlatform.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Apache Shiro：最小可用内存账号（admin / admin123）。
 */
@Configuration
public class ShiroConfig {

    /**
     * 内存账号认证域。
     *
     * @return realm
     */
    @Bean
    public Realm realm() {
        SimpleAccountRealm realm = new SimpleAccountRealm();
        realm.setName("simpleRealm");
        realm.addAccount("admin", "admin123");
        return realm;
    }

    /**
     * Web 安全管理器。
     *
     * @param realm 认证域
     * @return SecurityManager
     */
    @Bean(name = "securityManager")
    public SecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        return manager;
    }

    /**
     * Shiro 过滤器工厂。
     *
     * @param securityManager 安全管理器
     * @return ShiroFilterFactoryBean
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl("/login");
        factoryBean.setSuccessUrl("/uav");

        Map<String, String> chain = new LinkedHashMap<>();
        chain.put("/login", "anon");
        chain.put("/logout", "anon");
        chain.put("/webjars/**", "anon");
        chain.put("/css/**", "anon");
        chain.put("/js/**", "anon");
        chain.put("/favicon.ico", "anon");
        chain.put("/api/**", "anon");
        chain.put("/**", "authc");
        factoryBean.setFilterChainDefinitionMap(chain);
        return factoryBean;
    }
}
