package org.innovateuk.ifs.procurement.milestone.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.mapstruct.Mapper;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class ApplicationProcurementMilestoneMapper extends ProcurementMilestoneMapper<ApplicationProcurementMilestone, ApplicationProcurementMilestoneResource> {
}
