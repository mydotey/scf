package org.mydotey.scf.labeled;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public class DefaultPropertyLabels implements PropertyLabels {

    private HashMap<Object, PropertyLabel> _labelMap;
    private Collection<PropertyLabel> _labels;
    private PropertyLabels _alternative;

    public DefaultPropertyLabels(Collection<PropertyLabel> labels, PropertyLabels alternative) {
        if (labels == null || labels.isEmpty())
            throw new IllegalArgumentException("labels is null or empty");

        _labelMap = new HashMap<>();
        labels.forEach(l -> {
            if (l != null)
                _labelMap.put(l.getKey(), l);
        });

        if (_labelMap.isEmpty())
            throw new IllegalArgumentException("all elements of labels are null");
        _labels = Collections.unmodifiableCollection(_labelMap.values());

        _alternative = alternative;
    }

    @Override
    public Collection<PropertyLabel> getLabels() {
        return _labels;
    }

    @Override
    public PropertyLabels getAlternative() {
        return _alternative;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_alternative == null) ? 0 : _alternative.hashCode());
        result = prime * result + ((_labelMap == null) ? 0 : _labelMap.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        DefaultPropertyLabels labels = (DefaultPropertyLabels) obj;

        if (!Objects.equals(_labelMap, labels._labelMap))
            return false;

        if (!Objects.equals(_alternative, labels._alternative))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return String.format("%s { labels: %s, alternative: %s }", getClass().getSimpleName(), _labels, _alternative);
    }

}
