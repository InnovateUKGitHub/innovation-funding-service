package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.competition.domain.ExternalFinance;
import org.innovateuk.ifs.competition.repository.CompetitionParticipantRepository;

import java.util.List;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.EXTERNAL_FINANCE;

public interface ExternalFinanceRepository extends CompetitionParticipantRepository<ExternalFinance> {

    boolean existsByCompetitionIdAndUserId(long competitionId, long loggedInUserId);

    default List<ExternalFinance> findCompetitionFinance(long competitionId) {
        return getByCompetitionIdAndRole(competitionId, EXTERNAL_FINANCE);
    }

    default List<ExternalFinance> findByCompetitionFinanceId(long compFinanceId) {
        return getByUserIdAndRole(compFinanceId, EXTERNAL_FINANCE);
    }

    default boolean existsByCompetitionIdAndCompetitionFinanceEmail(long competitionId, String compFinanceEmail) {
        return existsByCompetitionIdAndUserEmailAndRole(competitionId, compFinanceEmail, EXTERNAL_FINANCE);
    }

    default void deleteCompetitionFinance(long competitionId, long userId) {
        deleteByCompetitionIdAndUserIdAndRole(competitionId, userId, EXTERNAL_FINANCE);
    }
}

