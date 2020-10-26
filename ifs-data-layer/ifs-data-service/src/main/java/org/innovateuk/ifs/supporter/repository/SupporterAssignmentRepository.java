package org.innovateuk.ifs.supporter.repository;

import org.innovateuk.ifs.supporter.domain.SupporterAssignment;
import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingResource;
import org.innovateuk.ifs.supporter.domain.CompetitionForCofunding;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface SupporterAssignmentRepository extends ProcessRepository<SupporterAssignment>, PagingAndSortingRepository<SupporterAssignment, Long> {

    @Query(
            "SELECT new org.innovateuk.ifs.supporter.domain.CompetitionForCofunding( " +
                    "competition, " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.supporter.resource.SupporterState.CREATED THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.supporter.resource.SupporterState.REJECTED THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.supporter.resource.SupporterState.ACCEPTED THEN 1 ELSE 0 END)" +
                    ") " +
                    "FROM SupporterAssignment assignment " +
                    "INNER JOIN Application application ON application.id = assignment.target.id " +
                    "INNER JOIN Competition competition ON competition.id = application.competition " +
                    "WHERE assignment.participant.id = :userId " +
                    "GROUP BY competition.id"
    )
    List<CompetitionForCofunding> findCompetitionsForParticipant(long userId);

    Optional<SupporterAssignment> findByParticipantIdAndTargetId(long userId, long applicationId);

    List<SupporterAssignment> findByParticipantId(long userId);

    boolean existsByParticipantIdAndTargetId(long userId, long applicationId);

    String QUERY = "FROM User user " +
            "JOIN user.roles role " +
            "WHERE role = org.innovateuk.ifs.user.resource.Role.SUPPORTER " +
            "AND CONCAT(user.firstName, ' ', user.lastName) LIKE CONCAT('%', :filter, '%') " +
            "AND NOT EXISTS (" +
            "   SELECT assignment.id FROM SupporterAssignment assignment WHERE assignment.target.id = :applicationId AND assignment.participant.id = user.id" +
            ")";

    @Query(
            "SELECT CASE WHEN count(assignment)>0 THEN TRUE ELSE FALSE END " +
                    "FROM SupporterAssignment assignment " +
                    "INNER JOIN Application application ON application.id = assignment.target.id " +
                    "WHERE assignment.participant.id = :userId " +
                    "AND application.competition.id = :competitionId"
    )
    boolean existsByParticipantIdAndCompetitionId(long userId, long competitionId);

    @Query(
            "SELECT new org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationResource( " +
                    "application.id, " +
                    "application.name, " +
                    "organisation.name, " +
                    "assignment.activityState" +
                    ") " +
                    "FROM SupporterAssignment assignment " +
                    "JOIN assignment.target application " +
                    "JOIN ProcessRole pr on pr.applicationId = application.id " +
                    "JOIN Organisation organisation on pr.organisationId = organisation.id " +
                    "WHERE application.competition.id = :competitionId " +
                    "AND assignment.participant.id = :userId " +
                    "AND pr.role = org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT " +
                    "AND assignment.activityState in :states "
    )
    Page<SupporterDashboardApplicationResource> findApplicationsForSupporterCompetitionDashboard(long userId, long competitionId, Collection<SupporterState> states, Pageable pageable);

    @Query(
            "SELECT new org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingResource( " +
                    "application.id, " +
                    "application.name, " +
                    "organisation.name, " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.supporter.resource.SupporterState.REJECTED THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.supporter.resource.SupporterState.ACCEPTED THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.supporter.resource.SupporterState.CREATED THEN 1 ELSE 0 END) " +
                    ") " +
                    "FROM Application application " +
                    "LEFT JOIN SupporterAssignment assignment on application.id = assignment.target.id " +
                    "JOIN ProcessRole pr on pr.applicationId = application.id " +
                    "JOIN Organisation organisation on pr.organisationId = organisation.id " +
                    "WHERE application.competition.id = :competitionId " +
                    "AND application.applicationProcess.activityState = org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED " +
                    "AND pr.role = org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT " +
                    "AND (str(application.id) LIKE CONCAT('%', :filter, '%')) " +
                    "GROUP BY application.id"
    )
    Page<ApplicationsForCofundingResource> findApplicationsForCofunding(long competitionId, String filter, Pageable pageable);

    @Query("SELECT user " + QUERY)
    Page<User> findUsersAvailableForCofunding(long applicationId, String filter, Pageable pageable);

    @Query("SELECT user.id " + QUERY)
    List<Long> usersAvailableForCofundingUserIds(long applicationId, String filter);

    @Query("SELECT COUNT(DISTINCT assignment.participant) " +
            "FROM SupporterAssignment assignment " +
            "WHERE assignment.target.competition.id = :competitionId")
    int countByTargetCompetitionId(long competitionId);
}
