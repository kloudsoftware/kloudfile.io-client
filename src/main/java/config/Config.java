package config;


import helper.IOHelper;
import lombok.Cleanup;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.util.Properties;

@Log4j
public class Config {

    @Getter
    private static Config instance = new Config();

    private File configFile = new java.io.File(System.getProperty("user.home") + "/.push/push.properties");
    private File configFileDir = new java.io.File(System.getProperty("user.home") + "/.push");
    private Properties properties;

    public Config() {
        try {
            @Cleanup InputStream fsin = this.getClass().getClassLoader().getResourceAsStream("push.properties");
            if (!configFile.exists()) {
                configFileDir.mkdirs();
                @Cleanup FileOutputStream fout = new FileOutputStream(configFile);
                configFile.createNewFile();
                IOHelper.copy(fsin, fout);
                fout.flush();
            }
            this.properties = new Properties();
            @Cleanup FileInputStream sysIn = new FileInputStream(configFile);
            properties.load(sysIn);
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }

    public Properties getProperties() {
        return properties;
    }
}
