package com.worth.ifs.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

/**
 * <p>
 * Service to generate new CSRF tokens, and validate them. This service makes use of the Spring Security Crypto module to encrypt the token using 256 bit AES encryption. CSRF tokens consist of:
 * <ul>
 *     <li>A random one-off string (nonce) used to make the token unpredictable.</li>
 *     <li>The UID of the currently authenticated user.
 *     <li>Timestamp which is checked against when validating the token</li>
 * </ul>
 * </p>
 * <p>
 * CSRF Protected HTTP requests (e.g. requests using the POST method) are expected to contain the CSRF token in either the `X-CSRF-TOKEN` header or the `_csrf` parameter.
 * The token, after being decrypted, is expected to contain the UID of the currently authenticated user and a valid timestamp.
 * </p>
 * <p>
 * As it stands at version 4.0.4 of Spring Security, the JCE Unlimited Strength Jurisdiction Policy Files are required to be installed to use the Spring Crypto module.
 * These provide policy files which contain no restrictions on cryptographic strengths.
 * In a future version of Spring Security - possibly 4.1.0, issue SEC-2778 will hopefully have been dealt with, allowing Encryptors to be used without needing the JCE Unlimited Strength Jurisdiction Policy Files.
 * @see <a href="https://github.com/spring-projects/spring-security/issues/2917">SEC-2778</a>
 * </p>
 */
@Service
@Configurable
class CsrfTokenService {

    private static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    private static final String CSRF_PARAMETER_NAME = "_csrf";

    private TextEncryptor encryptor;
    private String encryptionPassword;
    private String encryptionSalt;
    private int tokenValidityMins;

    @PostConstruct
    void init() {
        this.encryptor = Encryptors.text(encryptionPassword, encryptionSalt);
    }

    /**
     * Generates a {@link CsrfToken}.
     * {@link CsrfToken} contains the encrypted token and is returned here rather than {@link CsrfUidToken} because this is what is expected by org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor#getExtraHiddenFields(javax.servlet.http.HttpServletRequest).
     *
     * @return {@link CsrfToken}
     */
    CsrfToken generateToken() {
        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAMETER_NAME, encryptToken());
    }

    /**
     * Validates the expected {@link CsrfUidToken} from the {@link HttpServletRequest}
     * @param request the {@link HttpServletRequest} to use
     * @throws CsrfException if the token is not found or not valid
     */
    void validateToken(final HttpServletRequest request) throws CsrfException {
        final CsrfUidToken csrfUidToken = decryptToken(request);

        if (!isUIdValid(csrfUidToken.getuId())) {
            throw new CsrfException("User id not recognised while validating CSRF token.");
        }

        if (!isTimestampValid(csrfUidToken.getTimestamp())) {
            throw new CsrfException("Timestamp not valid while validating CSRF token.");
        }
    }

    private CsrfUidToken createToken() {
        return new CsrfUidToken(randomUUID().toString(), getUserId(), Instant.now());
    }

    private CsrfUidToken decryptToken(final HttpServletRequest request) throws CsrfException {
        final String token = getTokenFromRequest(request);

        String decryptedToken;
        try {
            decryptedToken = encryptor.decrypt(token);
        } catch (final Exception e) {
            throw new CsrfException("CSRF Token could not be decrypted");
        }

        return CsrfUidToken.parse(decryptedToken);
    }

    private String encryptToken() {
        return encryptor.encrypt(createToken().getToken());
    }

    private String getTokenFromRequest(final HttpServletRequest request) throws CsrfException {
        final Optional<String> headerToken = ofNullable(request.getHeader(CSRF_HEADER_NAME));
        final Optional<String> token = ofNullable(headerToken.orElse(request.getParameter(CSRF_PARAMETER_NAME)));
        return token.orElseThrow(() -> new CsrfException(format("CSRF Token not found. Expected token in header with name '%s' or request parameter with name '%s'.", CSRF_HEADER_NAME, CSRF_PARAMETER_NAME)));
    }

    private String getUserId() {
        return getAuthentication().map((authentication) ->
                authentication instanceof AnonymousAuthenticationFilter ? "ANONYMOUS" : authentication.getCredentials().toString()
        ).orElse("ANONYMOUS");
    }

    private Optional<Authentication> getAuthentication() {
        return ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    private boolean isUIdValid(final String uId) {
        return getUserId().equals(uId);
    }

    private boolean isTimestampValid(final Instant timestamp) {
        return ChronoUnit.MINUTES.between(timestamp, Instant.now()) < tokenValidityMins;
    }

    @Value("${ifs.web.security.csrf.encryption.password}")
    public void setEncryptionPassword(final String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
    }

    @Value("${ifs.web.security.csrf.encryption.salt}")
    public void setEncryptionSalt(final String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
    }

    @Value("${ifs.web.security.csrf.token.validity.mins}")
    public void setTokenValidityMins(final int tokenValidityMins) {
        this.tokenValidityMins = tokenValidityMins;
    }

    static final class CsrfUidToken {
        private final String nonce;
        private final String uId;
        private final Instant timestamp;

        CsrfUidToken(String nonce, String uId, Instant timestamp) {
            this.nonce = nonce;
            this.uId = uId;
            this.timestamp = timestamp;
        }

        String getNonce() {
            return nonce;
        }

        String getuId() {
            return uId;
        }

        Instant getTimestamp() {
            return timestamp;
        }

        String getToken() {
            return Arrays.asList(getNonce(), getuId(), getTimestamp().toString()).stream().collect(Collectors.joining("_"));
        }

        static CsrfUidToken parse(final String token) throws CsrfException {
            if (StringUtils.isBlank(token)) {
                throw new CsrfException("Could not parse blank CSRF token.");
            }

            final String[] elems = token.split("_");
            if (elems.length != 3) {
                throw new CsrfException(format("Could not parse CSRF token into constituent elements '%s'.", token));
            }

            return new CsrfUidToken(elems[0], elems[1], Instant.parse(elems[2]));
        }

        @Override
        public String toString() {
            return "CsrfUidToken{" +
                    "nonce='" + nonce + '\'' +
                    ", uId='" + uId + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
