package org.mydotey.scf.util;

import java.util.Comparator;
import java.util.Objects;

import org.mydotey.java.StringExtension;
import org.mydotey.scf.Property;

/**
 * Created by Qiang Zhao on 10/05/2016.
 */
@SuppressWarnings("rawtypes")
public class PropertyComparator implements Comparator<Property> {

    public static final PropertyComparator DEFAULT = new PropertyComparator();

    @Override
    public int compare(Property o1, Property o2) {
        if (o1 == o2)
            return 0;

        String key1 = StringExtension.toString(o1.getConfig().getKey());
        String key2 = StringExtension.toString(o1.getConfig().getKey());
        if (Objects.equals(key1, key2))
            return 0;
        if (key1 == null)
            return -1;
        if (key2 == null)
            return 1;
        return key1.compareTo(key2);
    }

    @Override
    public String toString() {
        return "PropertyComparator []";
    }

}
