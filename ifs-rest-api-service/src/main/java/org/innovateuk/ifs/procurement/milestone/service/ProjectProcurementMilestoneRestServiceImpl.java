package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectProcurementMilestoneRestServiceImpl
        extends BaseProcurementMilestoneRestServiceImpl<ProjectProcurementMilestoneResource>
        implements ProjectProcurementMilestoneRestService {

    protected ProjectProcurementMilestoneRestServiceImpl() {
        super("/project-procurement-milestone");
    }

    @Override
    public RestResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId) {
        return getWithRestResult(getMilestoneUrl() + String.format("/project/%d/organisation/%d", projectId, organisationId),
                new ParameterizedTypeReference<List<ProjectProcurementMilestoneResource>>() {});
    }

    @Override
    protected Class<ProjectProcurementMilestoneResource> getResourceClass() {
        return ProjectProcurementMilestoneResource.class;
    }
}
