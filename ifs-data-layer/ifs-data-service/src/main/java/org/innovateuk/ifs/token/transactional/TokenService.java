package org.innovateuk.ifs.token.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.token.domain.Token;
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
