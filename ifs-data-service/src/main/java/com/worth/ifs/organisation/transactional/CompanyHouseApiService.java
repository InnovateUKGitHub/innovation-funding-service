package com.worth.ifs.organisation.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 *
 */
public interface CompanyHouseApiService {

    @PreAuthorize("hasAuthority('system_registrar')")
    ServiceResult<List<OrganisationSearchResult>> searchOrganisations(String encodedSearchText);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationSearchResult> getOrganisationById(String id);
}
