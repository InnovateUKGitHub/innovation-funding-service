package org.innovateuk.ifs.token.repository;

import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.resource.TokenType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {

    Optional<Token> findByHash(final String hash);

    Optional<Token> findByHashAndTypeAndClassName(final String hash, final TokenType type, final String className);

    Optional<Token> findByTypeAndClassNameAndClassPk(final TokenType type, final String className, final Long classPk);

}
