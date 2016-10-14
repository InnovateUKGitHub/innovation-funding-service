package com.worth.ifs.project;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.project.service.PartnerOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerOrganisationServiceImpl implements PartnerOrganisationService {
    @Autowired
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Override
    public ServiceResult<List<PartnerOrganisationResource>> getPartnerOrganisations(Long projectId) {
        return partnerOrganisationRestService.getProjectPartnerOrganisations(projectId).toServiceResult();
    }
}
