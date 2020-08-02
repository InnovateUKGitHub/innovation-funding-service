package org.innovateuk.ifs.organisation.transactional;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.springframework.security.core.parameters.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * Represents operations surrounding the use of Organisations in the system
 */
public interface OrganisationService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<Set<OrganisationResource>> findByApplicationId(long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> findById(long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> getByUserAndApplicationId(long userId, long applicationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> getByUserAndProjectId(long userId, long projectId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<OrganisationResource>> getAllByUserId(long userId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<OrganisationResource>> getOrganisations(long userId, boolean international);

    @PreAuthorize("hasPermission(#organisation, 'CREATE')")
    ServiceResult<OrganisationResource> create(@P("organisation") OrganisationResource organisation);

    @PreAuthorize("hasPermission(#organisation, 'UPDATE')")
    ServiceResult<OrganisationResource> update(@P("organisation") OrganisationResource organisationResource);

    @PreAuthorize("hasPermission(#organisationId, 'org.innovateuk.ifs.organisation.resource.OrganisationResource', 'UPDATE')")
    ServiceResult<OrganisationResource> updateOrganisationNameAndRegistration(final long organisationId, final String organisationName, final String registrationNumber);

    @PreAuthorize("hasPermission(#organisationId, 'org.innovateuk.ifs.organisation.resource.OrganisationResource', 'UPDATE')")
    ServiceResult<OrganisationResource> addAddress(@P("organisationId") long organisationId, OrganisationAddressType addressType, AddressResource addressResource);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<OrganisationSearchResult>> searchAcademic(String organisationName, int maxItems);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationSearchResult> getSearchOrganisation(@P("organisationId") long searchOrganisationId);
}
