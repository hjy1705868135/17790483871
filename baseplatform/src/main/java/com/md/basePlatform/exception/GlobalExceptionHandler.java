package com.md.basePlatform.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 全局异常处理（页面模式）。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 资源不存在。
     *
     * @param ex 异常
     * @return 错误页
     */
    @ExceptionHandler(UavNotFoundException.class)
    public ModelAndView handleNotFound(UavNotFoundException ex) {
        log.warn(ex.getMessage());
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("title", "未找到");
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    /**
     * 编号重复等业务冲突。
     *
     * @param ex 异常
     * @param redirect 重定向属性
     * @return 重定向列表页
     */
    @ExceptionHandler(DuplicateCodeException.class)
    public String handleDuplicate(DuplicateCodeException ex, RedirectAttributes redirect) {
        log.warn(ex.getMessage());
        redirect.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/uav";
    }

    /**
     * 表单校验失败。
     *
     * @param ex 异常
     * @return 错误页
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("参数校验失败");
        log.warn(msg);
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("title", "参数错误");
        mv.addObject("message", msg);
        return mv;
    }

    /**
     * 非法参数。
     *
     * @param ex 异常
     * @return 错误页
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgument(IllegalArgumentException ex) {
        log.warn(ex.getMessage());
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("title", "参数错误");
        mv.addObject("message", ex.getMessage());
        return mv;
    }

    /**
     * 其它未预期异常。
     *
     * @param ex 异常
     * @return 错误页
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAny(Exception ex) {
        log.error("Unhandled error", ex);
        ModelAndView mv = new ModelAndView("error/error");
        mv.addObject("title", "系统错误");
        mv.addObject("message", "服务暂时不可用，请稍后重试。");
        return mv;
    }
}
