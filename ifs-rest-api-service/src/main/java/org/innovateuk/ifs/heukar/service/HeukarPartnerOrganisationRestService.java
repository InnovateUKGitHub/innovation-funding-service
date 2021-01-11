package org.innovateuk.ifs.heukar.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;

import java.util.List;

public interface HeukarPartnerOrganisationRestService {

    RestResult<List<HeukarPartnerOrganisationResource>> getHeukarPartnerOrganisationsForApplicationWithId(Long applicationId);

    RestResult<Void> addNewHeukarOrgType(Long applicationId, Long organisationTypeId);

    RestResult<Void> updateHeukarOrgType(Long id, long organisationTypeId);

    RestResult<Void> removeHeukarPartnerOrganisation(Long id);

    RestResult<HeukarPartnerOrganisationResource> getExistingPartnerById(long selectedId);

    RestResult<List<HeukarPartnerOrganisationTypeEnum>> getAllHeukarPartnerOrganisationTypes();
}
