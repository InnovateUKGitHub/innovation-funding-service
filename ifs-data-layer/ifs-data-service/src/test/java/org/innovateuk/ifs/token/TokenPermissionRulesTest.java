package org.innovateuk.ifs.token;

import org.innovateuk.ifs.BasePermissionRulesTest;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.security.TokenPermissionRules;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests around the functionality of the permission rules surrounding Tokens
 */
public class TokenPermissionRulesTest extends BasePermissionRulesTest<TokenPermissionRules> {

    @Test
    public void testSystemRegistrationUserCanReadTokens() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanReadTokens(new Token(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanReadTokens(new Token(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanUseTokensToResetPaswords() {
        allGlobalRoleUsers.forEach(user -> {
            if (user.equals(systemRegistrationUser())) {
                assertTrue(rules.systemRegistrationUserCanUseTokensToResetPaswords(new Token(), user));
            } else {
                assertFalse(rules.systemRegistrationUserCanUseTokensToResetPaswords(new Token(), user));
            }
        });
    }

    @Test
    public void testSystemRegistrationUserCanDeleteTokens() {
        allGlobalRoleUsers.forEach(user -> {
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
