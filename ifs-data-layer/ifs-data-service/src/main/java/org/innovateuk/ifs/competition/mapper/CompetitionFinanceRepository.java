package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.competition.domain.CompetitionFinance;
import org.innovateuk.ifs.competition.repository.CompetitionParticipantRepository;

public interface CompetitionFinanceRepository extends CompetitionParticipantRepository<CompetitionFinance> {

    boolean existsByCompetitionIdAndUserId(long competitionId, long loggedInUserId);
}

