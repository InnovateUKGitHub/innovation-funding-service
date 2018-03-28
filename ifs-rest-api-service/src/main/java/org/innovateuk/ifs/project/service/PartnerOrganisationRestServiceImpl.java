package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.partnerOrganisationResourceList;

@Service
public class PartnerOrganisationRestServiceImpl extends BaseRestService implements PartnerOrganisationRestService {
    private String projectRestURL = "/project";

    public RestResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner-organisation", partnerOrganisationResourceList());
    }

    @Override
    public RestResult<PartnerOrganisationResource> getPartnerOrganisation(long projectId, long organisationId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner/" + organisationId, PartnerOrganisationResource.class);
    }
}
