package org.innovateuk.ifs.project.monitoringofficer.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.monitoringofficer.domain.LegacyMonitoringOfficer;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class
        }
)
public abstract class LegacyMonitoringOfficerMapper extends BaseMapper<LegacyMonitoringOfficer, LegacyMonitoringOfficerResource, Long>{

    @Override
    public abstract LegacyMonitoringOfficerResource mapToResource(LegacyMonitoringOfficer monitoringOfficer);

    @Override
    public abstract LegacyMonitoringOfficer mapToDomain(LegacyMonitoringOfficerResource monitoringOfficerResource);


    public Long mapMonitoringOfficerToId(LegacyMonitoringOfficer object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }


}
