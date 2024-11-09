package com.aberk.config_app_backend.controller;

import com.aberk.config_app_backend.service.ConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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

    @GetMapping("/all")
    public ResponseEntity<List<Map<String, String>>> getAllConfigurations() throws IOException {
        return ResponseEntity.ok(configService.getAllConfigFiles());
    }

    @GetMapping("/configFile")
    public ResponseEntity<String> getConfiguration() throws IOException {
        return ResponseEntity.ok(configService.getConfigAsJson());
    }

    @GetMapping("/byFileName/{fileName}")
    public ResponseEntity<String> getConfigurationByFileName(@PathVariable String fileName) throws IOException {
        return ResponseEntity.ok(configService.getConfigAsJson(fileName));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addConfiguration(@RequestParam String fileName, @RequestBody Map<String, Object> config) {
        try {
            configService.createConfigFile(fileName, config);
            return ResponseEntity.ok("Config file created successfully: " + fileName + ".yaml");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("An error occurred while creating the configuration file " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateConfiguration(@RequestParam String fileName, @RequestBody Map<String, Object> config) {
        return configService.updateConfigFile(fileName, config);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteConfigFile(@RequestParam String fileName) {
        boolean isDeleted = configService.deleteConfigFile(fileName);

        if (isDeleted) {
            return ResponseEntity.ok(fileName + ".yaml deleted successfully.");
        } else {
            return ResponseEntity.status(404).body(fileName + ".yaml cannot found.");
        }
    }

    @GetMapping("/specificConfigFile")
    public ResponseEntity<String> getSpecificConfiguration() throws IOException {
        String spesificConfigFile = configService.getSpesificConfigAsJson();

        if (spesificConfigFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Configuration not found");
        }

        return ResponseEntity.ok(spesificConfigFile);
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
