package ru.geekbrains.lesson3;

import org.junit.jupiter.api.BeforeAll;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AbstractTest {

    static Properties prop = new Properties();
    private static InputStream configFile;
    private static String apiKey;
    private static String baseUrl;


    @BeforeAll
    static void initTest() throws IOException {
        configFile = new FileInputStream("src/main/project.properties/project.properties");
        prop.load(configFile);

        apiKey = prop.getProperty("apiKey");
        baseUrl = prop.getProperty("base");
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getApiKey() {
        return apiKey;
    }

}
