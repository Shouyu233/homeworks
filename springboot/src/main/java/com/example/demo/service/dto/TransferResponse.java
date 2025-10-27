package com.example.demo.service.dto;

import java.math.BigDecimal;

public class TransferResponse {
    private long fromUserId;
    private long toUserId;
    private BigDecimal amount;
    private BigDecimal fromBalance;
    private BigDecimal toBalance;
    private String status; // OK or FAILED
    private String message;

    public long getFromUserId() { return fromUserId; }
    public void setFromUserId(long fromUserId) { this.fromUserId = fromUserId; }

    public long getToUserId() { return toUserId; }
    public void setToUserId(long toUserId) { this.toUserId = toUserId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public BigDecimal getFromBalance() { return fromBalance; }
    public void setFromBalance(BigDecimal fromBalance) { this.fromBalance = fromBalance; }

    public BigDecimal getToBalance() { return toBalance; }
    public void setToBalance(BigDecimal toBalance) { this.toBalance = toBalance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
