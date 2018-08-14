using System;
using System.Collections.Generic;

namespace MyDotey.SCF.Type.String
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringToDictionaryConverter<K, V> : StringConverter<Dictionary<K, V>>
    {
        private TypeConverter<string, K> _keyConverter;
        private TypeConverter<string, V> _valueConverter;

        public StringToDictionaryConverter(TypeConverter<string, K> keyConverter, TypeConverter<string, V> valueConverter)
        {
            if (keyConverter == null)
                throw new ArgumentNullException("keyConverter is null");

            if (valueConverter == null)
                throw new ArgumentNullException("valueConverter is null");

            _keyConverter = keyConverter;
            _valueConverter = valueConverter;
        }

        public override Dictionary<K, V> Convert(string source)
        {
            if (string.IsNullOrWhiteSpace(source))
                return null;
            source = source.Trim();

            Dictionary<K, V> map = null;
            string[] array = source.Split(',');
            foreach (string s in array)
            {
                if (string.IsNullOrWhiteSpace(s))
                    continue;

                string str = s.Trim();
                string[] keyValueParts = str.Split(':');
                if (keyValueParts.Length != 2)
                    continue;

                if (string.IsNullOrWhiteSpace(keyValueParts[0]) || string.IsNullOrWhiteSpace(keyValueParts[1]))
                    continue;

                string part1 = keyValueParts[0].Trim();
                string part2 = keyValueParts[1].Trim();
                K key = _keyConverter.Convert(part1);
                V value = _valueConverter.Convert(part2);
                if (key == null || value == null)
                    continue;

                if (map == null)
                    map = new Dictionary<K, V>();

                map[key] = value;
            }

            return map;
        }

        public override int GetHashCode()
        {
            int prime = 31;
            int result = base.GetHashCode();
            result = prime * result + ((_keyConverter == null) ? 0 : _keyConverter.GetHashCode());
            result = prime * result + ((_valueConverter == null) ? 0 : _valueConverter.GetHashCode());
            return result;
        }

        public override bool Equals(Object obj)
        {
            if (!base.Equals(obj))
                return false;

            StringToDictionaryConverter<K, V> other = (StringToDictionaryConverter<K, V>)obj;
            return Object.Equals(_keyConverter, other._keyConverter)
                    && Object.Equals(_valueConverter, other._valueConverter);
        }

        public override string ToString()
        {
            return string.Format("{0} {{ sourceType: {1}, targetType: {2}, keyConverter: {3}, valueConverter: {4} }}",
                    GetType().Name, SourceType, TargetType, _keyConverter, _valueConverter);
        }
    }

    public class StringToDictionaryConverter : StringToDictionaryConverter<string, string>
    {
        public static readonly StringToDictionaryConverter DEFAULT = new StringToDictionaryConverter();

        protected StringToDictionaryConverter()
            : base(StringInplaceConverter.DEFAULT, StringInplaceConverter.DEFAULT)
        {

        }
    }
}