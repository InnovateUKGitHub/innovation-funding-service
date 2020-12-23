package org.innovateuk.ifs.procurement.milestone.repository;

import org.innovateuk.ifs.procurement.milestone.domain.ProcurementMilestone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ProcurementMilestoneRepository<D extends ProcurementMilestone> extends CrudRepository<D, Long> {
}
