package org.innovateuk.ifs.procurement.milestone.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneId;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ApplicationProcurementMilestoneService extends ProcurementMilestoneService<ApplicationProcurementMilestoneResource, ApplicationProcurementMilestoneId> {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ_FINANCE_TOTALS')")
    ServiceResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId);

    @NotSecured(value = "Must be secured by other services.")
    ServiceResult<Boolean> arePaymentMilestonesEqualToFunding(long applicationId, long organisationId);
}
