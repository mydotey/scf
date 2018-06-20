package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.labeled.DefaultLabeledConfigurationManager;
import org.mydotey.scf.labeled.LabeledConfigurationManager;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public class LabeledConfigurationManagers {

    protected LabeledConfigurationManagers() {

    }

    public static LabeledConfigurationManager newManager(ConfigurationManagerConfig config) {
        return new DefaultLabeledConfigurationManager(config);
    }

}
