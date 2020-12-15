package org.mydotey.scf.facade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mydotey.java.ObjectExtension;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.ConfigurationSource;
import org.mydotey.scf.DefaultConfigurationManager;
import org.mydotey.scf.DefaultConfigurationManagerConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class ConfigurationManagers {

    protected ConfigurationManagers() {

    }

    public static ConfigurationManager newManager(ConfigurationSource... sources) {
        return newManager("application", sources);
    }

    public static ConfigurationManager newManager(String name, ConfigurationSource... sources) {
        ObjectExtension.requireNonNullOrEmpty(sources, "sources");
        return newManager(name, Arrays.asList(sources));
    }

    public static ConfigurationManager newManager(List<ConfigurationSource> sources) {
        return newManager("application", sources);
    }

    public static ConfigurationManager newManager(String name, List<ConfigurationSource> sources) {
        ObjectExtension.requireNonNullOrEmpty(sources, "sources");
        HashMap<Integer, ConfigurationSource> sourceMap = new HashMap<>();
        for (int i = sources.size(); i > 0; i--) {
            sourceMap.put(i, sources.get(i - 1));
        }
        return newManager(sourceMap);
    }

    public static ConfigurationManager newManager(Map<Integer, ConfigurationSource> sources) {
        return newManager("application", sources);
    }

    public static ConfigurationManager newManager(String name, Map<Integer, ConfigurationSource> sources) {
        ObjectExtension.requireNonNullOrEmpty(sources, "sources");
        ConfigurationManagerConfig config = newConfigBuilder().setName(name).addSources(sources).build();
        return newManager(config);
    }

    public static ConfigurationManagerConfig.Builder newConfigBuilder() {
        return new DefaultConfigurationManagerConfig.Builder();
    }

    public static ConfigurationManager newManager(ConfigurationManagerConfig config) {
        ObjectExtension.requireNonNull(config, "config");
        return new DefaultConfigurationManager(config);
    }

}
