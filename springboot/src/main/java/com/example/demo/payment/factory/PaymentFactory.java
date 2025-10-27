package com.example.demo.payment.factory;

import com.example.demo.payment.PaymentMethod;
import com.example.demo.payment.PaymentProcessor;
import com.example.demo.payment.impl.AlipayProcessor;
import com.example.demo.payment.impl.CreditCardProcessor;
import com.example.demo.payment.impl.WeChatPayProcessor;
import org.springframework.stereotype.Component;

@Component
public class PaymentFactory {

    public PaymentProcessor getProcessor(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException("Payment method is required");
        }
        return switch (method) {
            case WECHAT -> new WeChatPayProcessor();
            case ALIPAY -> new AlipayProcessor();
            case CREDIT_CARD -> new CreditCardProcessor();
        };
    }
}

