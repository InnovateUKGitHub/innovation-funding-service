package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationRestService {

    RestResult<List<OrganisationResource>> getOrganisationsByApplicationId(Long applicationId);
    RestResult<OrganisationResource> getOrganisationById(Long organisationId);
    RestResult<OrganisationResource> getOrganisationByIdForAnonymousUserFlow(Long organisationId);
    RestResult<OrganisationResource> getOrganisationByUserId(Long userId);
    RestResult<OrganisationResource> createOrMatch(OrganisationResource organisation);
    RestResult<OrganisationResource> createAndLinkByInvite(OrganisationResource organisation, String inviteHash);
    RestResult<OrganisationResource> updateNameAndRegistration(OrganisationResource organisation);
    RestResult<OrganisationResource> addAddress(OrganisationResource organisation, AddressResource address, AddressTypeEnum type);
}
