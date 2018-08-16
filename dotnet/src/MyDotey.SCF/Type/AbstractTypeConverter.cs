using System;
using sType = System.Type;

namespace MyDotey.SCF.Type
{
    /**
     * @author koqizhao
     *
     * May 22, 2018
     */
    public abstract class AbstractTypeConverter<S, T> : ITypeConverter<S, T>
    {
        public virtual sType SourceType { get; protected set; }
        public virtual sType TargetType { get; protected set; }

        public AbstractTypeConverter()
        {
            SourceType = typeof(S);
            TargetType = typeof(T);
        }

        public virtual object Convert(object source)
        {
            return Convert((S)source);
        }

        public abstract T Convert(S source);

        public override int GetHashCode()
        {
            int prime = 31;
            int result = 1;
            result = prime * result + typeof(S).GetHashCode();
            result = prime * result + typeof(T).GetHashCode();
            return result;
        }

        public override bool Equals(object obj)
        {
            if (object.ReferenceEquals(this, obj))
                return true;

            if (obj == null)
                return false;

            return GetType() == obj.GetType();
        }

        public override String ToString()
        {
            return string.Format("{0} {{ sourceType: {1}, targetType: {2} }}", GetType().Name, SourceType,
                TargetType);
        }
    }
}