package org.innovateuk.ifs.token.security;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.token.resource.TokenType.RESET_PASSWORD;
import static org.innovateuk.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static java.time.ZonedDateTime.now;
import static org.mockito.Mockito.*;

public class TokenServiceSecurityTest extends BaseServiceSecurityTest<TokenService> {

    private TokenPermissionRules tokenRules;

    @Override
    protected Class<? extends TokenService> getClassUnderTest() {
        return TestTokenService.class;
    }

    @Before
    public void lookupPermissionRules() {
        tokenRules = getMockPermissionRulesBean(TokenPermissionRules.class);
    }

    @Test
    public void getEmailToken() throws Exception {
        assertAccessDenied(
                () -> classUnderTest.getPasswordResetToken("hash"),
                () -> {
                    verify(tokenRules).systemRegistrationUserCanReadTokens(isA(Token.class), isA(UserResource.class));
                    verifyNoMoreInteractions(tokenRules);
                });
    }

    @Test
    public void getPasswordResetToken() throws Exception {
        assertAccessDenied(
                () -> classUnderTest.getEmailToken("hash"),
                () -> {
                    verify(tokenRules).systemRegistrationUserCanReadTokens(isA(Token.class), isA(UserResource.class));
                    verifyNoMoreInteractions(tokenRules);
                });
    }

    @Test
    public void removeToken() throws Exception {
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, "hash", now(), JsonNodeFactory.instance.objectNode());
        assertAccessDenied(
                () -> classUnderTest.removeToken(token),
                () -> {
                    verify(tokenRules).systemRegistrationUserCanDeleteTokens(token, getLoggedInUser());
                    verifyNoMoreInteractions(tokenRules);
                });
    }

    @Test
    public void handleExtraAttributes() throws Exception {
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, "hash", now(), JsonNodeFactory.instance.objectNode());
        assertAccessDenied(
                () -> classUnderTest.handleExtraAttributes(token),
                () -> {
                    verify(tokenRules, times(1)).systemRegistrationUserCanReadTokens(token, getLoggedInUser());
                    verifyNoMoreInteractions(tokenRules);
                });
    }

    public static class TestTokenService implements TokenService {

        @Override
        public ServiceResult<Token> getEmailToken(final String hash) {
            return serviceSuccess(new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, "hash", now(), JsonNodeFactory.instance.objectNode()));
        }

        @Override
        public ServiceResult<Token> getPasswordResetToken(final String hash) {
            return serviceSuccess(new Token(RESET_PASSWORD, User.class.getName(), 1L, "hash", now(), JsonNodeFactory.instance.objectNode()));
        }

        @Override
        public void removeToken(final Token token) {

        }

        @Override
        public void handleExtraAttributes(final Token token) {

        }
    }

}
