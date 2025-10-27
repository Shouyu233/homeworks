package com.example.demo.payment.impl;

import com.example.demo.payment.PaymentProcessor;
import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.Payout;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class AlipayProcessor implements PaymentProcessor {
    @Override
    public Payout pay(PaymentRequest request) {
        Payout p = new Payout();
        p.setSuccess(true);
        p.setOrderId(request.getOrderId());
        p.setMethod("ALIPAY");
        p.setPaidAt(OffsetDateTime.now());
        p.setMessage("模拟支付宝支付成功");
        Map<String, Object> echo = new HashMap<>();
        echo.put("amount", request.getAmount());
        echo.put("currency", request.getCurrency());
        echo.put("extra", request.getExtra());
        p.setEcho(echo);
        return p;
    }
}

