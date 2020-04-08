package org.innovateuk.ifs.competition.repository;

import org.innovateuk.ifs.competition.domain.CompetitionFinanceInvite;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.repository.InviteRepository;

import java.util.List;

public interface CompetitionFinanceInviteRepository extends InviteRepository<CompetitionFinanceInvite> {

    List<CompetitionFinanceInvite> findByCompetitionIdAndStatus(long competitionId, InviteStatus status);

    boolean existsByCompetitionIdAndStatusAndEmail(long competitionId, InviteStatus sent, String email);
}


