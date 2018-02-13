package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.competition.AssessmentInterviewPanelInvite;
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
public interface AssessmentInterviewPanelInviteRepository extends PagingAndSortingRepository<AssessmentInterviewPanelInvite, Long> {

    AssessmentInterviewPanelInvite getByEmailAndCompetitionId(String email, long competitionId);

    List<AssessmentInterviewPanelInvite> getByCompetitionId(long competitionId);

    List<AssessmentInterviewPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    Page<AssessmentInterviewPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status, Pageable pageable);

    int countByCompetitionIdAndStatusIn(long competitionId, Set<InviteStatus> statuses);

    List<AssessmentInterviewPanelInvite> getByUserId(long userId);

    List<AssessmentInterviewPanelInvite> getByIdIn(List<Long> inviteIds);

    AssessmentInterviewPanelInvite getByHash(String hash);

    void deleteByCompetitionIdAndStatus(long competitionId, InviteStatus status);
}

