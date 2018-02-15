package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.competition.ReviewInvite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentPanelInviteRepository extends PagingAndSortingRepository<ReviewInvite, Long> {

    ReviewInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<ReviewInvite> getByCompetitionId(long competitionId);

    List<ReviewInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<ReviewInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    List<ReviewInvite> getByUserId(long userId);

    List<ReviewInvite> getByIdIn(List<Long> inviteIds);

    ReviewInvite getByHash(String hash);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}

