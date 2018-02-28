package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentReviewPanelInvite;
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
public interface AssessmentPanelInviteRepository extends PagingAndSortingRepository<AssessmentReviewPanelInvite, Long> {

    AssessmentReviewPanelInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<AssessmentReviewPanelInvite> getByCompetitionId(long competitionId);

    List<AssessmentReviewPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<AssessmentReviewPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    List<AssessmentReviewPanelInvite> getByUserId(long userId);

    List<AssessmentReviewPanelInvite> getByIdIn(List<Long> inviteIds);

    AssessmentReviewPanelInvite getByHash(String hash);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}

