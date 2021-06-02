package org.innovateuk.ifs.config.validation;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Some time ago the hibernate validator switched to using {min} style syntax as opposed to {0} indexed based
 * following a CVE.
 *
 * The ui uses the old style in thymeleaf and js in abundance, so this is very much a workaround.
 *
 * Also workaround the ' escape issues
 *
 */
public class DeprecatedStyleMessageInterpolator extends ResourceBundleMessageInterpolator {

    private final static Logger LOG = LoggerFactory.getLogger(DeprecatedStyleMessageInterpolator.class);

    private final static String MSG_KEY = "message";
    private final static String GROUPS_KEY = "groups";
    private final static String PAYLOAD_KEY = "payload";
    private final static String MAX_KEY = "max";
    private final static String TOO_MANY_KEY = "too.many";
    private final static String MIN_KEY = "min";
    private final static String TOO_FEW_KEY = "too.few";

    @Override
    public String interpolate(String message, Context context) {
        String interpolatedMessage = super.interpolate(message, context);
        return interpolatedMessage.replaceAll("''", "'");
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        String interpolatedMessage = super.interpolate(message, context, locale);
        return interpolatedMessage.replaceAll("''", "'");
    }

    @Override
    public String interpolate(Context context, Locale locale, String term) {
        String message = (String) context.getConstraintDescriptor().getAttributes().get(MSG_KEY);
        message = message.toLowerCase();
        Map<String, Object> relevantAttributes = extractRelevantAttributes(context);
        if (relevantAttributes.size() == 1) {
            // if there is only one attribute then its easy
            return String.valueOf(relevantAttributes.values().toArray()[0]);
        }
        if (relevantAttributes.size() == 2) {
            // case min and max
            if ((message.contains(MAX_KEY) || message.contains(TOO_MANY_KEY)) && relevantAttributes.containsKey(MAX_KEY)) {
                return String.valueOf(relevantAttributes.get(MAX_KEY));
            }
            if ((message.contains(MIN_KEY) || message.contains(TOO_FEW_KEY)) && relevantAttributes.containsKey(MIN_KEY)) {
                return String.valueOf(relevantAttributes.get(MIN_KEY));
            }
        }
        LOG.error("ERROR: Unable to interpolate message: {} {}", message, relevantAttributes);
        return "ERROR: Unable to interpolate message: " + message + " " + relevantAttributes;
    }

    private Map<String, Object> extractRelevantAttributes(Context context) {
        Map<String, Object> mapCopy = new HashMap<>(context.getConstraintDescriptor().getAttributes());
        mapCopy.remove(GROUPS_KEY);
        mapCopy.remove(MSG_KEY);
        mapCopy.remove(PAYLOAD_KEY);
        return mapCopy;
    }

}
