package com.aberk.config_app_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;

@Service
public class YAMLService {
    public String getConfigAsString(File file) throws IOException {
        Yaml yaml = new Yaml();
        ObjectMapper objectMapper = new ObjectMapper();

        Object yamlData = yaml.load(file.toURI().toURL().openStream());

        return objectMapper.writeValueAsString(yamlData);
    }
}
