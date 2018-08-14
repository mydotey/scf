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
    public abstract class AbstractValueFilter<V> : ValueFilter<V>
    {
        object ValueFilter.Filter(object v)
        {
            return Filter((V)v);
        }

        public abstract V Filter(V t);

        public override int GetHashCode()
        {
            return GetType().GetHashCode();
        }

        public override bool Equals(Object obj)
        {
            if (object.ReferenceEquals(this, obj))
                return true;

            if (obj == null)
                return false;

            return GetType() == obj.GetType();
        }

        public override string ToString()
        {
            return GetType().Name;
        }
    }
}