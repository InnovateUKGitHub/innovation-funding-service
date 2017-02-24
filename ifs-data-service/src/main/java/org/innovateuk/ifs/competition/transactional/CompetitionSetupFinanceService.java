package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupFinanceResource;
import org.springframework.security.access.prepost.PreAuthorize;


/**
 * Service interface to deal with the finance part of competition setup.
 */
public interface CompetitionSetupFinanceService {

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "SAVE", securedType = CompetitionSetupFinanceResource.class, description = "Comp Admins and project finance users should be able to edit the competition setup details for finance")
    ServiceResult<Void> save(CompetitionSetupFinanceResource competitionSetupFinanceResource);

    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    @SecuredBySpring(value = "READ", securedType = CompetitionSetupFinanceResource.class, description = "Comp Admins and project finance users should be able to read the competition setup details for finance")
    ServiceResult<CompetitionSetupFinanceResource> getForCompetition(Long competitionId);

}
