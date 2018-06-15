package org.mydotey.scf.labeled;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public class TestDataCenterSetting implements Cloneable {

    public static final String DC_KEY = "dc";
    public static final String APP_KEY = "app";

    private String key;
    private String value;

    private String dc;
    private String app;

    public TestDataCenterSetting(String key, String value, String dc, String app) {
        super();
        this.key = key;
        this.value = value;
        this.dc = dc;
        this.app = app;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    @Override
    public TestDataCenterSetting clone() {
        try {
            return (TestDataCenterSetting) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((app == null) ? 0 : app.hashCode());
        result = prime * result + ((dc == null) ? 0 : dc.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        TestDataCenterSetting setting = (TestDataCenterSetting) obj;

        if (!Objects.equals(key, setting.key))
            return false;

        if (!Objects.equals(dc, setting.dc))
            return false;

        if (!Objects.equals(app, setting.app))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return String.format("%s { key: %s, value: %s, dc: %s, app: %s }", getClass().getSimpleName(), key, value, dc,
                app);
    }

}
