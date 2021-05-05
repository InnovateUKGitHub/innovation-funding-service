package org.innovateuk.ifs.invite.mapper;


import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class StakeholderInviteMapper extends BaseMapper<StakeholderInvite, StakeholderInviteResource, Long> {

}