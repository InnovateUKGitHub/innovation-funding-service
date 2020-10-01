package org.innovateuk.ifs.cofunder.repository;

import org.innovateuk.ifs.cofunder.domain.CofunderAssignment;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.repository.ProcessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface CofunderAssignmentRepository extends ProcessRepository<CofunderAssignment>, PagingAndSortingRepository<CofunderAssignment, Long> {

    Optional<CofunderAssignment> findByParticipantIdAndTargetId(long userId, long applicationId);
    boolean existsByParticipantIdAndTargetId(long userId, long applicationId);

    @Query(
            "SELECT new org.innovateuk.ifs.cofunder.resourceApplicationsForCofundingResource(" +
                    "application.id," +
                    "application.name," +
                    "org.name," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.REJECTED THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.ACCEPTED THEN 1 ELSE 0 END)," +
                    "SUM(CASE WHEN assignment.id IS NOT NULL AND assignment.activityState = org.innovateuk.ifs.cofunder.resource.CofunderState.CREATED THEN 1 ELSE 0 END)" +
                    ")" +
                    "FROM Application application " +
                    "LEFT JOIN CofunderAssignment assignment on application.id = assignment.target.id " +
                    "JOIN ProcessRole pr on pr.applicationId = application.id " +
                    "JOIN Organisation org on pr.organisationId = organisation.id " +
                    "WHERE application.competition.id = :competitionId " +
                    "AND pr.role = org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT " +
                    "GROUP BY application.id"
    )
    Page<ApplicationsForCofundingResource> findApplicationsForCofunding(long competitionId, Pageable pageable);

    @Query(
            "SELECT user " +
            "FROM User user " +
            "JOIN user.roles role " +
            "WHERE NOT EXSITS (" +
            "   SELECT assignment.id FROM CofunderAssignment assignment WHERE assignment.target.id = :applicationId" +
            ")" +
            "AND role = org.innovateuk.ifs.user.resource.Role.COFUNDER"
    )
    Page<User> findUsersAvailableForCofunding(long applicationId, Pageable pageable);
}
