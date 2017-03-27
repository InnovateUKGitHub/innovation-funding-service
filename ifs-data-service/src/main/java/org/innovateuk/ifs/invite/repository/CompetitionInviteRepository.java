package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.CompetitionInvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CompetitionInviteRepository extends PagingAndSortingRepository<CompetitionInvite, Long> {

    CompetitionInvite getByEmailAndCompetitionId(String email, long competitionId);

    Page<CompetitionInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    CompetitionInvite getByHash(String hash);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);
}
