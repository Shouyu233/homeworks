package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.db.User;

@RestController
@RequestMapping("/api/users")
public class UserJdbcController {
    private final com.example.demo.service.UserService service;

    public UserJdbcController(com.example.demo.service.UserService service) {
        this.service = service;
    }

    // backend 可选: jt(默认), raw
    @GetMapping
    public List<User> list(@RequestParam(value = "backend", required = false) String backend) throws Exception {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable long id,
                                    @RequestParam(value = "backend", required = false) String backend) throws Exception {
        Optional<User> u = service.findById(id);
        return u.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User body,
                                       @RequestParam(value = "backend", required = false) String backend) throws Exception {
        if (body.getName() == null || body.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    long id = service.create(body);
    return service.findById(id)
                .map(u -> ResponseEntity.status(HttpStatus.CREATED).body(u))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable long id,
                                       @RequestBody User body,
                                       @RequestParam(value = "backend", required = false) String backend) throws Exception {
        if (body.getName() == null || body.getName().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        boolean ok = service.update(id, body);
        if (!ok) return ResponseEntity.notFound().build();
        return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id,
                                       @RequestParam(value = "backend", required = false) String backend) throws Exception {
        boolean ok = service.delete(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
