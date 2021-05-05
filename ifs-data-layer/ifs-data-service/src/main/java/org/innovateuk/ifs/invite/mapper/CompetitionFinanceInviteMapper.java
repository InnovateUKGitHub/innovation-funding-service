package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.competition.domain.ExternalFinanceInvite;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CompetitionFinanceInviteMapper extends BaseMapper<ExternalFinanceInvite, CompetitionFinanceInviteResource, Long> {
}