package com.loan_manage.utils.arbitration.util;

import java.io.FileInputStream;
import java.util.Properties;

public class Util_properties {
    public Util_properties() {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "\\arbitration-config.properties";
        Admin_prope(path);
    }

    private Properties properties = new Properties();

    public void Admin_prope(String path) {
        try {
            UtilPrint.sysLog("path=" + path);
            this.properties.load(new FileInputStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConfig(String key) {
        return this.properties.getProperty(key);
    }

    public int getConfigKeyInt(String key) {
        try {
            return Integer.valueOf(this.properties.getProperty(key)).intValue();
        } catch (Exception e) {
        }
        return 0;
    }

    public boolean getConfigKeyBoolen(String key) {
        try {
            return Boolean.valueOf(this.properties.getProperty(key)).booleanValue();
        } catch (Exception e) {
        }
        return false;
    }
}
