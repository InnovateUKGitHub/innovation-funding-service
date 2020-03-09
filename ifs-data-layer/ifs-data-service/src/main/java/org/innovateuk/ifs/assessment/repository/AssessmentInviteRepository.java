package org.innovateuk.ifs.assessment.repository;

import org.innovateuk.ifs.assessment.domain.AssessmentInvite;
import org.innovateuk.ifs.competition.repository.CompetitionInviteRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssessmentInviteRepository extends CompetitionInviteRepository<AssessmentInvite> {

    String USERS_WITH_COMPETITION_INVITE = "SELECT invite.user.id " +
            "FROM AssessmentInvite invite " +
            "WHERE invite.competition.id = :competitionId " +
            "AND invite.user IS NOT NULL";

    String ASSESSORS_WITH_COMPETITION = "SELECT user " +
            "FROM User user " +
            "JOIN user.roles roles " +
            "WHERE user.id NOT IN (" + USERS_WITH_COMPETITION_INVITE + ") " +
            "AND roles = org.innovateuk.ifs.user.resource.Role.ASSESSOR " +
            "GROUP BY user.id ";

    /**
     * We have to explicitly join {@link User} and Profile due to the relational mapping
     * on {@link User} being removed. This join was not possible without using a
     * cartesian product before Hibernate 5.1.
     * <p>
     * Unfortunately, due to this explicit join, we cannot leverage Spring JPAs {@link Specification}
     * as we have to use the {@link Query} annotation to create this query.
     * <p>
     * We should try to be clever with how we write the query. Otherwise, we will have to have
     * multiple repository methods that have different parameters (and this will get out of hand quickly).
     * <p>
     * Try to keep any other required filtering parameters in this query.
     */
    String ASSESSORS_WITH_COMPETITION_AND_ASSESSOR_NAME =
            "FROM User user " +
            "JOIN Profile profile ON profile.id = user.profileId " +
            "JOIN user.roles roles " +
            "JOIN user.roleProfileStatuses roleStatuses " +
            "WHERE user.id NOT IN (" + USERS_WITH_COMPETITION_INVITE + ") " +
            "AND roles = org.innovateuk.ifs.user.resource.Role.ASSESSOR " +
            "AND CONCAT(user.firstName, ' ', user.lastName) LIKE CONCAT('%', :assessorNameFilter, '%') " +
            "AND roleStatuses.profileRole = org.innovateuk.ifs.user.resource.ProfileRole.ASSESSOR " +
            "AND roleStatuses.roleProfileState = org.innovateuk.ifs.user.resource.RoleProfileState.ACTIVE " +
            "AND user.status = org.innovateuk.ifs.user.resource.UserStatus.ACTIVE " +
            "GROUP BY user.id ";

    @Query(ASSESSORS_WITH_COMPETITION)
    Page<User> findAssessorsByCompetition(@Param("competitionId") long competitionId, Pageable pageable);

    @Query("SELECT user " + ASSESSORS_WITH_COMPETITION_AND_ASSESSOR_NAME)
    Page<User> findAssessorsByCompetitionAndAssessorNameLike(@Param("competitionId") long competitionId,
                                                             @Param("assessorNameFilter") String assessorNameFilter, Pageable pageable);

    @Query("SELECT user.id " + ASSESSORS_WITH_COMPETITION_AND_ASSESSOR_NAME)
    List<Long> findAssessorsByCompetitionAndAssessorNameLike(@Param("competitionId") long competitionId, @Param("assessorNameFilter") String assessorNameFilter);
}
