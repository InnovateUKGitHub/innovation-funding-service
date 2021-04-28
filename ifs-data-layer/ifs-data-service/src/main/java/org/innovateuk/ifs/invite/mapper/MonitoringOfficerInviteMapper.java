package org.innovateuk.ifs.invite.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MonitoringOfficerInviteMapper extends BaseMapper<MonitoringOfficerInvite, MonitoringOfficerInviteResource, Long> {

}