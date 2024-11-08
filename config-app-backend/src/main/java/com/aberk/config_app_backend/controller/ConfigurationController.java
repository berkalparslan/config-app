package com.aberk.config_app_backend.controller;

import com.aberk.config_app_backend.service.YAMLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/configuration")
@CrossOrigin(origins = "*")
public class ConfigurationController {

    @Autowired
    private YAMLService yamlService;

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getConfigurationById(@PathVariable String id) {
        Map<String, Object> config = Map.of(
                "id", id,
                "actions", List.of(
                        Map.of("type", "remove", "selector", ".ad-banner"),
                        Map.of("type", "replace", "selector", "#old-header", "newElement", "<header id='new-header'>New Header</header>")
                )
        );
        return ResponseEntity.ok(config);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllConfigurations() {
        List<Map<String, Object>> configs = List.of(
                Map.of("id", "1", "actions", List.of(
                        Map.of("type", "remove", "selector", ".ad-banner")
                )),
                Map.of("id", "2", "actions", List.of(
                        Map.of("type", "replace", "selector", "#old-header", "newElement", "<header id='new-header'>New Header</header>")
                ))
        );
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/all/json")
    public String getConfiguration() throws IOException {
        return yamlService.getConfigAsJson();
    }

    @PostMapping
    public ResponseEntity<String> addConfiguration(@RequestBody Map<String, Object> config) {
        return ResponseEntity.ok("Configuration added successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateConfiguration(@PathVariable String id, @RequestBody Map<String, Object> config) {
        return ResponseEntity.ok("Configuration updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConfiguration(@PathVariable String id) {
        return ResponseEntity.ok("Configuration deleted successfully.");
    }
}
