package com.worth.ifs.token.security;

import com.worth.ifs.commons.security.PermissionEntityLookupStrategies;
import com.worth.ifs.commons.security.PermissionEntityLookupStrategy;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
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
