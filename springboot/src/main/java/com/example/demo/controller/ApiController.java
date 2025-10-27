package com.example.demo.controller;

import com.example.demo.model.UserRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    // Example using @RequestParam
    @GetMapping("/hello")
    public Map<String, Object> hello(@RequestParam(name = "name", defaultValue = "world") String name) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Hello, " + name + "!");
        resp.put("from", "@RequestParam");
        return resp;
    }

    // Example using @PathVariable
    @GetMapping("/user/{id}")
    public Map<String, Object> getUserById(@PathVariable("id") long id, 
                                          @RequestParam(name = "slow", required = false) Boolean slow) {
        // 如果slow参数为true，模拟慢请求
        if (Boolean.TRUE.equals(slow)) {
            try {
                Thread.sleep(1500); // 延迟1.5秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        Map<String, Object> resp = new HashMap<>();
        resp.put("id", id);
        resp.put("info", "Fetched user by @PathVariable");
        resp.put("slow", slow != null ? slow : false);
        return resp;
    }

    // Example using @RequestBody
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody UserRequest body) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("received", body);
        resp.put("summary", String.format("Name=%s, Age=%s, Email=%s",
                nullSafe(body.getName()),
                body.getAge() == null ? "" : body.getAge().toString(),
                nullSafe(body.getEmail())));
        resp.put("from", "@RequestBody");
        return resp;
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    /**
     * 测试异常处理的端点
     */
    @GetMapping("/user/exception")
    public Map<String, Object> testException() {
        throw new RuntimeException("这是一个测试异常");
    }

    /**
     * 测试空指针异常的端点
     */
    @GetMapping("/user/null")
    public Map<String, Object> testNullPointer() {
        String nullString = null;
        // 故意制造空指针异常
        return Map.of("length", nullString.length());
    }
}

