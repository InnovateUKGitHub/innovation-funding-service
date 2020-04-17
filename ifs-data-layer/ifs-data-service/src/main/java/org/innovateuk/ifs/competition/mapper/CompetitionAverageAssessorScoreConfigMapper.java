package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionAverageAssessorScoreConfig;
import org.innovateuk.ifs.competition.resource.CompetitionAverageAssessorScoreConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionAverageAssessorScoreConfigMapper extends BaseResourceMapper<CompetitionAverageAssessorScoreConfig, CompetitionAverageAssessorScoreConfigResource> {

    @Override
    public abstract CompetitionAverageAssessorScoreConfigResource mapToResource(CompetitionAverageAssessorScoreConfig domain);
}
