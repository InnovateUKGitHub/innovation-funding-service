package org.innovateuk.ifs.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

@Slf4j
public final class MessageUtil {

	private MessageUtil() {}
	
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
        	log.debug(nsme.getMessage(), nsme);
            msg = defaultMsg;
        }
        return msg;
    }
}
