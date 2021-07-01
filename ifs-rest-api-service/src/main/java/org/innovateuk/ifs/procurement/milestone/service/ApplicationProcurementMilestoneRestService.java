package org.innovateuk.ifs.procurement.milestone.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;

import java.util.List;
import java.util.Optional;

public interface ApplicationProcurementMilestoneRestService extends ProcurementMilestoneRestService<ApplicationProcurementMilestoneResource> {
    RestResult<List<ApplicationProcurementMilestoneResource>> getByApplicationIdAndOrganisationId(long applicationId, long organisationId);

    RestResult<Optional<Integer>> findMaxByApplicationId(long applicationId);
}
