/**
 * 自定义Shiro Realm
 * Realm是Shiro获取安全数据（如用户、角色、权限）的组件
 * 自定义Realm实现身份认证（Authentication）和授权（Authorization）逻辑
 * 当前为简化实现，实际项目中应从数据库读取用户信息进行验证
 */
package com.md.basePlatform.config;

// 导入Shiro框架相关类
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 自定义Realm
 * 继承AuthorizingRealm类，实现认证和授权方法
 */
public class CustomRealm extends AuthorizingRealm {

    /**
     * 授权方法
     * 获取用户的角色和权限信息
     * 
     * @param principals 用户身份集合，包含用户的身份信息
     * @return AuthorizationInfo 授权信息对象，包含角色和权限
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 当前为简化实现，返回空的授权信息
        // 实际项目中应从数据库查询用户的角色和权限，然后构建AuthorizationInfo返回
        return null;
    }

    /**
     * 认证方法
     * 验证用户身份，检查用户名和密码是否正确
     * 
     * @param token 认证令牌，包含用户的登录凭证（用户名和密码）
     * @return AuthenticationInfo 认证信息对象，包含用户身份和凭证
     * @throws AuthenticationException 认证异常，如用户名不存在、密码错误等
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
            throws AuthenticationException {
        // 从token中获取用户名（Principal）
        String username = (String) token.getPrincipal();
        
        // 当前为简化实现，允许任意用户登录
        // 实际项目中应从数据库查询用户信息，并验证密码是否正确
        
        // 返回认证信息
        // 参数说明：
        // 1. principal: 用户身份（通常是用户名）
        // 2. credentials: 用户凭证（密码，此处留空表示跳过密码验证）
        // 3. realmName: Realm名称，通过getName()方法获取
        return new SimpleAuthenticationInfo(username, "", getName());
    }
}