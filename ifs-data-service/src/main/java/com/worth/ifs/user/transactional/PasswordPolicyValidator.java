package com.worth.ifs.user.transactional;

import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.CASE_INSENSITIVE;

/**
 * A component that is able to enforce certain additional rules for the Innovate UK password policy
 * that are not handled in the Identity Provider (Shibboleth REST API)
 */
@Component
public class PasswordPolicyValidator {

    private class ExclusionRule {

        private String description;
        private Function<UserResource, String> exclusionSupplier;

        public ExclusionRule(String description, Function<UserResource, String> exclusionSupplier) {
            this.description = description;
            this.exclusionSupplier = exclusionSupplier;
        }
    }

    private interface ExclusionRuleMutator extends BiFunction<ExclusionRule, UserResource, List<Pattern>> {

    }

    private ExclusionRule containsFirstName = new ExclusionRule("PASSWORD_MUST_NOT_CONTAIN_FIRST_NAME", user -> user.getFirstName());
    private ExclusionRule containsLastName = new ExclusionRule("PASSWORD_MUST_NOT_CONTAIN_LAST_NAME", user -> user.getLastName());

    private ExclusionRuleMutator numbersForLettersMutator = new ExclusionRuleMutator() {

        @Override
        public List<Pattern> apply(ExclusionRule exclusionRule, UserResource user) {

            String currentExcludedWord = exclusionRule.exclusionSupplier.apply(user);
            Pattern currentExcludedWordCaseInsensitive = Pattern.compile(format("(%s)", currentExcludedWord), CASE_INSENSITIVE);
            return currentExcludedWord;
        }
    }
}
