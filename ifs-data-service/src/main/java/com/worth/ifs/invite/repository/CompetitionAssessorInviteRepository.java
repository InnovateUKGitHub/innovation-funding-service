package com.worth.ifs.invite.repository;

import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.domain.CompetitionAssessorInvite;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompetitionAssessorInviteRepository extends CrudRepository<CompetitionAssessorInvite, Long> {

    // TODO want this to include the Competition
    CompetitionAssessorInvite getByHash(@Param("hash") String hash);
}