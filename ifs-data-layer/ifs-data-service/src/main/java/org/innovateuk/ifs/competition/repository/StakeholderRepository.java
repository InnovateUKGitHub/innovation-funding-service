package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Stakeholder;

import java.util.List;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.STAKEHOLDER;

/**
 * This interface is used to generate Spring Data Repositories.
 * For more info:
 * http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
 */
public interface StakeholderRepository extends CompetitionParticipantRepository<Stakeholder> {

    default List<Stakeholder> findStakeholders(long competitionId) {
        return getByCompetitionIdAndRole(competitionId, STAKEHOLDER);
    }

    default List<Stakeholder> findByStakeholderId(long stakeholderUserId) {
        return getByUserIdAndRole(stakeholderUserId, STAKEHOLDER);
    }

    default boolean findStakeholderByCompetitionIdAndStakeholderEmail(long competitionId, String stakeholderUserEmail) {
        return existsByCompetitionIdAndUserEmailAndRole(competitionId, stakeholderUserEmail, STAKEHOLDER);
    }

    default void deleteAllStakeholders(long competitionId) {
        deleteByCompetitionIdAndRole(competitionId, STAKEHOLDER);
    }

    default void deleteStakeholder(long competitionId, long stakeholderUserId) {
        deleteByCompetitionIdAndUserIdAndRole(competitionId, stakeholderUserId, STAKEHOLDER);
    }
}

