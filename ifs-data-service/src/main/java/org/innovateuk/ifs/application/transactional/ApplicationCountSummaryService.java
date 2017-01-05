package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.xml.ws.ServiceMode;
import java.util.List;

public interface ApplicationCountSummaryService {

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "READ", description = "Comp Admins can see all Application Summary counts accros the whole system", securedType = ApplicationCountSummaryPageResource.class)
    ServiceResult<List<ApplicationCountSummaryResource>> getApplicationCountSummariesByCompetitionId(Long competitionId);
}

