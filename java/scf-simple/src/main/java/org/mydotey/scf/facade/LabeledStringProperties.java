package org.mydotey.scf.facade;

import org.mydotey.scf.labeled.LabeledConfigurationManager;
import org.mydotey.scf.labeled.LabeledKey;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class LabeledStringProperties extends StringValueProperties<LabeledKey<String>> {

    public LabeledStringProperties(LabeledConfigurationManager manager) {
        super(manager);
    }

    @Override
    public LabeledConfigurationManager getManager() {
        return (LabeledConfigurationManager) super.getManager();
    }

}
