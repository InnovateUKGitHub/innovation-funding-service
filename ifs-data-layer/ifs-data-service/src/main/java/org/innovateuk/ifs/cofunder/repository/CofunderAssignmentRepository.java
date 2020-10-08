package org.innovateuk.ifs.cofunder.repository;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingResource;
import org.innovateuk.ifs.cofunder.domain.CompetitionForCofunding;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CofunderAssignmentRepository extends ProcessRepository<CofunderAssignment>, PagingAndSortingRepository<CofunderAssignment, Long> {

    @Query("SELECT new org.innovateuk.ifs.cofunder.domain.CompetitionForCofunding(" +
            "competition, " +
            "SUM(CASE WHEN p.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.CREATED THEN 1 ELSE 0 END)," +
            "SUM(CASE WHEN p.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.REJECTED THEN 1 ELSE 0 END)," +
            "SUM(CASE WHEN p.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.ACCEPTED THEN 1 ELSE 0 END)" +
            ") " +
            "FROM CofunderAssignment assignment" +
            "INNER JOIN Application application ON application.id = assignment.target.id " +
            "INNER JOIN Competition competition ON competition.id = application.competition " +
            "WHERE assignment.participant.id = :userId " +
            "GROUP BY competition.id"
    )
    List<CompetitionForCofunding> findCompetitionsForParticipant(long userId);

    Optional<CofunderAssignment> findByParticipantIdAndTargetId(long userId, long applicationId);
    boolean existsByParticipantIdAndTargetId(long userId, long applicationId);

    @Query(
            "SELECT new org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingResource( " +
                    "application.id, " +
                    "application.name, " +
                    "organisation.name, " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.REJECTED THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.ACCEPTED THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.CREATED THEN 1 ELSE 0 END) " +
                    ") " +
                    "FROM Application application " +
                    "LEFT JOIN CofunderAssignment assignment on application.id = assignment.target.id " +
                    "JOIN ProcessRole pr on pr.applicationId = application.id " +
                    "JOIN Organisation organisation on pr.organisationId = organisation.id " +
                    "WHERE application.competition.id = :competitionId " +
                    "AND pr.role = org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT " +
                    "AND (str(application.id) LIKE CONCAT('%', :filter, '%')) " +
                    "GROUP BY application.id"
    )
    Page<ApplicationsForCofundingResource> findApplicationsForCofunding(long competitionId, String filter, Pageable pageable);

    @Query(
            "SELECT user " +
            "FROM User user " +
            "JOIN user.roles role " +
            "WHERE role = org.innovateuk.ifs.user.resource.Role.COFUNDER " +
            "AND CONCAT(user.firstName, ' ', user.lastName) LIKE CONCAT('%', :filter, '%') " +
            "AND NOT EXISTS (" +
            "   SELECT assignment.id FROM CofunderAssignment assignment WHERE assignment.target.id = :applicationId AND assignment.participant.id = user.id" +
            ")"
    )
    Page<User> findUsersAvailableForCofunding(long applicationId, String filter, Pageable pageable);
}
