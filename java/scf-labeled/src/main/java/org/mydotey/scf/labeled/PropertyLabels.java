package org.mydotey.scf.labeled;

import java.util.Collection;

/**
 * @author koqizhao
 *
 * Jun 15, 2018
 */
public interface PropertyLabels {

    Collection<PropertyLabel> getLabels();

    PropertyLabels getAlternative();

}
