package org.innovateuk.ifs.finance.totals.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Interface for CompetitionFinanceTotalsSenderImpl defining security rules.
 */
public interface CompetitionFinanceTotalsSender {
    @PreAuthorize("hasAuthority('system_maintainer')")
    @SecuredBySpring(value = "SEND_APPLICATION_TOTALS",
            description = "Only the system maintainer can send all the totals.")
    ServiceResult<Void> sendFinanceTotalsForCompetition(Long competitionId);
}