package org.mydotey.scf.labeled;

import java.util.Collection;
import java.util.Collections;

/**
 * @author koqizhao
 *
 * Jun 15, 2018
 */
public interface PropertyLabels {

    /**
     * empty labels and no altenative
     */
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

    /**
     * labels
     */
    Collection<PropertyLabel> getLabels();

    /**
     * if not configured for @see {@link PropertyLabels#getLabels()}, use the alternative to have a try
     * <p>
     * default to null
     */
    PropertyLabels getAlternative();

}
