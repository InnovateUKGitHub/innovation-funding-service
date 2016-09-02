package com.worth.ifs.controller;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * Helper utility class to look up Errors based upon error keys.  These would typically be used when we can't rely on
 * Thymeleaf templates doing the lookups for us, like in Ajax calls whose responses do not return templates but instead
 * lists of errors
 */
public class ErrorLookupHelper {

    public static List<String> lookupErrorMessageResourceBundleEntries(MessageSource messageSource, ErrorHolder errorHolder) {
        return simpleMap(errorHolder.getErrors(), e -> lookupErrorMessageResourceBundleEntry(messageSource, e));
    }

    public static String lookupErrorMessageResourceBundleEntry(MessageSource messageSource, Error e) {
        return lookupErrorMessageResourceBundleEntry(messageSource, e.getErrorKey(), e.getArguments());
    }

    public static String lookupErrorMessageResourceBundleEntry(MessageSource messageSource, String errorKey, List<Object> arguments) {
        return messageSource.getMessage(errorKey, arguments.toArray(), Locale.UK);
    }
}
