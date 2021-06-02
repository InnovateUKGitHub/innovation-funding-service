package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;

public interface ProcurementMilestoneRestService<R extends ProcurementMilestoneResource> {

    RestResult<R> create(R resource);

    RestResult<Void> update(R resource);

    RestResult<Void> delete(long milestoneId);

    RestResult<R> get(long milestoneId);
}
