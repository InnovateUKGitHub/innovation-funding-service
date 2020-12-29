package org.innovateuk.ifs.procurement.milestone.repository;

import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;

import java.util.List;

public interface ProjectProcurementMilestoneRepository extends ProcurementMilestoneRepository<ProjectProcurementMilestone> {

    List<ProjectProcurementMilestone> findByProjectFinanceProjectIdAndProjectFinanceOrganisationId(long projectId, long organisationId);

    List<ProjectProcurementMilestone> findByProjectFinanceProjectId(long projectId);
}
