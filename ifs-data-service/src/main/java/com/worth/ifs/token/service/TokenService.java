package com.worth.ifs.token.service;

import com.worth.ifs.token.domain.Token;

import java.util.Optional;

public interface TokenService {
    Optional<Token> getTokenByHash(String hash);

    void removeToken(Token token);

    void handleExtraAttributes(Token token);
}
