package com.worth.ifs.token.repository;

import com.worth.ifs.token.domain.Token;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends PagingAndSortingRepository<Token, Long>{
    Optional<Token> findByHash(@Param("hash") String hash);
}
