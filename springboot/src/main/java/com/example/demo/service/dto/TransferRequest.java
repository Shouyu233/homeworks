package com.example.demo.service.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private long fromUserId;
    private long toUserId;
    private BigDecimal amount;

    public long getFromUserId() { return fromUserId; }
    public void setFromUserId(long fromUserId) { this.fromUserId = fromUserId; }

    public long getToUserId() { return toUserId; }
    public void setToUserId(long toUserId) { this.toUserId = toUserId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
