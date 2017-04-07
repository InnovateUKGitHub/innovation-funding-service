package org.innovateuk.ifs.token.transactional;

import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.token.resource.TokenType.RESET_PASSWORD;
import static org.innovateuk.ifs.token.resource.TokenType.VERIFY_EMAIL_ADDRESS;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository repository;
    @Autowired
    private ApplicationService applicationService;

    @Value("${ifs.data.service.token.email.validity.mins}")
    private int emailTokenValidityMins;

    @Override
    public ServiceResult<Token> getEmailToken(final String hash) {
        return find(repository.findByHashAndTypeAndClassName(hash, VERIFY_EMAIL_ADDRESS, User.class.getName()), new Error(USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND)).andOnSuccess(
                token -> isTokenValid(token) ? serviceSuccess(token) : serviceFailure(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED));
    }

    @Override
    public ServiceResult<Token> getPasswordResetToken(final String hash) {
        return find(repository.findByHashAndTypeAndClassName(hash, RESET_PASSWORD, User.class.getName()), notFoundError(Token.class, hash));
    }

    @Override
    public void removeToken(Token token) {
        repository.delete(token);
    }

    /**
     * if there are extra attributes in the token, then maybe we need to create a new application, or add the user to a application.
     */
    @Override
    public void handleExtraAttributes(Token token) {
        JsonNode extraInfo = token.getExtraInfo();
        if (User.class.getName().equals(token.getClassName()) && extraInfo.has("competitionId")) {
            Long competitionId = extraInfo.get("competitionId").asLong();
            if (competitionId != null && competitionId != 0L) {
                applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId("", competitionId, token.getClassPk());
            }
        }
    }

    private boolean isTokenValid(final Token token) {
        // Prefer the updated time over the created time in case the token has been refreshed
        final ZonedDateTime tokenDateTime = token.getUpdated() == null ? token.getCreated() : token.getUpdated();
        return ChronoUnit.MINUTES.between(tokenDateTime, ZonedDateTime.now()) < emailTokenValidityMins;
    }
}
