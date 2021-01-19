package org.innovateuk.ifs.procurement.milestone.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.procurement.milestone.domain.ProjectProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.resource.PaymentMilestoneResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class ProjectProcurementMilestoneMapper extends ProcurementMilestoneMapper<ProjectProcurementMilestone, PaymentMilestoneResource> {

    @Mappings({
            @Mapping(source = "projectFinance.project.id", target = "projectId"),
            @Mapping(source = "projectFinance.organisation.id", target = "organisationId")
    })
    public abstract PaymentMilestoneResource mapToResource(ProjectProcurementMilestone domain);
}
