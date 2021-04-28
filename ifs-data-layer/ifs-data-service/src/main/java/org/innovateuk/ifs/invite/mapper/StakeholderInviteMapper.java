package org.innovateuk.ifs.invite.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class StakeholderInviteMapper extends BaseMapper<StakeholderInvite, StakeholderInviteResource, Long> {

}