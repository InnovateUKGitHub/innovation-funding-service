package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.util.List;

public interface ApplicationProcurementMilestoneService extends ProcurementMilestoneService<ApplicationProcurementMilestoneResource> {

    ServiceResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId);

}
