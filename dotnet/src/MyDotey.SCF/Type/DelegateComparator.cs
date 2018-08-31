using System;
using System.Collections.Generic;

namespace MyDotey.SCF.Type
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class DelegateComparator<V> : IComparer<V>
    {
        private Func<V, V, int> _comparator;

        public DelegateComparator(Func<V, V, int> comparator)
        {
            if (comparator == null)
                throw new ArgumentNullException("comparator is null");
            _comparator = comparator;
        }

        public virtual int Compare(V o1, V o2)
        {
            return _comparator(o1, o2);
        }

        public override int GetHashCode()
        {
            int prime = 31;
            int result = 1;
            result = prime * result + ((_comparator == null) ? 0 : _comparator.GetHashCode());
            return result;
        }

        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(this, obj))
                return true;

            if (obj == null)
                return false;

            DelegateComparator<V> other = (DelegateComparator<V>)obj;
            return object.Equals(_comparator, other._comparator);
        }

        public override string ToString()
        {
            return string.Format("{0} {{ comparator: {1} }}", GetType().Name, _comparator);
        }
    }
}