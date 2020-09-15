package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationRestService {
    RestResult<List<OrganisationResource>> getOrganisationsByApplicationId(Long applicationId);
    RestResult<OrganisationResource> getOrganisationById(long organisationId);
    RestResult<OrganisationResource> getOrganisationByIdForAnonymousUserFlow(Long organisationId);
    RestResult<OrganisationResource> getByUserAndApplicationId(long userId, long applicationId);
    RestResult<OrganisationResource> getByUserAndProjectId(long userId, long projectId);
    RestResult<List<OrganisationResource>> getAllByUserId(long userId);
    RestResult<List<OrganisationResource>> getOrganisations(long userId, boolean international);
    RestResult<OrganisationResource> createOrMatch(OrganisationResource organisation);
    RestResult<OrganisationResource> updateNameAndRegistration(OrganisationResource organisation);

}
