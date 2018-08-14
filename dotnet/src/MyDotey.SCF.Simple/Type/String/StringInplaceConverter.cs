using System;

namespace MyDotey.SCF.Type.String
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringInplaceConverter : InplaceConverter<System.String>
    {
        public static readonly StringInplaceConverter DEFAULT = new StringInplaceConverter();
    }
}