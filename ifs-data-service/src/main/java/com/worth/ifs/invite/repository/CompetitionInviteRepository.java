package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.CompetitionInvite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionInviteRepository extends CrudRepository<CompetitionInvite, Long> {

    CompetitionInvite getByHash(@Param("hash") String hash);
}