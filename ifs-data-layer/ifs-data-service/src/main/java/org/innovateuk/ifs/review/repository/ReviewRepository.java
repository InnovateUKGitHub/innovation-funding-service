package org.innovateuk.ifs.review.repository;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.resource.ReviewState;
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
public interface ReviewRepository extends ProcessRepository<Review>, PagingAndSortingRepository<Review, Long> {
    List<Review> findByTargetCompetitionIdAndActivityState(long competitionId, ReviewState state);
    boolean existsByParticipantUserAndTargetAndActivityStateNot(User user, Application target, ReviewState state);
    boolean existsByTargetCompetitionIdAndActivityState(long competitionId, ReviewState backingState);

    @Query("SELECT CASE WHEN count(a.id)>0 THEN TRUE ELSE FALSE END " +
            "FROM Application a " +
            "INNER JOIN AssessmentParticipant ap ON ap.competition = a.competition " +
            "WHERE " +
            "  a.competition.id = :competitionId AND a.inAssessmentReviewPanel=true " +
            "AND " +
            "  ap.status = org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED AND " +
            "  ap.role=org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.PANEL_ASSESSOR AND " +
            "  NOT EXISTS (SELECT 1 FROM Review r " +
            "              WHERE " +
            "                r.target=a AND " +
            "                r.participant.user = ap.user AND " +
            "                r.activityState <> org.innovateuk.ifs.review.resource.ReviewState.WITHDRAWN) "
    )
    boolean notifiable(@Param("competitionId") long competitionId);

    List<Review> findByTargetIdAndActivityStateNot(long applicationId, ReviewState withdrawnState);

    List<Review> findByParticipantUserIdAndTargetCompetitionIdOrderByActivityStateAscIdAsc(long userId, long competitionId);
}