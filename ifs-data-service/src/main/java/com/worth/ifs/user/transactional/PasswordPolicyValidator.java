package com.worth.ifs.user.transactional;

import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

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

        public ExclusionRule(String description) {
            this.description = description;
        }
    }

    private class ExclusionRuleMutator {

    }
}
