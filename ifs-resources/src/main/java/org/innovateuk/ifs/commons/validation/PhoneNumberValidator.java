package org.innovateuk.ifs.commons.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This regex matches a null entry or a valid entry, as we don't want to perform a telephone check on a null value,
 * it then discounts permitted non numeric characters from the 8-20 count, checks out numerics and
 * permitted characters (+()-), and finally checks that the string is between 8-20.
 */
public class PhoneNumberValidator {

    Pattern pattern;
    private Matcher matcher;
    public static final String VALID_PHONE_NUMBER = "^[\\\\)\\\\(\\\\+\\s-]*(?:\\d[\\\\)\\\\(\\\\+\\s-]*){8,20}$";

    public PhoneNumberValidator() {
        pattern = Pattern.compile(VALID_PHONE_NUMBER);
    }

    public boolean validate(final String phonenumber) {
        matcher = pattern.matcher(phonenumber);
        return matcher.matches();
    }
}
