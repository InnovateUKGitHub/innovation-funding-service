package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.InnovationLead;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INNOVATION_LEAD;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InnovationLeadRepository extends CompetitionParticipantRepository<InnovationLead> {

    String INNOVATION_LEADS_IN_COMPETITION =
            "(SELECT competitionParticipant.user " +
                    "FROM CompetitionParticipant competitionParticipant " +
                    "WHERE competitionParticipant.role = org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INNOVATION_LEAD " +
                    "AND competitionParticipant.competition.id = :competitionId)";

    String INNOVATION_LEADS_ASSIGNED_T0_COMPETITION =
            "FROM User user " +
                    "JOIN user.roles roles " +
                    "JOIN Competition competition " +
                    "ON competition.id = :competitionId " +
                    "WHERE roles = org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD " +
                    "AND user.status = org.innovateuk.ifs.user.resource.UserStatus.ACTIVE " +
                    "AND user.id != competition.leadTechnologist.id " +
                    "AND user.id IN " +
                    INNOVATION_LEADS_IN_COMPETITION;

    String AVAILABLE_INNOVATION_LEADS =
            "FROM User user " +
                    "JOIN user.roles roles " +
                    "JOIN Competition competition " +
                    "ON competition.id = :competitionId " +
                    "WHERE roles = org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD " +
                    "AND user.status = org.innovateuk.ifs.user.resource.UserStatus.ACTIVE " +
                    "AND user.id != competition.leadTechnologist.id " +
                    "AND user.id NOT in " +
                    INNOVATION_LEADS_IN_COMPETITION;

    @Query(AVAILABLE_INNOVATION_LEADS)
    List<User> findAvailableInnovationLeadsNotAssignedToCompetition(long competitionId);

    @Query(INNOVATION_LEADS_ASSIGNED_T0_COMPETITION)
    List<User> findInnovationsLeadsAssignedToCompetition(long competitionId);

    default List<InnovationLead> findInnovationsLeads(long competitionId) {
        return getByCompetitionIdAndRole(competitionId, INNOVATION_LEAD);
    }

    default InnovationLead findInnovationLead(long competitionId, long userId) {
        return getByCompetitionIdAndUserIdAndRole(competitionId, userId, INNOVATION_LEAD);
    }

    default void deleteInnovationLead(long competitionId, long userId) {
        deleteByCompetitionIdAndUserIdAndRole(competitionId, userId, INNOVATION_LEAD);
    }

    default void deleteAllInnovationLeads(long competitionId) {
        deleteByCompetitionIdAndRole(competitionId, INNOVATION_LEAD);
    }

    default boolean existsInnovationLead(long competitionId, long userId) {
        return existsByCompetitionIdAndUserIdAndRole(competitionId, userId, INNOVATION_LEAD);
    }
}
