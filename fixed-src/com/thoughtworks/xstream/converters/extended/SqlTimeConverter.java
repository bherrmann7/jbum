package com.thoughtworks.xstream.converters.extended;

import java.sql.Time;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.sql.Time to text. Warning: Any granularity smaller than seconds is lost.
 *
 * @author Jose A. Illescas
 */
public class SqlTimeConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Time.class);
    }

    public Object fromString(String str) {
        return Time.valueOf(str);
    }

}
