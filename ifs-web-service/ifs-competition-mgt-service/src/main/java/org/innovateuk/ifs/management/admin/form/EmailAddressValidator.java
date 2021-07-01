package org.innovateuk.ifs.management.admin.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;

import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddressValidator {

    private static boolean validateEmail(String emailAddress) {
        Pattern pattern = Pattern.compile(ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static class KtpPredicateProvider implements BiPredicateProvider<String, Boolean> {
        public BiPredicate<String, Boolean> predicate() {
            return (emailAddress, ktpRole) -> isValidEmailAddress(emailAddress, ktpRole);
        }

        private boolean isValidEmailAddress(String emailAddress, Boolean ktpRole) {
            if (ktpRole) {
                return validateEmail(emailAddress);
            }
            return true;
        }
    }

    public static class NonKtpPredicateProvider implements BiPredicateProvider<String, Boolean> {
        public BiPredicate<String, Boolean> predicate() {
            return (emailAddress, ktpRole) -> isValidEmailAddress(emailAddress, ktpRole);
        }

        private boolean isValidEmailAddress(String emailAddress, Boolean ktpRole) {
            if (!ktpRole) {
                return validateEmail(emailAddress);
            }
            return true;
        }
    }
}
