package com.example.demo.payment.dto;

import com.example.demo.payment.PaymentMethod;

import java.math.BigDecimal;
import java.util.Map;

public class PaymentRequest {
    private String orderId;
    private BigDecimal amount;
    private String currency = "CNY";
    private PaymentMethod method;
    // 可选的附加参数，如 openId、cardNo 等
    private Map<String, Object> extra;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public Map<String, Object> getExtra() { return extra; }
    public void setExtra(Map<String, Object> extra) { this.extra = extra; }
}

