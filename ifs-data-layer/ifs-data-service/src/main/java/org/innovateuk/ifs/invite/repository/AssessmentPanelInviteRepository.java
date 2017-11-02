package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.AssessmentPanelInvite;
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
public interface AssessmentPanelInviteRepository extends PagingAndSortingRepository<AssessmentPanelInvite, Long> {

    AssessmentPanelInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<AssessmentPanelInvite> getByCompetitionId(long competitionId);

    List<AssessmentPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<AssessmentPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    List<AssessmentPanelInvite> getByUserId(long userId);

    List<AssessmentPanelInvite> getByIdIn(List<Long> inviteIds);

    AssessmentPanelInvite getByHash(String hash);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}

