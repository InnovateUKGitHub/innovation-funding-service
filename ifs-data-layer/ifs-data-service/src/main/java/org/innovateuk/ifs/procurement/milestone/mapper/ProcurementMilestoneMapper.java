package org.innovateuk.ifs.procurement.milestone.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.procurement.milestone.domain.ProcurementMilestone;
import org.innovateuk.ifs.procurement.milestone.resource.ProcurementMilestoneResource;
import org.mapstruct.Mapper;

public abstract class ProcurementMilestoneMapper<D extends ProcurementMilestone, R extends ProcurementMilestoneResource> extends BaseResourceMapper<D, R> {
}
