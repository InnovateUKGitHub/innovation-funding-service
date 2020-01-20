package org.innovateuk.ifs.project.service;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.partnerOrganisationResourceList;


import java.util.List;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.springframework.stereotype.Service;

@Service
public class PartnerOrganisationRestServiceImpl extends BaseRestService implements PartnerOrganisationRestService {
    private String projectRestURL = "/project";

    public RestResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner-organisation", partnerOrganisationResourceList());
    }

    public RestResult<List<PartnerOrganisationResource>> getActiveProjectPartnerOrganisations(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/active-partner-organisation", partnerOrganisationResourceList());
    }

    @Override
    public RestResult<PartnerOrganisationResource> getPartnerOrganisation(long projectId, long organisationId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner/" + organisationId, PartnerOrganisationResource.class);
    }

    @Override
    public RestResult<Void> removePartnerOrganisation(long projectId, long organisationId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/remove-organisation/" + organisationId, Void.class);
    }
}
