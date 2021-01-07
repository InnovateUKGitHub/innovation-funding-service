package org.innovateuk.ifs.procurement.milestone.repository;

import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;

import java.util.List;

public interface ApplicationProcurementMilestoneRepository extends ProcurementMilestoneRepository<ApplicationProcurementMilestone> {

    List<ApplicationProcurementMilestone> findByApplicationFinanceApplicationIdAndApplicationFinanceOrganisationId(long applicationId, long organisationId);

    List<ApplicationProcurementMilestone> findByApplicationFinanceApplicationId(long applicationId);
}
