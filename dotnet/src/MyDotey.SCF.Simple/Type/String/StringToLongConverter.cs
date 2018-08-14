using System;

namespace MyDotey.SCF.Type.String
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringToLongConverter : StringConverter<long?>
    {
        public static readonly StringToLongConverter DEFAULT = new StringToLongConverter();

        public override long? Convert(string source)
        {
            if (string.IsNullOrWhiteSpace(source))
                return null;

            return long.Parse(source);
        }
    }
}