package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public final class MessageUtil {

	private MessageUtil() {}
	
	private static final Log LOG = LogFactory.getLog(MessageUtil.class);
	
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
        	LOG.debug(nsme);
            msg = defaultMsg;
        }
        return msg;
    }
}
