package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneId;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectProcurementMilestoneService extends ProcurementMilestoneService<ProjectProcurementMilestoneResource, ProjectProcurementMilestoneId> {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectId(long projectId);
}
