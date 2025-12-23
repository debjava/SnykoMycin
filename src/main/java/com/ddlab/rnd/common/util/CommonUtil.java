package com.ddlab.rnd.common.util;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

    private static Properties properties = null;

    public final static Map<String,String> BUILDTYPEMAP = Map.of("pom.xml", "maven",
            "build.gradle", "gradle", "build.gradle.kts", "kotlin", "package.json", "npm");

    public static String getResourceContentAsText(String filePath) {
        String fileContent = null;
        try (InputStream inputStream = CommonUtil.class
                .getClassLoader()
                .getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: "+filePath);
            }
            // Read all bytes and convert to String (Java 17)
            fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error while reading file from resources: {}", filePath, e);
        }
        return fileContent;
    }

    public static String getProperty(String key) {
        if (properties == null) {
            properties = new Properties();
            properties = getConfig();
        }
        return properties.getProperty(key);
    }

    public static Properties getConfig() {
        Properties properties = new Properties();
        String propertiesFileName = "config/config.properties";
        try (InputStream inputStream = CommonUtil.class
                .getClassLoader()
                .getResourceAsStream(propertiesFileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: "+propertiesFileName);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            log.error("Error while reading file from resources: ",  e);
        }

        return properties;
    }

}
