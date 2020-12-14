package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;

import java.util.List;

public interface ProjectProcurementMilestoneService extends ProcurementMilestoneService<ProjectProcurementMilestoneResource> {

    ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);

}
