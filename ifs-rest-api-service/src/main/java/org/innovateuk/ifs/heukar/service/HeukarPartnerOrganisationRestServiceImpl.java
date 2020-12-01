package org.innovateuk.ifs.heukar.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.heukarOrganisationTypeResourceListType;

@Service
public class HeukarPartnerOrganisationRestServiceImpl extends BaseRestService implements HeukarPartnerOrganisationRestService {

    private String heukarRestUrl = "heukar-partner-organisation";

    @Override
    public RestResult<List<HeukarPartnerOrganisationResource>> getHeukarOrganisationTypesForApplicationWithId(Long applicationId) {
        return getWithRestResult(heukarRestUrl + "/find-by-application-id/" + applicationId, heukarOrganisationTypeResourceListType());
    }

    @Override
    public RestResult<Void> addNewHeukarOrgType(Long applicationId, Long organisationTypeId) {
        return postWithRestResult(heukarRestUrl + "/add-new-org-type/" + applicationId + "/" + organisationTypeId);
    }

    @Override
    public RestResult<Void> updateHeukarOrgType(Long id) {
        return putWithRestResult(heukarRestUrl + "/" + id);
    }

    @Override
    public RestResult<Void> removeHeukarPartnerOrganisation(Long id) {
        return deleteWithRestResult(heukarRestUrl + "/" + id);
    }

    @Override
    public RestResult<HeukarPartnerOrganisationResource> getExistingPartnerById(long id) {
        return getWithRestResult(heukarRestUrl + "/" + id, HeukarPartnerOrganisationResource.class);
    }

}
