using System;
using System.Collections.Generic;

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
    public class PipelineValueFilter<V> : AbstractValueFilter<V>
    {
        private List<ValueFilter<V>> _filters;

        public PipelineValueFilter(List<ValueFilter<V>> filters)
        {
            if (filters == null)
                throw new ArgumentNullException("filters is null");

            _filters = new List<ValueFilter<V>>();
            filters.ForEach(f =>
            {
                if (f != null)
                    _filters.Add(f);
            });

            if (_filters.Count == 0)
                throw new ArgumentException("filters is empty");
        }

        public override V Filter(V t)
        {
            foreach (ValueFilter<V> filter in _filters)
            {
                t = filter.Filter(t);
                if (Object.Equals(t, default(V)))
                    return default(V);
            }

            return t;
        }

        public override int GetHashCode()
        {
            int prime = 31;
            int result = 1;
            result = prime * result + ((_filters == null) ? 0 : _filters.HashCode());
            return result;
        }

        public override bool Equals(Object obj)
        {
            if (!base.Equals(obj))
                return false;

            PipelineValueFilter<V> other = (PipelineValueFilter<V>)obj;
            return _filters.Equal(other._filters);
        }

        public override string ToString()
        {
            return string.Format("{0} {{ filters: [ {1} ] }}", GetType().Name, string.Join(", ", _filters));
        }
    }
}