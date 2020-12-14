package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;

public interface ProcurementMilestoneService<R extends ProcurementMilestoneResource> {

    ServiceResult<R> create(R milestone);

    ServiceResult<R> update(R milestone);

    ServiceResult<Void> delete(long milestoneId);

    ServiceResult<R> get(long milestoneId);

}
