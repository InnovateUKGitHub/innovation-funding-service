package org.innovateuk.ifs.management.admin.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.commons.validation.predicate.BiPredicateProvider;

import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddressValidator {

    public static Pattern pattern = Pattern.compile(ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX);

    public static class KtpPredicateProvider implements BiPredicateProvider<String, Boolean> {
        public BiPredicate<String, Boolean> predicate() {
            return (emailAddress, ktpRole) -> isValidEmailAddress(emailAddress, ktpRole);
        }

        private boolean isValidEmailAddress(String emailAddress, Boolean ktpRole) {
            if (ktpRole) {
                Matcher matcher = pattern.matcher(emailAddress);
                return matcher.matches();
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
                Matcher matcher = pattern.matcher(emailAddress);
                return matcher.matches();
            }
            return true;
        }
    }
}
