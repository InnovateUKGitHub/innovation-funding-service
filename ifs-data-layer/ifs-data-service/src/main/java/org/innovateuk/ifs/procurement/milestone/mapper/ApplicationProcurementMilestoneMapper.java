package org.innovateuk.ifs.procurement.milestone.mapper;

import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.procurement.milestone.domain.ApplicationProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
    config = GlobalMapperConfig.class,
    uses = {
    }
)
public abstract class ApplicationProcurementMilestoneMapper extends ProcurementMilestoneMapper<ApplicationProcurementMilestone, ApplicationProcurementMilestoneResource> {

    @Mappings({
            @Mapping(source = "applicationFinance.application.id", target = "applicationId"),
            @Mapping(source = "applicationFinance.organisation.id", target = "organisationId")
    })
    public abstract ApplicationProcurementMilestoneResource mapToResource(ApplicationProcurementMilestone domain);
}
