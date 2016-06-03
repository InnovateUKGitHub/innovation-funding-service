package com.worth.ifs.token.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.worth.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static com.worth.ifs.token.resource.TokenType.RESET_PASSWORD;
import static com.worth.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static java.time.LocalDateTime.now;
import static java.util.Optional.of;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TokenServiceImplTest extends BaseUnitTestMocksTest {

    private static final int EMAIL_TOKEN_VALIDITY_MINS = 5;

    @InjectMocks
    private final TokenService tokenService = new TokenServiceImpl();

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(tokenService, "emailTokenValidityMins", EMAIL_TOKEN_VALIDITY_MINS);
    }

    @Test
    public void test_getEmailToken() throws Exception {
        final String hash = "ffce0dbb58bd7780cba3a6c64a666d7d3481604722c55400fd5356195407144259de4c9ec75f8edb";

        final Token expected = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash, recentCreatedDate(), JsonNodeFactory.instance.objectNode());
        when(tokenRepositoryMock.findByHashAndTypeAndClassName(hash, VERIFY_EMAIL_ADDRESS, User.class.getName())).thenReturn(of(expected));

        final Token token = tokenService.getEmailToken(hash).getSuccessObject();
        assertEquals(expected, token);

        verify(tokenRepositoryMock, only()).findByHashAndTypeAndClassName(hash, VERIFY_EMAIL_ADDRESS, User.class.getName());
    }

    @Test
    public void test_getEmailToken_expired() throws Exception {
        final String hash = "ffce0dbb58bd7780cba3a6c64a666d7d3481604722c55400fd5356195407144259de4c9ec75f8edb";

        final Token expected = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, hash, expiredCreatedDate(), JsonNodeFactory.instance.objectNode());
        when(tokenRepositoryMock.findByHashAndTypeAndClassName(hash, VERIFY_EMAIL_ADDRESS, User.class.getName())).thenReturn(of(expected));

        final ServiceResult<Token> result = tokenService.getEmailToken(hash);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED));
    }

    @Test
    public void test_getPasswordResetToken() throws Exception {
        final String hash = "ffce0dbb58bd7780cba3a6c64a666d7d3481604722c55400fd5356195407144259de4c9ec75f8edb";

        final Token expected = new Token(RESET_PASSWORD, User.class.getName(), 1L, hash, recentCreatedDate(), JsonNodeFactory.instance.objectNode());
        when(tokenRepositoryMock.findByHashAndTypeAndClassName(hash, RESET_PASSWORD, User.class.getName())).thenReturn(of(expected));

        final Token token = tokenService.getPasswordResetToken(hash).getSuccessObject();
        assertEquals(expected, token);

        verify(tokenRepositoryMock, only()).findByHashAndTypeAndClassName(hash, RESET_PASSWORD, User.class.getName());
    }

    @Test
    public void test_removeToken() throws Exception {
        final Token token = new Token(RESET_PASSWORD, User.class.getName(), 1L, "ffce0dbb58bd7780cba3a6c64a666d7d3481604722c55400fd5356195407144259de4c9ec75f8edb", now(), JsonNodeFactory.instance.objectNode());

        tokenService.removeToken(token);

        verify(tokenRepositoryMock, only()).delete(token);
    }

    @Test
    public void test_handleExtraAttributes() throws Exception {
        final Long competitionId = 999L;
        final Long userId = 888L;
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), userId, "ffce0dbb58bd7780cba3a6c64a666d7d3481604722c55400fd5356195407144259de4c9ec75f8edb", now(), JsonNodeFactory.instance.objectNode().put("competitionId", competitionId));

        tokenService.handleExtraAttributes(token);

        verify(applicationServiceMock, only()).createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, EMPTY);
    }

    @Test
    public void test_handleExtraAttributes_empty() throws Exception {
        final Token token = new Token(VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L, "ffce0dbb58bd7780cba3a6c64a666d7d3481604722c55400fd5356195407144259de4c9ec75f8edb", now(), JsonNodeFactory.instance.objectNode());

        tokenService.handleExtraAttributes(token);

        verify(applicationServiceMock, never()).createApplicationByApplicationNameForUserIdAndCompetitionId(isA(Long.class), isA(Long.class), isA(String.class));
    }

    /**
     * Create a date to be used in a token such that it expires now.
     *
     * @return
     */
    private LocalDateTime expiredCreatedDate() {
        return now().minusMinutes(EMAIL_TOKEN_VALIDITY_MINS);
    }

    /**
     * Create a date to be used in a token such that it has one minute remaining until expiry.
     *
     * @return
     */
    private LocalDateTime recentCreatedDate() {
        return now().minusMinutes(EMAIL_TOKEN_VALIDITY_MINS - 1);
    }
}