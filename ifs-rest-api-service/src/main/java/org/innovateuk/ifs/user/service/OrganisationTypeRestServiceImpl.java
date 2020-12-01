package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.organisationTypeResourceListType;

@Service
public class OrganisationTypeRestServiceImpl extends BaseRestService implements OrganisationTypeRestService {

    private String restUrl = "/organisationtype";
    private String heukarRestUrl = "heukar-organisation-type";

    @Override
    public RestResult<OrganisationTypeResource> findOne(Long id) {
        return getWithRestResultAnonymous(restUrl + "/" + id, OrganisationTypeResource.class);
    }

    @Override
    public RestResult<List<OrganisationTypeResource>> getAll() {
        return getWithRestResultAnonymous(restUrl + "/get-all", organisationTypeResourceListType());
    }

    @Override
    public RestResult<OrganisationTypeResource> getForOrganisationId(Long organisationId) {
        return getWithRestResultAnonymous(restUrl + "/get-type-for-organisation/" + organisationId, OrganisationTypeResource.class);
    }

    public RestResult<List<OrganisationTypeResource>> getHeukarOrganisationTypesForApplicationWithId(Long applicationId) {
        return getWithRestResult(heukarRestUrl + "/find-by-application-id/" + applicationId, organisationTypeResourceListType());
    }

    @Override
    public RestResult<Void> addNewHeukarOrgType(Long applicationId, Long organisationTypeId) {
        return postWithRestResult(heukarRestUrl + "/add-new-org-type/" + applicationId + "/" + organisationTypeId);
    }

}
