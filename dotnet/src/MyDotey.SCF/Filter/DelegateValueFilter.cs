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
    public class DelegateValueFilter<V> : AbstractValueFilter<V>
    {
        private Func<V, V> _filter;

        public DelegateValueFilter(Func<V, V> filter)
        {
            if (filter == null)
                throw new ArgumentNullException("filter is null");
            _filter = filter;
        }

        public override V Filter(V t)
        {
            return _filter(t);
        }

        public override int GetHashCode()
        {
            int prime = 31;
            int result = 1;
            result = prime * result + ((_filter == null) ? 0 : _filter.GetHashCode());
            return result;
        }

        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(this, obj))
                return true;

            if (obj == null)
                return false;

            DelegateValueFilter<V> other = (DelegateValueFilter<V>)obj;
            return object.Equals(_filter, other._filter);
        }

        public override string ToString()
        {
            return string.Format("{0} {{ filter: {1} }}", GetType().Name, _filter);
        }
    }
}