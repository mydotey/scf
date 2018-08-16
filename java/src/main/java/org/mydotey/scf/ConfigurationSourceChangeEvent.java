package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * Jul 19, 2018
 */
public interface ConfigurationSourceChangeEvent {

    ConfigurationSource getSource();

    long getChangeTime();

}
