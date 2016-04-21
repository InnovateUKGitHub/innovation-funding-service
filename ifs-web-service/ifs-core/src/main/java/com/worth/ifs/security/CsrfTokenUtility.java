package com.worth.ifs.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
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
import static java.util.UUID.randomUUID;

/**
 * TODO
 * <p>
 */
@Service
@Configurable
class CsrfTokenUtility {

    private static final String CSRF_HEADER_NAME = "X-CSRF-TOKEN";
    private static final String CSRF_PARAMETER_NAME = "_csrf";
    private static final long TOKEN_VALIDITY_MINS = 30;

    private TextEncryptor encryptor;
    private String encryptionPassword;
    private String encryptionSalt;

    @PostConstruct
    void init() {
        this.encryptor = Encryptors.text(encryptionPassword, encryptionSalt);
    }

    /**
     * Generates a {@link CsrfToken} based on the encrypted user id for the currently authenticated user.
     *
     * @return {@link CsrfToken}
     */
    CsrfToken generateToken() {
        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAMETER_NAME, encryptToken());
    }

    /**
     * Validates the expected {@link CsrfToken} from the {@link HttpServletRequest}
     *
     * @param request the {@link HttpServletRequest} to use
     * @return true if the token is valid for the currently authenticated user, otherwise false
     */
    boolean validateToken(final HttpServletRequest request) throws CsrfException {
        final CsrfUidToken csrfUidToken = decryptToken(request);
        if (isUIdValid(csrfUidToken.getuId())) {
            if (isTimestampValid(csrfUidToken.getTimestamp())) {
                return true;
            }
            throw new CsrfException("Timestamp not valid while validating CSRF token.");
        }
        throw new CsrfException("User id not recognised while validating CSRF token.");
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
        final Optional<String> token = Optional.ofNullable(Optional.ofNullable(request.getHeader(CSRF_HEADER_NAME)).orElse(request.getParameter(CSRF_PARAMETER_NAME)));
        return token.orElseThrow(() -> new CsrfException(format("CSRF Token not found. Expected token in header with name '%s' or request parameter with name '%s'.", CSRF_HEADER_NAME, CSRF_PARAMETER_NAME)));
    }

    private String getUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getCredentials().toString();
    }

    private boolean isUIdValid(final String uId) {
        final String expected = getUserId();
        return expected != null && expected.equals(uId);
    }

    private boolean isTimestampValid(final Instant timestamp) {
        return ChronoUnit.MINUTES.between(timestamp, Instant.now()) < TOKEN_VALIDITY_MINS;
    }

    @Value("${ifs.web.security.csrf.encryption.password}")
    public void setEncryptionPassword(String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
    }

    @Value("${ifs.web.security.csrf.encryption.salt}")
    public void setEncryptionSalt(String encryptionSalt) {
        this.encryptionSalt = encryptionSalt;
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
            return Arrays.asList(randomUUID().toString(), getuId(), getTimestamp().toString()).stream().collect(Collectors.joining("_"));
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
