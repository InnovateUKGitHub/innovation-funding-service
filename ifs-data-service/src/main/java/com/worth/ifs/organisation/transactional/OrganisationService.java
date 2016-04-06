package com.worth.ifs.organisation.transactional;

import java.util.List;
import java.util.Set;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.resource.OrganisationSearchResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.resource.OrganisationResource;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
public interface OrganisationService {

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Set<OrganisationResource>> findByApplicationId(Long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> findById(Long organisationId);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> create(Organisation organisation);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> saveResource(OrganisationResource organisationResource);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationResource> addAddress(Long organisationId, AddressType addressType, AddressResource addressResource);
    
    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<List<OrganisationSearchResult>> searchAcademic(String organisationName, int maxItems);

    @NotSecured("When creating a application, this methods is called before creating a user account, so there his no way to authenticate.")
    ServiceResult<OrganisationSearchResult> getSearchOrganisation(Long searchOrganisationId);
}
