package com.worth.ifs.token;

import com.worth.ifs.BasePermissionRulesTest;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.security.TokenPermissionRules;
import org.junit.Test;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests around the functionality of the permission rules surrounding Tokens
 */
public class TokenPermissionRulesTest extends BasePermissionRulesTest<TokenPermissionRules> {

    @Test
    public void testSystemRegistrationUserCanReadTokens() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanReadTokens(new Token(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanReadTokens(new Token(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanDeleteTokens() {
        allRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanDeleteTokens(new Token(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanDeleteTokens(new Token(), user));
            }
        });
    }

    @Override
    protected TokenPermissionRules supplyPermissionRulesUnderTest() {
        return new TokenPermissionRules();
    }
}
