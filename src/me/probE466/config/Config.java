package me.probE466.config;


import sun.misc.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by larsg on 26.09.2016.
 */
public class Config {

    private File configFile = new java.io.File(System.getProperty("user.home") + "/.push/push.properties");

    public void genereateConfig() {
        Properties properties = new Properties();
        InputStream fsin = getClass().getClassLoader().getResourceAsStream("push.properties");
        if(fsin == null) {
            System.out.println("push.properties not found in classpath");
        }
        if(!configFile.exists()) {
            configFile.mkdirs();
            try {
                configFile.createNewFile();
                FileOutputStream fout = new FileOutputStream(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        try {
            properties.load(fsin);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
