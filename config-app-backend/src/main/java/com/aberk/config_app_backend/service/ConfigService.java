package com.aberk.config_app_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConfigService {

    @Value("classpath:config.yaml")
    private File configFile;

    @Value("classpath:spesific.yaml")
    private File spesificConfigFile;

    @Autowired
    private YAMLService yamlService;

    @Autowired
    private ResourceLoader resourceLoader;


    public String getConfigAsJson() throws IOException {
        return yamlService.getConfigAsString(configFile);
    }

    public String getConfigAsJson(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + fileName + ".yaml");

        return yamlService.getConfigAsString(resource.getFile());
    }

    public String getSpesificConfigAsJson() throws IOException {
        return yamlService.getConfigAsString(spesificConfigFile);
    }

    public List<Map<String, String>> getAllConfigFiles() throws IOException {
        List<Map<String, String>> yamlContents = new ArrayList<>();

        // find all yaml files in the classpath
        PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourceResolver.getResources("classpath*:*.yaml");
        for (Resource resource : resources) {
            if("application.yaml".equals(resource.getFilename())) {
                continue;
            }
            String configAsString = yamlService.getConfigAsString(resource.getFile());
            Map<String, String> yamlContent = new HashMap<>();
            yamlContent.put(resource.getFilename(), configAsString);
            yamlContents.add(yamlContent);
        }

        return yamlContents;
    }

    public ResponseEntity<String> updateConfigFile(String fileName, Map<String, Object> config) {
        String type = (String) config.get("type");
        if (!isValidConfig(type, config)) {
            return ResponseEntity.badRequest().body("Invalid or missing fields.");
        }

        Path filePath = Paths.get("src/main/resources/" + fileName);
        try {
            addConfigToFile(filePath, config);
            return ResponseEntity.ok("config add successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot add config to file.");
        }
    }

    private boolean isValidConfig(String type, Map<String, Object> config) {
        List<String> requiredFields = switch (type) {
            case "remove" -> List.of("selector");
            case "replace" -> List.of("selector", "newElement");
            case "insert" -> List.of("position", "target", "element");
            case "alter" -> List.of("oldValue", "newValue");
            default -> null;
        };

        return requiredFields != null && requiredFields.stream().allMatch(config::containsKey);
    }

    private void addConfigToFile(Path filePath, Map<String, Object> config) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlData;

        // read yaml file
        try (FileInputStream inputStream = new FileInputStream(filePath.toFile())) {
            yamlData = yaml.load(inputStream);
        }

        // if actions is not a list, initialize it as a new list
        if (yamlData != null && yamlData.containsKey("actions")) {
            List<Map<String, Object>> actions = (List<Map<String, Object>>) yamlData.get("actions");

            if (!(actions instanceof List)) {
                actions = new ArrayList<>();
                yamlData.put("actions", actions);
            }

            // add new config to actions
            actions.add(config);
        } else {
            // If "actions" is not present, create a new list and add the config
            List<Map<String, Object>> actions = new ArrayList<>();
            actions.add(config);
            yamlData.put("actions", actions);
        }

        // Write updated data to file
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yamlWriter = new Yaml(options);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            yamlWriter.dump(yamlData, writer);
        }
    }

    public void createConfigFile(String fileName, Map<String, Object> configData) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        Path filePath = Path.of("src/main/resources", fileName + ".yaml");
        File file = filePath.toFile();

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(configData, writer);
        }

        System.out.println(fileName + ".yaml file added under -> src/main/resources.");
    }

    public boolean deleteConfigFile(String fileName) {
        Path filePath = Path.of("src/main/resources", fileName + ".yaml");
        File file = filePath.toFile();

        if (file.exists()) {
            return file.delete();
        }
        return false; // if file does not exist
    }
}
