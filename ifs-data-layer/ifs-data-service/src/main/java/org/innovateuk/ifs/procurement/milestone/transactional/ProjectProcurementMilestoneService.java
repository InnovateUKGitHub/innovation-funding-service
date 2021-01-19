package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneId;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProjectProcurementMilestoneService extends ProcurementMilestoneService<PaymentMilestoneResource, ProjectProcurementMilestoneId> {

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<PaymentMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'READ_OVERVIEW')")
    ServiceResult<List<PaymentMilestoneResource>> getByProjectId(long projectId);
}
