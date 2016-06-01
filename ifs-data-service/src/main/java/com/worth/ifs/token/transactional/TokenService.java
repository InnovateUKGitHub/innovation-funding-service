package com.worth.ifs.token.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.token.domain.Token;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface TokenService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<Token> getEmailToken(String hash);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<Token> getPasswordResetToken(String hash);

    @PreAuthorize("hasPermission(#token, 'DELETE')")
    void removeToken(Token token);

    @PreAuthorize("hasPermission(#token, 'READ')")
    void handleExtraAttributes(Token token);
}
