package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a java.sql.Timestamp to text.
 *
 * @author Joe Walnes
 */
public class SqlTimestampConverter extends AbstractSingleValueConverter {

    public boolean canConvert(Class type) {
        return type.equals(Timestamp.class);
    }

    public Object fromString(String str) {
        return Timestamp.valueOf(str);
    }

}
