package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ApplicationProcurementMilestoneService extends ProcurementMilestoneService<ApplicationProcurementMilestoneResource, ApplicationProcurementMilestoneId> {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    ServiceResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId);

}
