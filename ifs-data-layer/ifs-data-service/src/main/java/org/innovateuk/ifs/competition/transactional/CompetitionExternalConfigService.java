package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CompetitionExternalConfigService {

    @SecuredBySpring(value = "READ", description = "A system maintainer can see the competition")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<CompetitionExternalConfigResource> findOneByCompetitionId(long competitionId);

    @SecuredBySpring(value = "UPDATE_EXTERNAL_COMP", description = "A system maintainer can update the external competition data.")
    @PreAuthorize("hasAuthority('system_maintainer')")
    ServiceResult<Void> update(long competitionId, CompetitionExternalConfigResource competitionExternalConfigResource);

}
