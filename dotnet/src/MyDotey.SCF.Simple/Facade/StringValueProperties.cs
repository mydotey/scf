using System;
using System.Collections.Generic;

using MyDotey.SCF.Type;
using MyDotey.SCF.Type.String;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringValueProperties<K>
    {
        private ConfigurationManager _manager;

        public StringValueProperties(ConfigurationManager manager)
        {
            if (manager == null)
                throw new ArgumentNullException("manager is null");
            _manager = manager;
        }

        public ConfigurationManager getManager()
        {
            return _manager;
        }

        public Property<K, String> getStringProperty(K key)
        {
            return getStringProperty(key, null);
        }

        public String getStringPropertyValue(K key)
        {
            return getStringPropertyValue(key, null);
        }

        public Property<K, String> getStringProperty(K key, String defaultValue)
        {
            return getStringProperty(key, defaultValue, null);
        }

        public String getStringPropertyValue(K key, String defaultValue)
        {
            return getStringPropertyValue(key, defaultValue, null);
        }

        public Property<K, String> getStringProperty(K key, String defaultValue, Func<String, String> valueFilter)
        {
            return getProperty(key, defaultValue, StringInplaceConverter.DEFAULT, valueFilter);
        }

        public String getStringPropertyValue(K key, String defaultValue, Func<String, String> valueFilter)
        {
            return getPropertyValue(key, defaultValue, StringInplaceConverter.DEFAULT, valueFilter);
        }

        public Property<K, int?> getIntProperty(K key)
        {
            return getIntProperty(key, null);
        }

        public int? getIntPropertyValue(K key)
        {
            return getIntPropertyValue(key, null);
        }

        public Property<K, int?> getIntProperty(K key, int? defaultValue)
        {
            return getIntProperty(key, defaultValue, null);
        }

        public int? getIntPropertyValue(K key, int? defaultValue)
        {
            return getIntPropertyValue(key, defaultValue, null);
        }

        public Property<K, int?> getIntProperty(K key, int? defaultValue, Func<int?, int?> valueFilter)
        {
            return getProperty(key, defaultValue, StringToIntConverter.DEFAULT, valueFilter);
        }

        public int? getIntPropertyValue(K key, int? defaultValue, Func<int?, int?> valueFilter)
        {
            return getPropertyValue(key, defaultValue, StringToIntConverter.DEFAULT, valueFilter);
        }

        public Property<K, long?> getLongProperty(K key)
        {
            return getLongProperty(key, null);
        }

        public long? getLongPropertyValue(K key)
        {
            return getLongPropertyValue(key, null);
        }

        public Property<K, long?> getLongProperty(K key, long? defaultValue)
        {
            return getLongProperty(key, defaultValue, null);
        }

        public long? getLongPropertyValue(K key, long? defaultValue)
        {
            return getLongPropertyValue(key, defaultValue, null);
        }

        public Property<K, long?> getLongProperty(K key, long? defaultValue, Func<long?, long?> valueFilter)
        {
            return getProperty(key, defaultValue, StringToLongConverter.DEFAULT, valueFilter);
        }

        public long? getLongPropertyValue(K key, long? defaultValue, Func<long?, long?> valueFilter)
        {
            return getPropertyValue(key, defaultValue, StringToLongConverter.DEFAULT, valueFilter);
        }

        public Property<K, float?> getFloatProperty(K key)
        {
            return getFloatProperty(key, null);
        }

        public float? getFloatPropertyValue(K key)
        {
            return getFloatPropertyValue(key, null);
        }

        public Property<K, float?> getFloatProperty(K key, float? defaultValue)
        {
            return getFloatProperty(key, defaultValue, null);
        }

        public float? getFloatPropertyValue(K key, float? defaultValue)
        {
            return getFloatPropertyValue(key, defaultValue, null);
        }

        public Property<K, float?> getFloatProperty(K key, float? defaultValue, Func<float?, float?> valueFilter)
        {
            return getProperty(key, defaultValue, StringToFloatConverter.DEFAULT, valueFilter);
        }

        public float? getFloatPropertyValue(K key, float? defaultValue, Func<float?, float?> valueFilter)
        {
            return getPropertyValue(key, defaultValue, StringToFloatConverter.DEFAULT, valueFilter);
        }

        public Property<K, double?> getDoubleProperty(K key)
        {
            return getDoubleProperty(key, null);
        }

        public double? getDoublePropertyValue(K key)
        {
            return getDoublePropertyValue(key, null);
        }

        public Property<K, double?> getDoubleProperty(K key, double? defaultValue)
        {
            return getDoubleProperty(key, defaultValue, null);
        }

        public double? getDoublePropertyValue(K key, double? defaultValue)
        {
            return getDoublePropertyValue(key, defaultValue, null);
        }

        public Property<K, double?> getDoubleProperty(K key, double? defaultValue, Func<double?, double?> valueFilter)
        {
            return getProperty(key, defaultValue, StringToDoubleConverter.DEFAULT, valueFilter);
        }

        public double? getDoublePropertyValue(K key, double? defaultValue, Func<double?, double?> valueFilter)
        {
            return getPropertyValue(key, defaultValue, StringToDoubleConverter.DEFAULT, valueFilter);
        }

        public Property<K, bool?> getBooleanProperty(K key)
        {
            return getBooleanProperty(key, null);
        }

        public bool? getBooleanPropertyValue(K key)
        {
            return getBooleanPropertyValue(key, null);
        }

        public Property<K, bool?> getBooleanProperty(K key, bool? defaultValue)
        {
            return getBooleanProperty(key, defaultValue, null);
        }

        public bool? getBooleanPropertyValue(K key, bool? defaultValue)
        {
            return getBooleanPropertyValue(key, defaultValue, null);
        }

        public Property<K, bool?> getBooleanProperty(K key, bool? defaultValue,
                Func<bool?, bool?> valueFilter)
        {
            return getProperty(key, defaultValue, StringToBooleanConverter.DEFAULT, valueFilter);
        }

        public bool? getBooleanPropertyValue(K key, bool? defaultValue, Func<bool?, bool?> valueFilter)
        {
            return getPropertyValue(key, defaultValue, StringToBooleanConverter.DEFAULT, valueFilter);
        }

        public Property<K, List<String>> getListProperty(K key)
        {
            return getListProperty(key, (List<String>)null);
        }

        public List<String> getListPropertyValue(K key)
        {
            return getListPropertyValue(key, (List<String>)null);
        }

        public Property<K, List<String>> getListProperty(K key, List<String> defaultValue)
        {
            return getProperty(key, defaultValue, StringToListConverter.DEFAULT);
        }

        public List<String> getListPropertyValue(K key, List<String> defaultValue)
        {
            return getPropertyValue(key, defaultValue, StringToListConverter.DEFAULT);
        }

        public Property<K, List<V>> getListProperty<V>(K key, TypeConverter<String, V> typeConverter)
        {
            return getListProperty(key, null, typeConverter);
        }

        public List<V> getListPropertyValue<V>(K key, TypeConverter<String, V> typeConverter)
        {
            return getListPropertyValue(key, null, typeConverter);
        }

        public Property<K, List<V>> getListProperty<V>(K key, List<V> defaultValue,
                TypeConverter<String, V> typeConverter)
        {
            return getListProperty(key, defaultValue, typeConverter, null);
        }

        public List<V> getListPropertyValue<V>(K key, List<V> defaultValue, TypeConverter<String, V> typeConverter)
        {
            return getListPropertyValue(key, defaultValue, typeConverter, null);
        }

        public Property<K, List<V>> getListProperty<V>(K key, List<V> defaultValue, TypeConverter<String, V> typeConverter,
                Func<List<V>, List<V>> valueFilter)
        {
            return getProperty(key, defaultValue, new StringToListConverter<V>(typeConverter), valueFilter);
        }

        public List<V> getListPropertyValue<V>(K key, List<V> defaultValue, TypeConverter<String, V> typeConverter,
                Func<List<V>, List<V>> valueFilter)
        {
            return getPropertyValue(key, defaultValue, new StringToListConverter<V>(typeConverter), valueFilter);
        }

        public Property<K, Dictionary<String, String>> getDictionaryProperty(K key)
        {
            return getDictionaryProperty(key, null);
        }

        public Dictionary<String, String> getDictionaryPropertyValue(K key)
        {
            return getDictionaryPropertyValue(key, null);
        }

        public Property<K, Dictionary<String, String>> getDictionaryProperty(K key, Dictionary<String, String> defaultValue)
        {
            return getProperty(key, defaultValue, StringToDictionaryConverter.DEFAULT);
        }

        public Dictionary<String, String> getDictionaryPropertyValue(K key, Dictionary<String, String> defaultValue)
        {
            return getPropertyValue(key, defaultValue, StringToDictionaryConverter.DEFAULT);
        }

        public Property<K, Dictionary<MK, MV>> getDictionaryProperty<MK, MV>(K key, TypeConverter<String, MK> keyConverter,
                TypeConverter<String, MV> valueConverter)
        {
            return getDictionaryProperty(key, null, keyConverter, valueConverter);
        }

        public Dictionary<MK, MV> getDictionaryPropertyValue<MK, MV>(K key, TypeConverter<String, MK> keyConverter,
                TypeConverter<String, MV> valueConverter)
        {
            return getDictionaryPropertyValue(key, null, keyConverter, valueConverter);
        }

        public Property<K, Dictionary<MK, MV>> getDictionaryProperty<MK, MV>(K key, Dictionary<MK, MV> defaultMValue,
                TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter)
        {
            return getDictionaryProperty(key, defaultMValue, keyConverter, valueConverter, null);
        }

        public Dictionary<MK, MV> getDictionaryPropertyValue<MK, MV>(K key, Dictionary<MK, MV> defaultMValue,
                TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter)
        {
            return getDictionaryPropertyValue(key, defaultMValue, keyConverter, valueConverter, null);
        }

        public Property<K, Dictionary<MK, MV>> getDictionaryProperty<MK, MV>(K key, Dictionary<MK, MV> defaultMValue,
                TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter,
                Func<Dictionary<MK, MV>, Dictionary<MK, MV>> valueFilter)
        {
            return getProperty(key, defaultMValue, new StringToDictionaryConverter<MK, MV>(keyConverter, valueConverter), valueFilter);
        }

        public Dictionary<MK, MV> getDictionaryPropertyValue<MK, MV>(K key, Dictionary<MK, MV> defaultValue,
                TypeConverter<String, MK> keyConverter, TypeConverter<String, MV> valueConverter,
                Func<Dictionary<MK, MV>, Dictionary<MK, MV>> valueFilter)
        {
            return getPropertyValue(key, defaultValue, new StringToDictionaryConverter<MK, MV>(keyConverter, valueConverter),
                    valueFilter);
        }

        public Property<K, V> getProperty<V>(K key, TypeConverter<String, V> valueConverter)
        {
            return getProperty(key, default(V), valueConverter);
        }

        public V getPropertyValue<V>(K key, TypeConverter<String, V> valueConverter)
        {
            return getPropertyValue(key, default(V), valueConverter);
        }

        public Property<K, V> getProperty<V>(K key, V defaultValue, TypeConverter<String, V> valueConverter)
        {
            return getProperty(key, defaultValue, valueConverter, null);
        }

        public V getPropertyValue<V>(K key, V defaultValue, TypeConverter<String, V> valueConverter)
        {
            return getPropertyValue(key, defaultValue, valueConverter, null);
        }

        public Property<K, V> getProperty<V>(K key, V defaultValue,
                TypeConverter<String, V> valueConverter, Func<V, V> valueFilter)
        {
            PropertyConfig<K, V> propertyConfig = createPropertyConfig(key, defaultValue, valueConverter,
                    valueFilter);
            return _manager.getProperty(propertyConfig);
        }

        public V getPropertyValue<V>(K key, V defaultValue, TypeConverter<String, V> valueConverter,
                Func<V, V> valueFilter)
        {
            PropertyConfig<K, V> propertyConfig = createPropertyConfig(key, defaultValue, valueConverter,
                    valueFilter);
            return _manager.getPropertyValue(propertyConfig);
        }

        protected PropertyConfig<K, V> createPropertyConfig<V>(K key, V defaultValue,
                TypeConverter<String, V> valueConverter, Func<V, V> valueFilter)
        {
            return ConfigurationProperties.newConfigBuilder<K, V>().setKey(key)
                     .setDefaultValue(defaultValue).addValueConverter(valueConverter).setValueFilter(valueFilter).build();
        }
    }
}