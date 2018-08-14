using System;

namespace MyDotey.SCF.Filter
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     * 
     * apply each filter to the value,
     * if null returned by a filter, stop filtering and return null,
     * otherwise, apply next filter to the value
     */
    public interface ValueFilter
    {
        object Filter(object t);
    }

    public interface ValueFilter<V> : ValueFilter
    {
        V Filter(V t);
    }
}