package org.innovateuk.ifs.assessment.panel.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface AssessmentReviewRepository extends ProcessRepository<AssessmentReview>, PagingAndSortingRepository<AssessmentReview, Long> {
    List<AssessmentReview> findByTargetCompetitionIdAndActivityStateState(long applicationId, State backingState);
    boolean existsByParticipantUserAndTargetAndActivityStateStateNot(User user, Application target, State state);
    boolean existsByTargetCompetitionIdAndActivityStateState(long competitionId, State backingState);
//    boolean existsByTargetCompetitionIdAndTargetInAssessmentPanelAndApplicationProcessActivityStateState(long competitionId, boolean inAssessmentPanel, AssessmentReviewState created);

    @Query("SELECT CASE WHEN count(a.id)> 0 THEN TRUE ELSE FALSE END AS foo FROM Application a WHERE a.competition.id = :competitionId AND a.inAssessmentPanel=true AND " +
            "NOT EXISTS (SELECT 1 FROM AssessmentReview r where r.target=a AND r.participant.user = :user) " )
    boolean foo(long competitionId, User user);

    @Query("SELECT CASE WHEN count(a.id)>0 THEN TRUE ELSE FALSE END " +
            "FROM Application a " +
            "INNER JOIN CompetitionAssessmentParticipant cap ON cap.competition = a.competition " +
            "WHERE " +
            "  a.competition.id = :competitionId AND a.inAssessmentPanel=true " +
            "AND " +
            "  cap.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED AND " +
            "  cap.role=org.innovateuk.ifs.invite.domain.competition.CompetitionParticipantRole.PANEL_ASSESSOR AND " +
            "  NOT EXISTS (SELECT 1 FROM AssessmentReview r " +
            "              WHERE " +
            "                r.target=a AND " +
            "                r.participant.user = cap.user AND " +
            "                r.activityState.state <> org.innovateuk.ifs.workflow.resource.State.WITHDRAWN) "
    )
    boolean notifiable(@Param("competitionId") long competitionId);
}