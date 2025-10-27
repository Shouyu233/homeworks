package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.UserService;
import com.example.demo.service.dto.TransferRequest;
import com.example.demo.service.dto.TransferResponse;

@RestController
@RequestMapping("/api")
public class TransferController {
    private final UserService userService;

    public TransferController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest req) {
        try {
            TransferResponse resp = userService.transferAndReturn(req);
            if ("OK".equals(resp.getStatus())) {
                return ResponseEntity.ok(resp);
            }
            // failed business case -> 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
        } catch (Exception e) {
            TransferResponse err = new TransferResponse();
            err.setStatus("FAILED");
            err.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
