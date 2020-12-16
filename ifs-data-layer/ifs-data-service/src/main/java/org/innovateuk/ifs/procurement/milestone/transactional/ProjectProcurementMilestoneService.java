package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectProcurementMilestoneService extends ProcurementMilestoneService<ProjectProcurementMilestoneResource> {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);

}
