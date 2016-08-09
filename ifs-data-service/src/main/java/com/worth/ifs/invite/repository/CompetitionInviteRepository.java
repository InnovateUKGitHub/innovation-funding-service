package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.CompetitionInvite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CompetitionInviteRepository extends CrudRepository<CompetitionInvite, Long> {

    CompetitionInvite getByHash(@Param("hash") String hash);
}