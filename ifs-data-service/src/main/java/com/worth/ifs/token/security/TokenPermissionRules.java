package com.worth.ifs.token.security;

import com.worth.ifs.security.PermissionRule;
import com.worth.ifs.security.PermissionRules;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.worth.ifs.user.domain.UserRoleType.SYSTEM_REGISTRATION_USER;

/**
 * Rules around who has permissions to perform CRUD around Tokens
 */
@Component
@PermissionRules
public class TokenPermissionRules {

    @PermissionRule(value = "READ", description = "The System Registration user can read Tokens in order to verify them on behalf of a non-logged in user")
    public boolean systemRegistrationUserCanReadTokens(Token token, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "READ", description = "The System Registration user can read Tokens in order to verify them on behalf of a non-logged in user")
    public boolean systemRegistrationUserCanReadTokensOptional(Optional<Token> token, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    @PermissionRule(value = "DELETE", description = "The System Registration user can delete Tokens after verifying them on behalf of a non-logged in user")
    public boolean systemRegistrationUserCanDeleteTokens(Token token, UserResource user) {
        return isSystemRegistrationUser(user);
    }

    private boolean isSystemRegistrationUser(UserResource user) {
        return user.hasRole(SYSTEM_REGISTRATION_USER);
    }
}
