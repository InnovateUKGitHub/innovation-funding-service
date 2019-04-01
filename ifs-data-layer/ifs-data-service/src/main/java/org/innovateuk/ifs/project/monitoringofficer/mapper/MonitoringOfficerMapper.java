package org.innovateuk.ifs.project.monitoringofficer.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class
        }
)
public abstract class MonitoringOfficerMapper extends BaseMapper<MonitoringOfficer, LegacyMonitoringOfficerResource, Long>{

    @Override
    public abstract LegacyMonitoringOfficerResource mapToResource(MonitoringOfficer monitoringOfficer);

    @Override
    public abstract MonitoringOfficer mapToDomain(LegacyMonitoringOfficerResource monitoringOfficerResource);


    public Long mapMonitoringOfficerToId(MonitoringOfficer object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }


}
