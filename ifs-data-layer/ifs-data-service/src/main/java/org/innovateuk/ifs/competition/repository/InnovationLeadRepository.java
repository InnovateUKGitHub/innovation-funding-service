package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.InnovationLead;

import java.util.List;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.INNOVATION_LEAD;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface InnovationLeadRepository extends CompetitionParticipantRepository<InnovationLead> {

    default InnovationLead findInnovationLead(long competitionId, long userId) {
        return getByCompetitionIdAndUserIdAndRole(competitionId, userId, INNOVATION_LEAD);
    }

    default List<InnovationLead> findInnovationsLeads(long competitionId) {
        return getByCompetitionIdAndRole(competitionId, INNOVATION_LEAD);
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
