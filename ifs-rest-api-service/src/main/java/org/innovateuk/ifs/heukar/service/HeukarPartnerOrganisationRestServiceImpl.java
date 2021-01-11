package org.innovateuk.ifs.heukar.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationResource;
import org.innovateuk.ifs.heukar.resource.HeukarPartnerOrganisationTypeEnum;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.heukarPartnerOrganisationResourceListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.heukarPartnerOrganisationTypeEnumListType;

@Service
public class HeukarPartnerOrganisationRestServiceImpl extends BaseRestService implements HeukarPartnerOrganisationRestService {

    private String heukarRestUrl = "heukar-partner-organisation";

    @Override
    public RestResult<List<HeukarPartnerOrganisationResource>> getHeukarPartnerOrganisationsForApplicationWithId(Long applicationId) {
        return getWithRestResult(heukarRestUrl + "/find-by-application-id/" + applicationId, heukarPartnerOrganisationResourceListType());
    }

    @Override
    public RestResult<Void> addNewHeukarOrgType(Long applicationId, Long organisationTypeId) {
        return postWithRestResult(heukarRestUrl + "/add-new-org-type/" + applicationId + "/" + organisationTypeId);
    }

    @Override
    public RestResult<Void> updateHeukarOrgType(Long id, long organisationTypeId) {
        return putWithRestResult(heukarRestUrl + "/" + id + "/" + organisationTypeId);
    }

    @Override
    public RestResult<Void> removeHeukarPartnerOrganisation(Long id) {
        return deleteWithRestResult(heukarRestUrl + "/" + id);
    }

    @Override
    public RestResult<HeukarPartnerOrganisationResource> getExistingPartnerById(long id) {
        return getWithRestResult(heukarRestUrl + "/" + id, HeukarPartnerOrganisationResource.class);
    }

    @Override
    public RestResult<List<HeukarPartnerOrganisationTypeEnum>> getAllHeukarPartnerOrganisationTypes() {
        return getWithRestResult(heukarRestUrl + "/all-org-types", heukarPartnerOrganisationTypeEnumListType());
    }

}
