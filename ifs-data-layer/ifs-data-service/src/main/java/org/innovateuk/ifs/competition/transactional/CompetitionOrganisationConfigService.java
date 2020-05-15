package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionOrganisationConfigService {

    @NotSecured(value = "Any user can find the international competitions", mustBeSecuredByOtherServices = false)
    ServiceResult<CompetitionOrganisationConfigResource> findOneByCompetitionId(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'ifs_administrator')")
    @SecuredBySpring(value="UPDATE", securedType= CompetitionOrganisationConfigResource.class,
            description = "Only Comp Admins and IFS Admins able to save Competition organisation configurations")
    ServiceResult<Void> update(long competitionId, CompetitionOrganisationConfigResource competitionOrganisationConfigResource);
}