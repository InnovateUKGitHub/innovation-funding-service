package com.worth.ifs.security;

import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
import com.worth.ifs.security.CsrfTokenService.CsrfUidToken;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.*;

public class CsrfTokenServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";
    private static final String ENCRYPTION_PASSWORD = randomUUID().toString();
    private static final String ENCRYPTION_SALT = KeyGenerators.string().generateKey();
    private static final int TOKEN_VALIDITY_MINS = 15;

    private TextEncryptor encryptor;
    private CsrfTokenService tokenUtility;

    @Before
    public void setUp() throws Exception {
        final UserResource user = newUserResource().withId(-1L).withUID(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));

        encryptor = Encryptors.text(ENCRYPTION_PASSWORD, ENCRYPTION_SALT);
        tokenUtility = setUpTokenUtility();
    }

    @Test
    public void test_generateToken() throws Exception {
        final CsrfToken token = tokenUtility.generateToken();
        assertEquals("X-CSRF-TOKEN", token.getHeaderName());
        assertEquals("_csrf", token.getParameterName());
        final String decrypted = encryptor.decrypt(token.getToken());
        final CsrfUidToken parsed = CsrfUidToken.parse(decrypted);
        assertEquals(UID, parsed.getuId());
    }

    @Test
    public void test_generateToken_anonymous() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        final CsrfToken token = tokenUtility.generateToken();
        final String decrypted = encryptor.decrypt(token.getToken());
        final CsrfUidToken parsed = CsrfUidToken.parse(decrypted);
        assertEquals("ANONYMOUS", parsed.getuId());
    }

    @Test
    public void test_validateToken_expired() throws Exception {
        final Instant oldTimestamp = Instant.now().minus(TOKEN_VALIDITY_MINS+1, ChronoUnit.MINUTES);
        thrown.expect(CsrfException.class);
        thrown.expectMessage("Timestamp not valid while validating CSRF token.");
        tokenUtility.validateToken(mockRequestWithHeaderValue(token(UID, oldTimestamp)));
    }

    @Test
    public void test_validateToken_wrong_uid() throws Exception {
        final String wrongUid = randomUUID().toString();
        thrown.expect(CsrfException.class);
        thrown.expectMessage("User id not recognised while validating CSRF token");
        tokenUtility.validateToken(mockRequestWithHeaderValue(token(wrongUid, recentTimestamp())));
    }

    @Test
    public void test_validateToken_anonymous() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        tokenUtility.validateToken(mockRequestWithHeaderValue(token("ANONYMOUS", recentTimestamp())));
    }

    @Test
    public void test_validateToken_requestBody_absent() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage(format("CSRF Token not found. Expected token in header with name '%s' or request parameter with name '%s'.", "X-CSRF-TOKEN", "_csrf"));
        tokenUtility.validateToken(mockRequest());
    }

    @Test
    public void test_validateToken_requestHeader_present() throws Exception {
        tokenUtility.validateToken(mockRequestWithHeaderValue(validToken()));
    }

    @Test
    public void test_validateToken_requestHeader_malformed() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage("CSRF Token could not be decrypted");
        tokenUtility.validateToken(mockRequestWithHeaderValue(malformedToken()));
    }

    @Test
    public void test_validateToken_requestHeader_empty() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage("CSRF Token could not be decrypted");
        tokenUtility.validateToken(mockRequestWithHeaderValue(EMPTY));
    }

    @Test
    public void test_validateToken_requestBody_present() throws Exception {
        tokenUtility.validateToken(mockRequestWithParameterValue(validToken()));
    }

    @Test
    public void test_validateToken_requestBody_malformed() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage("CSRF Token could not be decrypted");
        tokenUtility.validateToken(mockRequestWithParameterValue(malformedToken()));
    }

    @Test
    public void test_validateToken_requestBody_empty() throws Exception {
        thrown.expect(CsrfException.class);
        thrown.expectMessage("CSRF Token could not be decrypted");
        tokenUtility.validateToken(mockRequestWithParameterValue(EMPTY));
    }

    private CsrfTokenService setUpTokenUtility() {
        final CsrfTokenService tokenUtility = new CsrfTokenService();
        tokenUtility.setEncryptionPassword(ENCRYPTION_PASSWORD);
        tokenUtility.setEncryptionSalt(ENCRYPTION_SALT);
        tokenUtility.setTokenValidityMins(TOKEN_VALIDITY_MINS);
        tokenUtility.init();
        return tokenUtility;
    }

    private String encrypt(final CsrfUidToken token) {
        return encryptor.encrypt(token.getToken());
    }

    private Instant recentTimestamp() {
        // recent timestamp is one minute before it expires
        return Instant.now().minus(TOKEN_VALIDITY_MINS-1, ChronoUnit.MINUTES);
    }

    private String validToken() {
        return token(UID, recentTimestamp());
    }

    private String malformedToken() {
        return validToken().substring(1);
    }

    private String token(final String uid, final Instant timestamp) {
        return encrypt(new CsrfUidToken(randomUUID().toString(), uid, timestamp));
    }

    private HttpServletRequest mockRequestWithHeaderValue(final String value) {
        final MockHttpServletRequest request = mockRequest();
        request.addHeader("X-CSRF-TOKEN", value);
        return request;
    }

    private HttpServletRequest mockRequestWithParameterValue(final String value) {
        final MockHttpServletRequest request = mockRequest();
        request.addParameter("_csrf", value);
        return request;
    }

    private MockHttpServletRequest mockRequest() {
        return new MockHttpServletRequest();
    }

}