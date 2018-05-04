package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for operations around the usage and processing of TermsAndConditions
 */
public interface TermsAndConditionsService {

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ", securedType = GrantTermsAndConditionsResource.class,
            description = "Get all the latest terms and condition")
    ServiceResult<List<GrantTermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions();

    @PreAuthorize("hasAnyAuthority('comp_admin')")
    @SecuredBySpring(value = "READ", securedType = GrantTermsAndConditionsResource.class,
            description = "Get terms and condition by id")
    ServiceResult<GrantTermsAndConditionsResource> getById(Long id);

    @NotSecured(value = "Any user can see the latest site terms and conditions",
            mustBeSecuredByOtherServices = false)
    ServiceResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions();

}
