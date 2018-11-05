package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.Stakeholder;

import java.util.List;
import java.util.stream.Collectors;

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

    default List<Long> findCompetitionsByStakeholderId(long userId){
        return getCompetitionByUserIdAndRole(userId, STAKEHOLDER)
                .stream()
                .map(cp -> cp.getProcess().getId())
                .collect(Collectors.toList());
    }

    default void deleteAllStakeholders(long competitionId) {
        deleteByCompetitionIdAndRole(competitionId, STAKEHOLDER);
    }

    default void deleteStakeholder(long competitionId, long stakeholderUserId) {
        deleteByCompetitionIdAndUserIdAndRole(competitionId, stakeholderUserId, STAKEHOLDER);
    }
}

