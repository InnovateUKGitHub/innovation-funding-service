package org.innovateuk.ifs.controller;

import java.beans.PropertyEditorSupport;

/**
 * Class to support use of case-insensitive enums as method parameters
 */
public class CaseInsensitiveConverter<T extends Enum<T>> extends PropertyEditorSupport {

    private final Class<T> typeParameterClass;

    public CaseInsensitiveConverter(Class<T> typeParameterClass) {
        super();
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        String upper = text.toUpperCase();
        T value = T.valueOf(typeParameterClass, upper);
        setValue(value);
    }
}