package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.commons.security.SecuredBySpring;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 *
 */
public interface CompanyHouseApiService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "USE_COMPANIES_HOUSE_API", description = "The System Registration user can search for Organisations via the Companies House API on behalf of non-logged in users during the registration process",
        additionalComments = "The purpose of securing this method via a @PreAuthorize rule rather than filtering the returned results is to prevent any calls out to " +
                "the Companies House API altogether that will not be allowed, so no cost is incurred for forbidden access")
    ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationSearchResult> getOrganisationById(String id);
}
