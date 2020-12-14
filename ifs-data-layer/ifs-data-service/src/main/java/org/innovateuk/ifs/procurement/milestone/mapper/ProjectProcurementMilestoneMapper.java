package org.innovateuk.ifs.procurement.milestone.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class ProjectProcurementMilestoneMapper extends ProcurementMilestoneMapper<ProjectProcurementMilestone, ProjectProcurementMilestoneResource> {
}
