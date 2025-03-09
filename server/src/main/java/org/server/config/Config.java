package org.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private Properties properties;

    public Config() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("attributes.properties")) {
            if (input == null) {
                throw new RuntimeException("Файл config.properties не найден!");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке config.properties", e);
        }
    }

    public String getDbUrl(){
        return properties.getProperty("db.url");
    }
    public String getDbUser(){
        return properties.getProperty("db.user");
    }
    public String getDbPassword(){
        return properties.getProperty("db.password");
    }
}
