package org.innovateuk.ifs.invite.repository;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.AssessmentPanelInvite;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentPanelInviteRepository extends PagingAndSortingRepository<AssessmentPanelInvite, Long> {

    List<AssessmentPanelInvite> getByCompetitionId(long competitionId);

    List<AssessmentPanelInvite> getByCompetitionIdAndStatus(long competitionId, InviteStatus status);

}

