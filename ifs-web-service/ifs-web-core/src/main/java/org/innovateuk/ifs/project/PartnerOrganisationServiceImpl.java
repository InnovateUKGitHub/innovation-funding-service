package org.innovateuk.ifs.project;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
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
