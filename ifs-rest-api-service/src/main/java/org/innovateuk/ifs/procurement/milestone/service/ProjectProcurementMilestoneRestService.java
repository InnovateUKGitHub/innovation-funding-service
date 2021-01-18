package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;

import java.util.List;

public interface ProjectProcurementMilestoneRestService extends ProcurementMilestoneRestService<PaymentMilestoneResource> {
    RestResult<List<PaymentMilestoneResource>> getByProjectIdAndOrganisationId(long projectId, long organisationId);
    RestResult<List<PaymentMilestoneResource>> getByProjectId(long projectId);
}
