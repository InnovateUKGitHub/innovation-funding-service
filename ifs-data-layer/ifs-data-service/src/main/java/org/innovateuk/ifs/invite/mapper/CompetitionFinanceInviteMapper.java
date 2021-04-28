package org.innovateuk.ifs.invite.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.competition.domain.ExternalFinanceInvite;
import org.innovateuk.ifs.invite.resource.CompetitionFinanceInviteResource;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CompetitionFinanceInviteMapper extends BaseMapper<ExternalFinanceInvite, CompetitionFinanceInviteResource, Long> {
}