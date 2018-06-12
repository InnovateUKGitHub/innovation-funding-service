package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionTypeResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface CompetitionTypeService {

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can return read all competition types")
    @PreAuthorize("hasAnyAuthority('comp_admin' , 'project_finance')")
    ServiceResult<List<CompetitionTypeResource>> findAllTypes();
}
