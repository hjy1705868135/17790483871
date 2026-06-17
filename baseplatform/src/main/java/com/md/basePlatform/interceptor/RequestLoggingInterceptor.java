package com.md.basePlatform.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求日志拦截器：打印方法、路径、查询串、客户端 IP、耗时与当前登录主体（若有）。
 * 注意：该拦截器通过 WebMvcConfig 中的 @Bean 方法注册，不在此处使用 @Component。
 */
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String ATTR_START = RequestLoggingInterceptor.class.getName() + ".start";

    /**
     * 记录请求开始时间。
     *
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @return 是否继续处理
     */
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        request.setAttribute(ATTR_START, System.currentTimeMillis());
        return true;
    }

    /**
     * 请求完成后输出汇总日志。
     *
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @param ex 异常（若有）
     */
    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex) {
        Long start = (Long) request.getAttribute(ATTR_START);
        long durationMs = start == null ? -1L : System.currentTimeMillis() - start;
        String user = resolveUser();

        log.info(
                "HTTP {} {} | query={} | ip={} | status={} | {} ms | user={}",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                response.getStatus(),
                durationMs,
                user);
    }

    private static String resolveUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            Object principal = subject.getPrincipal();
            return principal != null ? principal.toString() : "authenticated";
        }
        return "anonymous";
    }
}
