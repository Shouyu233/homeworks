package com.example.demo.payment.impl;

import com.example.demo.payment.PaymentProcessor;
import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.Payout;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class CreditCardProcessor implements PaymentProcessor {
    private String maskCard(String cardNo) {
        if (cardNo == null || cardNo.length() < 4) return "****";
        String last4 = cardNo.substring(cardNo.length() - 4);
        return "**** **** **** " + last4;
    }

    @Override
    public Payout pay(PaymentRequest request) {
        Payout p = new Payout();
        p.setSuccess(true);
        p.setOrderId(request.getOrderId());
        p.setMethod("CREDIT_CARD");
        p.setPaidAt(OffsetDateTime.now());
        p.setMessage("模拟信用卡支付成功");
        Map<String, Object> echo = new HashMap<>();
        echo.put("amount", request.getAmount());
        echo.put("currency", request.getCurrency());
        Map<String, Object> extra = request.getExtra();
        if (extra != null && extra.containsKey("cardNo")) {
            Object raw = extra.get("cardNo");
            extra.put("cardNoMasked", maskCard(raw == null ? null : raw.toString()));
            extra.remove("cardNo"); // 不回显原始卡号
        }
        echo.put("extra", extra);
        p.setEcho(echo);
        return p;
    }
}

