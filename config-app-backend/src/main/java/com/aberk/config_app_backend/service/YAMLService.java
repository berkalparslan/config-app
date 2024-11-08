package com.aberk.config_app_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;

@Service
public class YAMLService {

    @Value("classpath:config.yaml")
    private File configFile;

    public String getConfigAsJson() throws IOException {
        Yaml yaml = new Yaml();
        ObjectMapper objectMapper = new ObjectMapper();

        Object yamlData = yaml.load(configFile.toURI().toURL().openStream());

        return objectMapper.writeValueAsString(yamlData);
    }
}
