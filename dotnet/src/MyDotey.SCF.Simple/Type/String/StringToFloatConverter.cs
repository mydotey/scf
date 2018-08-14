using System;

namespace MyDotey.SCF.Type.String
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringToFloatConverter : StringConverter<float?>
    {
        public static readonly StringToFloatConverter DEFAULT = new StringToFloatConverter();

        public override float? Convert(string source)
        {
            if (string.IsNullOrWhiteSpace(source))
                return null;

            return float.Parse(source);
        }
    }
}