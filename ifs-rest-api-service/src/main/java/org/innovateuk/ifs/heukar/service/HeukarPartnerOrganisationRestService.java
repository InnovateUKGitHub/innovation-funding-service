package org.innovateuk.ifs.heukar.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;

import java.util.List;

public interface HeukarPartnerOrganisationRestService {

    RestResult<List<HeukarPartnerOrganisationResource>> getHeukarOrganisationTypesForApplicationWithId(Long applicationId);

    RestResult<Void> addNewHeukarOrgType(Long applicationId, Long organisationTypeId);

    RestResult<Void> updateHeukarOrgType(Long id);

    RestResult<Void> removeHeukarPartnerOrganisation(Long id);

    RestResult<HeukarPartnerOrganisationResource> getExistingPartnerById(long selectedId);
}
