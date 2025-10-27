package com.example.demo.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller切面类
 * 统一处理Controller层的异常、日志打印和性能统计
 */
@Aspect
@Component
public class ControllerAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    @Autowired
    private PerformanceMetrics performanceMetrics;

    /**
     * 统一处理所有Controller方法的切面
     * 包括：异常处理、日志打印、性能统计
     */
    @Around("execution(* com.example.demo.controller.*.*(..))")
    public Object handleControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        // 记录方法开始执行
        long startTime = System.currentTimeMillis();
        logger.info("Controller方法开始执行 - {}.{}, 参数: {}", 
            className, methodName, Arrays.toString(args));

        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 记录性能统计
            performanceMetrics.recordExecutionTime(className + "." + methodName, executionTime);

            // 记录方法执行完成和耗时
            logger.info("Controller方法执行完成 - {}.{}, 耗时: {}ms", 
                className, methodName, executionTime);

            // 性能警告：如果执行时间超过1秒
            if (executionTime > 1000) {
                logger.warn("Controller方法执行较慢 - {}.{}, 耗时: {}ms", 
                className, methodName, executionTime);
            }

            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 记录异常信息
            logger.error("Controller方法执行异常 - {}.{}, 耗时: {}ms, 异常信息: {}", 
                className, methodName, executionTime, e.getMessage(), e);

            // 根据方法返回类型构建统一的错误响应
            if (signature.getReturnType().equals(ResponseEntity.class)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "ERROR");
                errorResponse.put("message", "系统内部错误: " + e.getMessage());
                return ResponseEntity.status(500).body(errorResponse);
            } else {
                // 对于其他返回类型，返回统一的错误响应
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "ERROR");
                errorResponse.put("message", "系统内部错误: " + e.getMessage());
                return errorResponse;
            }
        }
    }

    /**
     * 专门处理支付相关Controller的方法
     */
    @Around("execution(* com.example.demo.controller.PaymentController.*(..))")
    public Object handlePaymentMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();

        logger.info("支付操作开始 - {}.{}", className, methodName);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 记录性能统计
            performanceMetrics.recordExecutionTime(className + "." + methodName, executionTime);

            logger.info("支付操作完成 - {}.{}, 耗时: {}ms", 
                className, methodName, executionTime);

            return result;

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.error("支付操作异常 - {}.{}, 耗时: {}ms, 异常信息: {}", 
                className, methodName, executionTime, e.getMessage(), e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "PAYMENT_ERROR");
            errorResponse.put("message", "支付处理失败: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}