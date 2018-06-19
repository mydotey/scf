package org.mydotey.scf.labeled;

import java.util.Collection;
import java.util.Collections;

/**
 * @author koqizhao
 *
 * Jun 15, 2018
 */
public interface PropertyLabels {

    PropertyLabels EMPTY = new PropertyLabels() {
        @Override
        public Collection<PropertyLabel> getLabels() {
            return Collections.emptyList();
        }

        @Override
        public PropertyLabels getAlternative() {
            return null;
        }

        @Override
        public String toString() {
            return "EMPTY";
        }
    };

    Collection<PropertyLabel> getLabels();

    PropertyLabels getAlternative();

}
