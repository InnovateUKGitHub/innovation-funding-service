package com.worth.ifs.token.transactional;

import com.worth.ifs.security.NotSecured;
import com.worth.ifs.token.domain.Token;

import java.util.Optional;

public interface TokenService {
    @NotSecured("Not secured, this is used in the email verification flow.")
    Optional<Token> getTokenByHash(String hash);

    @NotSecured("Not secured, this is used in the email verification flow.")
    void removeToken(Token token);

    @NotSecured("Not secured, this is used in the email verification flow.")
    void handleExtraAttributes(Token token);
}
