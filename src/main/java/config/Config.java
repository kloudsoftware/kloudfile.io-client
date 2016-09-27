package config;



import helper.IOHelper;

import java.io.*;
import java.util.Properties;

/**
 * Created by larsg on 26.09.2016.
 */
public class Config {

    private static Properties properties;

    private static File configFile = new java.io.File(System.getProperty("user.home") + "/.push/push.properties");
    private static File configFileDir = new java.io.File(System.getProperty("user.home") + "/.push");

    public static int queryConfig() {
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
            Config.properties = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.valueOf(properties.getProperty("offset"));
    }


    public static Properties getProperties() {
        return properties;
    }

}
