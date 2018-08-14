using System;
using sType = System.Type;

namespace MyDotey.SCF.Type
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     * 
     * convert a value from source type to target type
     */
    public interface TypeConverter<S, T> : TypeConverter
    {
        T Convert(S source);
    }

    public interface TypeConverter
    {
        sType SourceType { get; }
        sType TargetType { get; }

        object Convert(object source);
    }

}