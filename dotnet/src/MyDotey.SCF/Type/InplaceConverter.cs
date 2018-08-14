using System;

namespace MyDotey.SCF.Type
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     * 
     * source type and target type is the same type, convert do nothing
     */
    public class InplaceConverter<V> : AbstractTypeConverter<V, V>
    {
        public override V Convert(V s)
        {
            return s;
        }
    }
}