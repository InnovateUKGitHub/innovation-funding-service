package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProcurementMilestoneService<R extends ProcurementMilestoneResource> {

    @PreAuthorize("hasPermission(#resource, 'EDIT')")
    ServiceResult<R> create(R resource);

    @PreAuthorize("hasPermission(#resource, 'EDIT')")
    ServiceResult<R> update(R resource);

    @PreAuthorize("hasPermission(#milestoneId, 'org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource', 'EDIT')")
    ServiceResult<Void> delete(ProcurementMilestoneId milestoneId);

    @PreAuthorize("hasPermission(#milestoneId, 'org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource', 'VIEW')")
    ServiceResult<R> get(ProcurementMilestoneId milestoneId);

}
