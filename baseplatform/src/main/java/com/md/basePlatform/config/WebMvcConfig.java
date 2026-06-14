/**
 * Web MVC配置类
 * 配置拦截器、跨域访问等Web相关设置
 * 实现WebMvcConfigurer接口来自定义Spring MVC的行为
 */
package com.md.basePlatform.config;

// 导入自定义请求日志拦截器
import com.md.basePlatform.interceptor.RequestLogInterceptor;
// 导入Spring框架相关注解和类
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 实现WebMvcConfigurer接口，用于自定义Spring MVC配置
 * 使用@Configuration注解标记，Spring会自动扫描并加载配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 注册拦截器
     * 重写WebMvcConfigurer接口的addInterceptors方法
     * 将自定义的RequestLogInterceptor添加到拦截器链中
     * 
     * @param registry 拦截器注册器对象，用于注册和管理拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册请求日志拦截器
        // addInterceptor()方法接收一个拦截器实例
        // addPathPatterns("/**")表示拦截所有URL路径
        registry.addInterceptor(requestLogInterceptor())
                .addPathPatterns("/**");
    }

    /**
     * 配置跨域访问（CORS）
     * 重写WebMvcConfigurer接口的addCorsMappings方法
     * 允许前端应用跨域访问后端API
     * 
     * @param registry CORS注册器对象，用于配置跨域规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // addMapping("/**")表示对所有路径应用跨域配置
        registry.addMapping("/**")
                // allowedOrigins("*")表示允许所有来源的请求
                // 注意：Spring Boot 2.2.x使用allowedOrigins而非allowedOriginPatterns
                .allowedOrigins("*")
                // allowedMethods指定允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // allowedHeaders("*")表示允许所有请求头
                .allowedHeaders("*")
                // maxAge(3600)表示预检请求的缓存时间为3600秒（1小时）
                .maxAge(3600);
    }

    /**
     * 创建请求日志拦截器Bean
     * 使用@Bean注解标记，Spring会自动将其注册到容器中
     * 
     * @return RequestLogInterceptor实例，用于记录请求日志
     */
    @Bean
    public RequestLogInterceptor requestLogInterceptor() {
        // 创建并返回RequestLogInterceptor实例
        return new RequestLogInterceptor();
    }
}