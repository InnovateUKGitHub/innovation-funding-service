package org.innovateuk.ifs.project.monitoring.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.project.monitoring.domain.ProjectMonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
import org.mapstruct.Mapper;

@Mapper
public abstract class ProjectMonitoringOfficerMapper extends BaseMapper<ProjectMonitoringOfficer, ProjectMonitoringOfficerResource, Long> {
//    @Mappings({
//            @Mapping(target = "affiliations", ignore = true),
//    })
    @Override
    public abstract ProjectMonitoringOfficerResource mapToResource(ProjectMonitoringOfficer domain);

//    @Mappings({
//            @Mapping(target = "id", ignore = true),
//            @Mapping(target = "agreement", ignore = true),
//            @Mapping(target = "agreementSignedDate", ignore = true)
//    })
    @Override
    public abstract ProjectMonitoringOfficer mapToDomain(ProjectMonitoringOfficerResource resource);
}