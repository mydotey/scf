using System;

namespace MyDotey.SCF.Type.String
{
    /**
    * @author koqizhao
    *
    * May 21, 2018
    */
    public class StringToBooleanConverter : StringConverter<bool?>
    {
        public static readonly StringToBooleanConverter DEFAULT = new StringToBooleanConverter();

        public override bool? Convert(string source) {
            if (string.IsNullOrWhiteSpace(source))
                return null;

            return bool.Parse(source);
        }
    }
}