package com.aberk.config_app_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
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

}
