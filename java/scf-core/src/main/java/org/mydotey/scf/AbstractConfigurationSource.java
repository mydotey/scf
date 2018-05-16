package org.mydotey.scf;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koqizhao
 *
 * May 16, 2018
 */
public abstract class AbstractConfigurationSource implements ConfigurationSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfigurationSource.class);

    private String _name;
    private int _priority;

    private volatile List<Consumer<ConfigurationSource>> _changeListeners;

    public AbstractConfigurationSource(String name, int priority) {
        Objects.requireNonNull(name, "name is null");
        name = name.trim();
        if (name.isEmpty())
            throw new IllegalArgumentException("name is empty or whitespace");

        _name = name;
        _priority = priority;
    }

    @Override
    public String name() {
        return _name;
    }

    @Override
    public int priority() {
        return _priority;
    }

    @Override
    public synchronized void addChangeListener(Consumer<ConfigurationSource> changeListener) {
        if (_changeListeners == null)
            _changeListeners = new ArrayList<>();

        Objects.requireNonNull("changeListener", "changeListener is null");
        _changeListeners.add(changeListener);
    }

    protected void raiseChangeEvent() {
        if (_changeListeners == null)
            return;

        _changeListeners.forEach(l -> {
            try {
                l.accept(AbstractConfigurationSource.this);
            } catch (Exception e) {
                LOGGER.error("configuration source change listener failed to run", e);
            }
        });
    }

}
