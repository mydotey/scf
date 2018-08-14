using System;
using System.Collections.Generic;

namespace MyDotey.SCF.Type.String
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringToListConverter<V> : StringConverter<List<V>>
    {
        private TypeConverter<string, V> _typeConverter;

        public StringToListConverter(TypeConverter<string, V> typeConverter)
        {
            if (typeConverter == null)
                throw new ArgumentNullException("typeConverter is null");

            _typeConverter = typeConverter;
        }

        public override List<V> Convert(string source)
        {
            if (string.IsNullOrWhiteSpace(source))
                return null;
            source = source.Trim();

            List<V> list = null;
            string[] array = source.Split(',');
            foreach (string s in array)
            {
                if (string.IsNullOrWhiteSpace(s))
                    continue;

                string str = s.Trim();
                V v = _typeConverter.Convert(str);
                if (v == null)
                    continue;

                if (list == null)
                    list = new List<V>();

                list.Add(v);
            }

            return list;
        }

        public override int GetHashCode()
        {
            int prime = 31;
            int result = base.GetHashCode();
            result = prime * result + ((_typeConverter == null) ? 0 : _typeConverter.GetHashCode());
            return result;
        }

        public override bool Equals(Object obj)
        {
            if (!base.Equals(obj))
                return false;

            StringToListConverter<V> other = (StringToListConverter<V>)obj;
            return Object.Equals(_typeConverter, other._typeConverter);
        }

        public override string ToString()
        {
            return string.Format("{0} {{ sourceType: {1}, targetType: {2}, typeConverter: {3} }}", GetType().Name,
                    SourceType, TargetType, _typeConverter);
        }
    }

    public class StringToListConverter : StringToListConverter<string>
    {
        public static readonly StringToListConverter DEFAULT = new StringToListConverter();

        protected StringToListConverter()
            : base(StringInplaceConverter.DEFAULT)
        {

        }
    }
}