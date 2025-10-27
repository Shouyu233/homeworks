package com.example.demo.payment;

import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.Payout;

public interface PaymentProcessor {
    Payout pay(PaymentRequest request);
}

