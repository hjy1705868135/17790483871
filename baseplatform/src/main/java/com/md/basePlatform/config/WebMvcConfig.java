/**
 * Web MVC配置类
 * 配置拦截器、跨域访问等Web相关设置
 * 实现WebMvcConfigurer接口来自定义Spring MVC的行为
 */
package com.md.basePlatform.config;

import com.md.basePlatform.interceptor.RequestLogInterceptor;
import com.md.basePlatform.interceptor.RequestLoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 实现WebMvcConfigurer接口，用于自定义Spring MVC配置
 * 注册多个拦截器：RequestLogInterceptor（详细日志）、RequestLoggingInterceptor（简要日志+Shiro用户）
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     * 将自定义拦截器添加到拦截器链中，排除静态资源路径
     *
     * @param registry 拦截器注册器对象
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册请求日志拦截器（详细日志：方法、URL、参数、耗时）
        registry.addInterceptor(requestLogInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/webjars/**", "/static/**", "/css/**", "/js/**", "/images/**");

        // 注册请求日志拦截器（简要日志：含Shiro用户信息）
        registry.addInterceptor(requestLoggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/webjars/**", "/static/**", "/css/**", "/js/**", "/images/**");
    }

    /**
     * 配置跨域访问（CORS）
     *
     * @param registry CORS注册器对象
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    /**
     * 创建请求日志拦截器Bean（详细日志版本）
     *
     * @return RequestLogInterceptor实例
     */
    @Bean
    public RequestLogInterceptor requestLogInterceptor() {
        return new RequestLogInterceptor();
    }

    /**
     * 创建请求日志拦截器Bean（简要日志+Shiro用户信息版本）
     *
     * @return RequestLoggingInterceptor实例
     */
    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
    }
}
