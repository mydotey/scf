using System;

namespace MyDotey.SCF.Type
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class DefaultTypeConverter<S, T> : AbstractTypeConverter<S, T>
    {
        private Func<S, T> _typeConverter;

        public DefaultTypeConverter(Func<S, T> typeConverter)
        {
            if (typeConverter == null)
                throw new ArgumentNullException("typeConverter is null");
            _typeConverter = typeConverter;
        }

        public override T Convert(S source)
        {
            return _typeConverter(source);
        }
    }
}