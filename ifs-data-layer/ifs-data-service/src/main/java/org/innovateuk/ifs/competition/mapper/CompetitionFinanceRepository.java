package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.competition.domain.CompetitionFinance;
import org.innovateuk.ifs.competition.repository.CompetitionParticipantRepository;

import java.util.List;

import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.COMPETITION_FINANCE;

public interface CompetitionFinanceRepository extends CompetitionParticipantRepository<CompetitionFinance> {

    boolean existsByCompetitionIdAndUserId(long competitionId, long loggedInUserId);

    default List<CompetitionFinance> findCompetitionFinance(long competitionId) {
        return getByCompetitionIdAndRole(competitionId, COMPETITION_FINANCE);
    }

    default List<CompetitionFinance> findByCompetitionFinanceId(long compFinanceId) {
        return getByUserIdAndRole(compFinanceId, COMPETITION_FINANCE);
    }

    default boolean existsByCompetitionIdAndCompetitionFinanceEmail(long competitionId, String compFinanceEmail) {
        return existsByCompetitionIdAndUserEmailAndRole(competitionId, compFinanceEmail, COMPETITION_FINANCE);
    }

    default void deleteCompetitionFinance(long competitionId, long userId) {
        deleteByCompetitionIdAndUserIdAndRole(competitionId, userId, COMPETITION_FINANCE);
    }
}

