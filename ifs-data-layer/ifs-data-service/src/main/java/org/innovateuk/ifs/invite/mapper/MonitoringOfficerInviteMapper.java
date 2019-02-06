package org.innovateuk.ifs.invite.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class MonitoringOfficerInviteMapper extends BaseMapper<MonitoringOfficerInvite, MonitoringOfficerInviteResource, Long> {

}