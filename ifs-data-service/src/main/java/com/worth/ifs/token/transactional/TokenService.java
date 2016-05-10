package com.worth.ifs.token.transactional;

import com.worth.ifs.token.domain.Token;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface TokenService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    Optional<Token> getTokenByHash(String hash);

    @PreAuthorize("hasPermission(#token, 'DELETE')")
    void removeToken(Token token);

    @PreAuthorize("hasPermission(#token, 'READ')")
    void handleExtraAttributes(Token token);
}
