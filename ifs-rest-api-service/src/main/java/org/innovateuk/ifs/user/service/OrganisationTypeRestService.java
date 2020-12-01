package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;

public interface OrganisationTypeRestService {

    RestResult<OrganisationTypeResource> findOne(Long id);

    RestResult<List<OrganisationTypeResource>> getAll();

    RestResult<OrganisationTypeResource> getForOrganisationId(Long organisationId);

    RestResult<List<OrganisationTypeResource>> getHeukarOrganisationTypesForApplicationWithId(Long applicationId);

    RestResult<Void> addNewHeukarOrgType(Long applicationId, Long organisationTypeId);
}
