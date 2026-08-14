package com.cc.blogserver.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.cc.blogserver.common.Result;
import com.cc.blogserver.exception.BusinessException;
import com.cc.blogserver.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Objects;

/**
 * 全局异常处理，统一转换为 Result 返回
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        log.warn("业务异常, code={}, message={}", e.getErrorCode().getCode(), e.getMessage());
        return Result.fail(e.getErrorCode().getCode(), e.getMessage());
    }

    /**
     * sa-token 未登录，按场景类型转成具体提示
     */
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException e, HttpServletRequest request) {
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供token";
            case NotLoginException.INVALID_TOKEN -> "token无效";
            case NotLoginException.TOKEN_TIMEOUT -> "token已过期";
            case NotLoginException.BE_REPLACED -> "账号已在其它设备登录";
            case NotLoginException.KICK_OUT -> "已被强制下线";
            case NotLoginException.TOKEN_FREEZE -> "token已冻结";
            case NotLoginException.NO_PREFIX -> "未提供token";
            default -> "未登录";
        };
        log.warn("未登录访问, type={}, uri={}", e.getType(), request.getRequestURI());
        return Result.fail(ErrorCode.UNAUTHORIZED.getCode(), message);
    }

    @ExceptionHandler({NotPermissionException.class, NotRoleException.class})
    public Result<Void> handleNotPermission(Exception e, HttpServletRequest request) {
        log.warn("权限不足, uri={}, message={}", request.getRequestURI(), e.getMessage());
        return Result.fail(ErrorCode.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleArgNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> Objects.isNull(fieldError.getDefaultMessage()) ? ErrorCode.PARAM_INVALID.getMessage() : fieldError.getDefaultMessage())
                .orElse(ErrorCode.PARAM_INVALID.getMessage());
        log.warn("参数校验失败(body), message={}", message);
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .findFirst()
                .map(v -> Objects.isNull(v.getMessage()) ? ErrorCode.PARAM_INVALID.getMessage() : v.getMessage())
                .orElse(ErrorCode.PARAM_INVALID.getMessage());
        log.warn("参数校验失败(param), message={}", message);
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持, method={}, uri={}", e.getMethod(), request.getRequestURI());
        return Result.fail(ErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandler(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("接口不存在, uri={}", request.getRequestURI());
        return Result.fail(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("文件上传超过大小限制, message={}", e.getMessage());
        return Result.fail(ErrorCode.PARAM_INVALID.getCode(), "上传文件大小超过限制");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常, uri={}", request.getRequestURI(), e);
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }
}
