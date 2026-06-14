/**
 * 请求日志拦截器
 * 实现Spring MVC的HandlerInterceptor接口
 * 用于记录所有HTTP请求的详细信息，包括：
 * - 请求方法（GET/POST/PUT/DELETE等）
 * - 请求URL和查询参数
 * - 客户端IP地址和User-Agent
 * - 请求处理耗时
 * - 响应状态码
 */
package com.md.basePlatform.interceptor;

// 导入SLF4J日志框架
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// 导入Spring MVC拦截器相关类
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

// 导入Java Servlet相关类
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
// 导入Java枚举类，用于遍历请求参数
import java.util.Enumeration;

/**
 * 请求日志拦截器
 * 实现HandlerInterceptor接口，在请求处理的三个阶段执行：
 * 1. preHandle: 请求处理前调用（记录请求信息）
 * 2. postHandle: 请求处理后、视图渲染前调用
 * 3. afterCompletion: 请求完全完成后调用（记录响应信息和耗时）
 */
public class RequestLogInterceptor implements HandlerInterceptor {

    /**
     * 日志记录器实例
     * 使用LoggerFactory根据类名创建Logger对象
     */
    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);

    /**
     * 请求处理前调用
     * 在Controller方法执行之前被调用
     * 记录请求的详细信息，包括方法、URL、参数等
     * 
     * @param request  HTTP请求对象，包含请求的所有信息
     * @param response HTTP响应对象，包含响应的所有信息
     * @param handler  处理器对象，即被调用的Controller方法
     * @return boolean 返回true表示继续执行后续拦截器和处理器，返回false表示中断请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取当前时间戳，用于计算请求处理时间
        long startTime = System.currentTimeMillis();
        // 将开始时间存储到request的属性中，以便在afterCompletion中使用
        request.setAttribute("startTime", startTime);

        // 获取HTTP请求方法（GET、POST、PUT、DELETE等）
        String method = request.getMethod();
        // 获取请求的URI（统一资源标识符）
        String requestURI = request.getRequestURI();
        // 获取请求的查询字符串（URL中?后面的部分）
        String queryString = request.getQueryString();
        // 获取客户端的IP地址
        String remoteAddr = request.getRemoteAddr();
        // 获取客户端的User-Agent（浏览器信息）
        String userAgent = request.getHeader("User-Agent");

        // 创建StringBuilder对象，用于构建日志消息
        StringBuilder logMessage = new StringBuilder();
        // 开始构建日志消息
        logMessage.append("【请求开始】")                    // 添加请求开始标识
                  .append("方法: ").append(method).append(", ")  // 添加请求方法
                  .append("URL: ").append(requestURI);           // 添加请求URL
        
        // 如果查询字符串不为空，将其添加到日志消息中
        if (queryString != null) {
            logMessage.append("?").append(queryString);
        }
        
        // 添加远程地址和User-Agent信息
        logMessage.append(", 远程地址: ").append(remoteAddr)
                  .append(", User-Agent: ").append(userAgent);

        // 获取请求参数的名称枚举
        Enumeration<String> parameterNames = request.getParameterNames();
        // 如果存在请求参数，将其添加到日志消息中
        if (parameterNames.hasMoreElements()) {
            logMessage.append(", 参数: {");
            // 遍历所有参数
            while (parameterNames.hasMoreElements()) {
                // 获取参数名
                String paramName = parameterNames.nextElement();
                // 获取参数值（可能有多个值，如复选框）
                String[] paramValues = request.getParameterValues(paramName);
                // 如果参数值不为空
                if (paramValues != null && paramValues.length > 0) {
                    logMessage.append(paramName).append("=");
                    // 如果只有一个参数值
                    if (paramValues.length == 1) {
                        logMessage.append(paramValues[0]);
                    } else {
                        // 如果有多个参数值，用方括号包裹
                        logMessage.append("[");
                        for (int i = 0; i < paramValues.length; i++) {
                            if (i > 0) logMessage.append(", ");
                            logMessage.append(paramValues[i]);
                        }
                        logMessage.append("]");
                    }
                    // 如果还有更多参数，添加逗号分隔
                    if (parameterNames.hasMoreElements()) {
                        logMessage.append(", ");
                    }
                }
            }
            logMessage.append("}");
        }

        // 使用INFO级别记录日志
        logger.info(logMessage.toString());
        // 返回true，允许请求继续执行
        return true;
    }

    /**
     * 请求处理后调用（视图渲染前）
     * 在Controller方法执行之后、视图渲染之前被调用
     * 可在此处添加额外的日志记录（如模型数据）
     * 
     * @param request       HTTP请求对象
     * @param response      HTTP响应对象
     * @param handler       处理器对象
     * @param modelAndView  模型和视图对象，包含要渲染的数据和视图信息
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, 
                          ModelAndView modelAndView) {
        // 当前为空实现，可根据需要添加日志记录逻辑
        // 例如：记录模型数据、视图名称等
    }

    /**
     * 请求完成后调用（视图渲染后）
     * 在请求完全完成后被调用，包括视图渲染完成
     * 记录请求处理时间和响应状态码
     * 
     * @param request   HTTP请求对象
     * @param response  HTTP响应对象
     * @param handler   处理器对象
     * @param ex        异常对象（如果请求处理过程中发生异常）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                Object handler, Exception ex) {
        // 从request属性中获取请求开始时间
        long startTime = (Long) request.getAttribute("startTime");
        // 获取当前时间，计算请求处理耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 获取HTTP响应状态码（如200、404、500等）
        int statusCode = response.getStatus();

        // 使用INFO级别记录请求结束日志
        logger.info("【请求结束】URL: {}, 状态码: {}, 耗时: {}ms", 
                    request.getRequestURI(), statusCode, duration);

        // 如果存在异常，记录异常信息
        if (ex != null) {
            logger.error("【请求异常】URL: {}, 异常: {}", request.getRequestURI(), ex.getMessage(), ex);
        }
    }
}