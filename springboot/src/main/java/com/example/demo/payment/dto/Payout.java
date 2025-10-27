package com.example.demo.payment.dto;

import java.time.OffsetDateTime;
import java.util.Map;

public class Payout {
    private boolean success;
    private String orderId;
    private String method;
    private String message;
    private OffsetDateTime paidAt;
    private Map<String, Object> echo; // 回显传入参数，便于在浏览器展示

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(OffsetDateTime paidAt) { this.paidAt = paidAt; }

    public Map<String, Object> getEcho() { return echo; }
    public void setEcho(Map<String, Object> echo) { this.echo = echo; }
}

