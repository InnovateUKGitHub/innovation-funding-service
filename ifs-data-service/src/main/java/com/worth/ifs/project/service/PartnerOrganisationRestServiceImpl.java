package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.partnerOrganisationResourceList;

@Service
public class PartnerOrganisationRestServiceImpl extends BaseRestService implements PartnerOrganisationRestService {
    private String projectRestURL = "/project";

    public RestResult<List<PartnerOrganisationResource>> getProjectPartnerOrganisations(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/partner-organisation", partnerOrganisationResourceList());
    }
}
