package com.techzen.techsale.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/client-api/v1")
public class AbstractController {

    public <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    public ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
