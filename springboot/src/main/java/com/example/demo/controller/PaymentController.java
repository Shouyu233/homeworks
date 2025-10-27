package com.example.demo.controller;

import com.example.demo.payment.dto.PaymentRequest;
import com.example.demo.payment.dto.Payout;
import com.example.demo.payment.factory.PaymentFactory;
import com.example.demo.payment.PaymentProcessor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentFactory factory;

    public PaymentController(PaymentFactory factory) {
        this.factory = factory;
    }

    @PostMapping("/pay")
    public Payout pay(@RequestBody PaymentRequest req) {
        PaymentProcessor processor = factory.getProcessor(req.getMethod());
        return processor.pay(req);
    }
}

