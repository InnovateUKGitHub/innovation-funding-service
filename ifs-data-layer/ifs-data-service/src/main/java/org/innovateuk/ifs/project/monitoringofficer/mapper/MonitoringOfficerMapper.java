package org.innovateuk.ifs.project.monitoringofficer.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.project.mapper.ProjectMapper;
import org.innovateuk.ifs.project.monitoringofficer.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class
        }
)
public abstract class MonitoringOfficerMapper extends BaseMapper<MonitoringOfficer, MonitoringOfficerResource, Long>{

    @Override
    public abstract MonitoringOfficerResource mapToResource(MonitoringOfficer monitoringOfficer);

    @Override
    public abstract MonitoringOfficer mapToDomain(MonitoringOfficerResource monitoringOfficerResource);


    public Long mapMonitoringOfficerToId(MonitoringOfficer object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }


}
