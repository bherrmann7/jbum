package com.thoughtworks.xstream.converters.extended;

import java.nio.charset.Charset;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.nio.charset.Carset to a string.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CharsetConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return Charset.class.isAssignableFrom(type);
    }

    public String toString(Object obj) {
        return obj == null ? null : ((Charset)obj).name();
    }


    public Object fromString(String str) {
        return Charset.forName(str);
    }
}