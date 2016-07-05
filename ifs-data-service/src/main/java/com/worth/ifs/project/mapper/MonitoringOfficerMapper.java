package com.worth.ifs.project.mapper;

import com.worth.ifs.commons.mapper.BaseMapper;
import com.worth.ifs.commons.mapper.GlobalMapperConfig;
import com.worth.ifs.project.domain.MonitoringOfficer;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import org.mapstruct.Mapper;

/**
 * Created by bronnyl on 6/27/16.
 */
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
