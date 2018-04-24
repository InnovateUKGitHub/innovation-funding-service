package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of TermsAndConditions
 */
public interface TermsAndConditionsService {

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value="READ", securedType=TermsAndConditionsResource.class,
            description = "Get all the latest terms and condition")
    ServiceResult<List<TermsAndConditionsResource>> getLatestTermsAndConditions();
}
