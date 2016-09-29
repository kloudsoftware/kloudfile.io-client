package config;


import helper.IOHelper;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class Config {

    private static File configFile = new java.io.File(System.getProperty("user.home") + "/.push/push.properties");
    private static File configFileDir = new java.io.File(System.getProperty("user.home") + "/.push");
    private Properties properties;

    public Config() throws IOException {
        try (InputStream fsin = this.getClass().getClassLoader().getResourceAsStream("push.properties")) {
            if (!configFile.exists()) {
                configFileDir.mkdirs();
                try (FileOutputStream fout = new FileOutputStream(configFile)) {
                    configFile.createNewFile();
                    IOHelper.copy(fsin, fout);
                    fout.flush();
                }
            }
        }
        this.properties = new Properties();
        try (FileInputStream sysIn = new FileInputStream(configFile)) {
            properties.load(sysIn);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
