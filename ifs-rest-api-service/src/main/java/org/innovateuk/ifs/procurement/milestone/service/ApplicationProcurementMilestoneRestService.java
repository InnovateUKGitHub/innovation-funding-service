package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.util.List;

public interface ApplicationProcurementMilestoneRestService extends ProcurementMilestoneRestService<ApplicationProcurementMilestoneResource> {
    RestResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId);
}
