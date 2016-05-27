package com.worth.ifs.token.repository;

import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.resource.TokenType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long> {

    Optional<Token> findByHash(final String hash);

    Optional<Token> findByHashAndTypeAndClassName(final String hash, final TokenType type, final String className);

}
