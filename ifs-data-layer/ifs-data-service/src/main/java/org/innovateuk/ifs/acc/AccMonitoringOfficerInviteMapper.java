package org.innovateuk.ifs.acc;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.invite.resource.AccMonitoringOfficerInviteResource;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerInviteResource;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AccMonitoringOfficerInviteMapper extends BaseMapper<AccMonitoringOfficerInvite, AccMonitoringOfficerInviteResource, Long> {
}