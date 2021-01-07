package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;

import java.util.List;

public interface ProjectProcurementMilestoneService extends ProcurementMilestoneService<ProjectProcurementMilestoneResource, ProjectProcurementMilestoneId> {

//    @PreAuthorize("hasAuthority('project_finance')")
//    @SecuredBySpring(value = "VIEW", securedType = ProjectProcurementMilestoneResource.class, description = "Project finance user should be able to view any project milestones")
    ServiceResult<List<ProjectProcurementMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);

}
