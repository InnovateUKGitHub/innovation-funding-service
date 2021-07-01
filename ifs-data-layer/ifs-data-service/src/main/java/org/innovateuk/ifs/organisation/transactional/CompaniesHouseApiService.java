package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 *
 */
public interface CompaniesHouseApiService {

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "USE_COMPANIES_HOUSE_API", description = "The System Registration user can search for Organisations via the Companies House API on behalf of non-logged in users during the registration process",
            additionalComments = "The purpose of securing this method via a @PreAuthorize rule rather than filtering the returned results is to prevent any calls out to " +
                    "the Companies House API altogether that will not be allowed, so no cost is incurred for forbidden access")
    ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText, int indexPos);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationSearchResult> getOrganisationById(String id);
}
