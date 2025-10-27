package com.example.demo.aspect;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能统计服务
 * 记录Controller方法的执行时间和调用次数
 */
@Component
public class PerformanceMetrics {

    private final Map<String, MethodMetrics> metricsMap = new ConcurrentHashMap<>();

    /**
     * 记录方法执行时间
     */
    public void recordExecutionTime(String methodName, long executionTime) {
        MethodMetrics metrics = metricsMap.computeIfAbsent(methodName, k -> new MethodMetrics());
        metrics.recordExecution(executionTime);
    }

    /**
     * 获取方法性能统计
     */
    public MethodMetrics getMethodMetrics(String methodName) {
        return metricsMap.get(methodName);
    }

    /**
     * 获取所有方法的性能统计
     */
    public Map<String, MethodMetrics> getAllMetrics() {
        return new ConcurrentHashMap<>(metricsMap);
    }

    /**
     * 方法性能统计类
     */
    public static class MethodMetrics {
        private final AtomicLong callCount = new AtomicLong(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private long minTime = Long.MAX_VALUE;
        private long maxTime = Long.MIN_VALUE;

        public void recordExecution(long executionTime) {
            callCount.incrementAndGet();
            totalTime.addAndGet(executionTime);
            minTime = Math.min(minTime, executionTime);
            maxTime = Math.max(maxTime, executionTime);
        }

        public long getCallCount() {
            return callCount.get();
        }

        public long getTotalTime() {
            return totalTime.get();
        }

        public double getAverageTime() {
            long count = callCount.get();
            long total = totalTime.get();
            return count > 0 ? (double) total / count : 0;
        }

        public long getMinTime() {
            return minTime == Long.MAX_VALUE ? 0 : minTime;
        }

        public long getMaxTime() {
            return maxTime == Long.MIN_VALUE ? 0 : maxTime;
        }
    }
}