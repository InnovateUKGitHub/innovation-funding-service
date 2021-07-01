package org.innovateuk.ifs.procurement.milestone.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.procurement.milestone.mapper.ProjectProcurementMilestoneMapper;
import org.innovateuk.ifs.procurement.milestone.repository.ProjectProcurementMilestoneRepository;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@PermissionEntityLookupStrategies
public class ProjectProcurementMilestoneLookupStrategy {

    @Autowired
    private ProjectProcurementMilestoneRepository projectProcurementMilestoneRepository;

    @Autowired
    private ProjectProcurementMilestoneMapper projectProcurementMilestoneMapper;

    @PermissionEntityLookupStrategy
    public ProcurementMilestoneResource get(final ProjectProcurementMilestoneId id) {
        return projectProcurementMilestoneMapper.mapToResource(projectProcurementMilestoneRepository.findById(id.getId()).orElse(null));
    }
}
