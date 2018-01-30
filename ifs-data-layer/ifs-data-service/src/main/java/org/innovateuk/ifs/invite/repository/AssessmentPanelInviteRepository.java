package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentReviewInvite;
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
public interface AssessmentPanelInviteRepository extends PagingAndSortingRepository<AssessmentReviewInvite, Long> {

    AssessmentReviewInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<AssessmentReviewInvite> getByCompetitionId(long competitionId);

    List<AssessmentReviewInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<AssessmentReviewInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    List<AssessmentReviewInvite> getByUserId(long userId);

    List<AssessmentReviewInvite> getByIdIn(List<Long> inviteIds);

    AssessmentReviewInvite getByHash(String hash);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}

