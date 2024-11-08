package com.aberk.config_app_backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/specific")
@CrossOrigin(origins = "*")
public class SpecificConfigurationController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getSpecificConfiguration(
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String page) {
        // Belirli bir host, url veya sayfaya göre özel konfigürasyon döner
        Map<String, Object> specificConfig = Map.of(
                "datasource", Map.of(
                        "pages", Map.of("list", "A.yaml", "details", "B.yaml"),
                        "urls", Map.of("/products", "A.yaml", "/orders", "B.yaml"),
                        "hosts", Map.of("example.com", "A.yaml", "another.com", "B.yaml")
                )
        );
        return ResponseEntity.ok(specificConfig);
    }
}
