package org.mydotey.scf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.Property;
import org.mydotey.scf.facade.ConfigurationManagers;
import org.mydotey.scf.facade.MoreProperties;
import org.mydotey.scf.source.stringproperties.propertiesfile.PropertiesFileConfigurationSourceConfig;
import org.mydotey.scf.type.StringToIntConverter;
import org.mydotey.scf.type.StringToLongConverter;

import com.google.common.collect.Lists;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class MorePropertiesTest {

    protected ConfigurationManager createManager(String fileName) {
        PropertiesFileConfigurationSourceConfig sourceConfig = ConfigurationManagers
                .newPropertiesFileSourceConfigBuilder().setName("properties-source").setPriority(1)
                .setFileName(fileName).build();
        System.out.println("source config: " + sourceConfig + "\n");
        ConfigurationManagerConfig managerConfig = ConfigurationManagers.newManagerConfigBuilder().setName("test")
                .setSources(Lists.newArrayList(ConfigurationManagers.newPropertiesFileSource(sourceConfig))).build();
        System.out.println("manager config: " + managerConfig + "\n");
        return ConfigurationManagers.newManager(managerConfig);
    }

    protected MoreProperties createMoreProperties(String fileName) {
        ConfigurationManager manager = createManager(fileName);
        return new MoreProperties(manager);
    }

    @Test
    public void testGetProperties() {
        MoreProperties moreProperties = createMoreProperties("test.properties");
        Property<String, String> property = moreProperties.getStringProperty("not-exist");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals(null, property.getValue());

        property = moreProperties.getStringProperty("not-exist2", "default");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("default", property.getValue());

        property = moreProperties.getStringProperty("exist", "default");
        System.out.println("property: " + property + "\n");
        Assert.assertEquals("ok", property.getValue());
    }

    @Test
    public void testGetTypedProperties() {
        MoreProperties moreProperties = createMoreProperties("test.properties");
        Property<String, Integer> property = moreProperties.getIntProperty("int-value");
        System.out.println("property: " + property + "\n");
        Integer expected = Integer.valueOf(1);
        Assert.assertEquals(expected, property.getValue());

        Property<String, List<String>> property2 = moreProperties.getListProperty("list-value");
        System.out.println("property: " + property2 + "\n");
        List<String> expected2 = Lists.newArrayList("s1", "s2", "s3");
        Assert.assertEquals(expected2, property2.getValue());

        Property<String, Map<String, String>> property3 = moreProperties.getMapProperty("map-value");
        System.out.println("property: " + property3 + "\n");
        Map<String, String> expected3 = new HashMap<>();
        expected3.put("k1", "v1");
        expected3.put("k2", "v2");
        expected3.put("k3", "v3");
        Assert.assertEquals(expected3, property3.getValue());

        Property<String, List<Integer>> property4 = moreProperties.getListProperty("int-list-value",
                StringToIntConverter.DEFAULT);
        System.out.println("property: " + property4 + "\n");
        List<Integer> expected4 = Lists.newArrayList(1, 2, 3);
        Assert.assertEquals(expected4, property4.getValue());

        Property<String, Map<Integer, Long>> property5 = moreProperties.getMapProperty("int-long-map-value",
                StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT);
        System.out.println("property: " + property5 + "\n");
        Map<Integer, Long> expected5 = new HashMap<>();
        expected5.put(1, 2L);
        expected5.put(3, 4L);
        expected5.put(5, 6L);
        Assert.assertEquals(expected5, property5.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameKeyDifferentConfig() {
        MoreProperties moreProperties = createMoreProperties("test.properties");
        Property<String, Map<String, String>> property = moreProperties.getMapProperty("map-value");
        Map<String, String> expected = new HashMap<>();
        expected.put("k1", "v1");
        expected.put("k2", "v2");
        expected.put("k3", "v3");
        Assert.assertEquals(expected, property.getValue());

        moreProperties.getMapProperty("map-value", StringToIntConverter.DEFAULT, StringToLongConverter.DEFAULT);
    }

}
