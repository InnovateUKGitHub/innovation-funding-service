package org.innovateuk.ifs.assessment.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.invite.domain.CompetitionInviteStatistics;
import org.innovateuk.ifs.invite.resource.CompetitionInviteStatisticsResource;
import org.mapstruct.Mapper;

import static org.mapstruct.ReportingPolicy.WARN;

/**
 * Mapper between {@link CompetitionInviteStatistics} and {@link CompetitionInviteStatisticsResource}
 */
@Mapper(config = GlobalMapperConfig.class, unmappedTargetPolicy = WARN)
public abstract class CompetitionInviteStatisticsMapper extends BaseMapper<CompetitionInviteStatistics, CompetitionInviteStatisticsResource, Long> {

}
