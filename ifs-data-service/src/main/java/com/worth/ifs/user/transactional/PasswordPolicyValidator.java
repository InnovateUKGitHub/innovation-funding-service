package com.worth.ifs.user.transactional;

import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

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

    private interface ExclusionRuleMutator extends Function<ExclusionRule, List<ExclusionRule>> {

    }

    private ExclusionRule containsFirstName = new ExclusionRule("PASSWORD_MUST_NOT_CONTAIN_FIRST_NAME", user -> user.getFirstName());
    private ExclusionRule containsLastName = new ExclusionRule("PASSWORD_MUST_NOT_CONTAIN_LAST_NAME", user -> user.getLastName());

    private ExclusionRuleMutator numbersForLettersMutator = new ExclusionRuleMutator() {

        @Override
        public List<ExclusionRule> apply(ExclusionRule exclusionRule) {
            return exclusionRule.description;
        }
    }
}
