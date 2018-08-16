package org.mydotey.scf;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * Jul 19, 2018
 */
public class DefaultConfigurationSourceChangeEvent implements ConfigurationSourceChangeEvent {

    private ConfigurationSource _source;
    private long _changeTime;

    public DefaultConfigurationSourceChangeEvent(ConfigurationSource source) {
        this(source, System.currentTimeMillis());
    }

    public DefaultConfigurationSourceChangeEvent(ConfigurationSource source, long changeTime) {
        Objects.requireNonNull(source, "source is null");
        _source = source;
        _changeTime = changeTime;
    }

    @Override
    public ConfigurationSource getSource() {
        return _source;
    }

    @Override
    public long getChangeTime() {
        return _changeTime;
    }

    @Override
    public String toString() {
        return String.format("%s { source: %s, changeTime: %s }", getClass().getSimpleName(), _source, _changeTime);
    }

}
