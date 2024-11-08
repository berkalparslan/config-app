package com.aberk.config_app_backend.controller;

import com.aberk.config_app_backend.service.ConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/configuration")
@CrossOrigin(origins = "*")
public class ConfigurationController {

    @Autowired
    private ConfigService configService;

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
    public ResponseEntity<String> getConfiguration() throws IOException {
        return ResponseEntity.ok(configService.getConfigAsJson());
    }

    @GetMapping("/byFileName/{fileName}")
    public ResponseEntity<String> getConfigurationByFileName(@PathVariable String fileName) throws IOException {
        return ResponseEntity.ok(configService.getConfigAsJson(fileName));
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

    @GetMapping("/specific/json/all")
    public ResponseEntity<String> getSpecificConfiguration() throws IOException {
        String spesificConfigfile = configService.getSpesificConfigAsJson();

        if (spesificConfigfile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Configuration not found");
        }

        return ResponseEntity.ok(spesificConfigfile);
    }

    @GetMapping("/specific")
    public ResponseEntity<?> getSpecificConfiguration(@RequestParam String host, @RequestParam String url) throws IOException {

        String configJson = configService.getSpesificConfigAsJson();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> configMap = objectMapper.readValue(configJson, Map.class);

            Map<String, Object> datasource = (Map<String, Object>) configMap.get("datasource");

            Map<String, Object> hosts = (Map<String, Object>) datasource.get("hosts");
            Map<String, Object> urls = (Map<String, Object>) datasource.get("urls");

            if (hosts == null || !hosts.containsKey(host)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Host configuration not found");
            }

            if (urls == null || !urls.containsKey(url)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL configuration not found");
            }

            String hostConfigFile = (String) hosts.get(host);
            String urlConfigFile = (String) urls.get(url);

            Map<String, String> configFiles = new HashMap<>();
            configFiles.put("hostConfigFile", hostConfigFile);
            configFiles.put("urlConfigFile", urlConfigFile);

            return ResponseEntity.ok(objectMapper.writeValueAsString(configFiles));

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
        }
    }

}
