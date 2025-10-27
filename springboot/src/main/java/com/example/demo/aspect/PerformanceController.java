package com.example.demo.aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 性能监控Controller
 * 提供性能统计信息的查询接口
 */
@RestController
@RequestMapping("/api")
public class PerformanceController {

    @Autowired
    private PerformanceMetrics performanceMetrics;

    /**
     * 获取所有Controller方法的性能统计
     */
    @GetMapping("/metrics")
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> result = new HashMap<>();
        Map<String, PerformanceMetrics.MethodMetrics> allMetrics = performanceMetrics.getAllMetrics();
        
        Map<String, Object> metricsData = new HashMap<>();
        allMetrics.forEach((methodName, metrics) -> {
            Map<String, Object> methodData = new HashMap<>();
            methodData.put("callCount", metrics.getCallCount());
            methodData.put("totalTime", metrics.getTotalTime());
            methodData.put("averageTime", String.format("%.2fms", metrics.getAverageTime()));
            methodData.put("minTime", metrics.getMinTime() + "ms");
            methodData.put("maxTime", metrics.getMaxTime() + "ms");
            metricsData.put(methodName, methodData);
        });
        
        result.put("status", "SUCCESS");
        result.put("metrics", metricsData);
        result.put("totalMethods", allMetrics.size());
        
        return result;
    }

    /**
     * 获取性能统计概览
     */
    @GetMapping("/metrics/summary")
    public Map<String, Object> getMetricsSummary() {
        Map<String, Object> result = new HashMap<>();
        Map<String, PerformanceMetrics.MethodMetrics> allMetrics = performanceMetrics.getAllMetrics();
        
        long totalCalls = allMetrics.values().stream()
            .mapToLong(PerformanceMetrics.MethodMetrics::getCallCount)
            .sum();
        
        long totalTime = allMetrics.values().stream()
            .mapToLong(PerformanceMetrics.MethodMetrics::getTotalTime)
            .sum();
        
        double overallAverage = totalCalls > 0 ? (double) totalTime / totalCalls : 0;
        
        result.put("status", "SUCCESS");
        result.put("totalMethods", allMetrics.size());
        result.put("totalCalls", totalCalls);
        result.put("totalTime", totalTime + "ms");
        result.put("overallAverage", String.format("%.2fms", overallAverage));
        
        return result;
    }
}