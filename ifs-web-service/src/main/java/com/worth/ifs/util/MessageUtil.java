package com.worth.ifs.util;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public class MessageUtil {

    public static String getFromMessageBundle(final MessageSource messageSource, final String key, final String defaultMsg, final Locale locale){
        return getFromMessageBundle(messageSource, key, defaultMsg, null, locale);
    }

    public static String getFromMessageBundle(final MessageSource messageSource, final String key, final String defaultMsg, final Object[] arguments, final Locale locale){
        String msg;
        try {
            msg = messageSource.getMessage(key, arguments, locale);
            if(msg == null){
                msg = defaultMsg;
            }
        } catch(NoSuchMessageException nsme){
            msg = defaultMsg;
        }
        return msg;
    }
}
