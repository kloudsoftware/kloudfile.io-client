package me.probE466.config;


import me.probE466.helper.IOHelper;

import java.io.*;
import java.util.Properties;

/**
 * Created by larsg on 26.09.2016.
 */
public class Config {

    private Properties properties;

    private File configFile = new java.io.File(System.getProperty("user.home") + "/.push/push.properties");
    private File configFileDir = new java.io.File(System.getProperty("user.home") + "/.push");

    public int queryConfig() {
        Properties properties = new Properties();
//        InputStream fsin = this.getClass().getClassLoader().getResourceAsStream("push.properties");
        FileInputStream fsin = null;
        try {
            fsin = new FileInputStream(new File("push.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("push.properties not found in classpath");
        }
        if (!configFile.exists()) {
            configFileDir.mkdirs();
            try (FileOutputStream fout = new FileOutputStream(configFile)) {
                configFile.createNewFile();
                IOHelper.copy(fsin, fout);
                fout.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            FileInputStream sysIn = new FileInputStream(configFile);
            properties.load(sysIn);
            this.properties = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.valueOf(properties.getProperty("offset"));
    }


    public Properties getProperties() {
        return properties;
    }

}
