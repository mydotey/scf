package org.mydotey.scf.facade;

import org.mydotey.scf.ConfigurationManager;
import org.mydotey.scf.ConfigurationManagerConfig;
import org.mydotey.scf.labeled.LabeledConfigurationManager;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public class LabeledManagers {

    protected LabeledManagers() {

    }

    public static ConfigurationManager newManager(ConfigurationManagerConfig config) {
        return new LabeledConfigurationManager(config);
    }

}
