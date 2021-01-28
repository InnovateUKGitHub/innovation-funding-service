package org.innovateuk.ifs.procurement.milestone.repository;

import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;

import java.util.List;

public interface ApplicationProcurementMilestoneRepository extends ProcurementMilestoneRepository<ApplicationProcurementMilestone> {

    List<ApplicationProcurementMilestone> findByApplicationFinanceApplicationIdAndApplicationFinanceOrganisationIdOrderByMonthAsc(long applicationId, long organisationId);

    void deleteByApplicationFinanceId(long applicationFinanceId);
}
