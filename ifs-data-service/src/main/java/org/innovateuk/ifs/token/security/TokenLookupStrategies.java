package org.innovateuk.ifs.token.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Mechanism for looking up Tokens for Spring Security checks
 */
@Component
@PermissionEntityLookupStrategies
public class TokenLookupStrategies {

    @Autowired
    private TokenRepository tokenRepository;

    @PermissionEntityLookupStrategy
    public Token getTokenByHash(String hash) {
        return tokenRepository.findByHash(hash).orElse(null);
    }
}
