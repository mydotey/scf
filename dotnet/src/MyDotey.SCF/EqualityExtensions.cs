using System;
using System.Collections.Generic;
using System.Linq;

namespace MyDotey.SCF
{
    public static class EqualityExtensions
    {
        public static int HashCode<T>(this IEnumerable<T> collection)
        {
            if (collection == null)
                return 0;

            unchecked
            {
                int prime = 31;
                int result = 1;
                foreach (T item in collection)
                {
                    result = prime * result + ((item == null) ? 0 : item.GetHashCode());
                }

                return result;
            }
        }

        public static bool Equal<T>(this IEnumerable<T> c1, IEnumerable<T> c2)
        {
            if (object.ReferenceEquals(c1, c2))
                return true;

            if (c1 == null || c2 == null)
                return false;

            return c1.SequenceEqual(c2);
        }
    }
}