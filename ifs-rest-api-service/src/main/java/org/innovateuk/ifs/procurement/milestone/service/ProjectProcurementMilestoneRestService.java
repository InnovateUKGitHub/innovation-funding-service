package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;

import java.util.List;

public interface ProjectProcurementMilestoneRestService extends ProcurementMilestoneRestService<ProjectProcurementMilestoneResource> {
    RestResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);
    RestResult<List<ProjectProcurementMilestoneResource>> getByProjectId(long projectId);
}
