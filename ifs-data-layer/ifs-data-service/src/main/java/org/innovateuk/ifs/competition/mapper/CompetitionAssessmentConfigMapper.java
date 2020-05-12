package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionAssessmentConfig;
import org.innovateuk.ifs.competition.resource.CompetitionAssessmentConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionAssessmentConfigMapper extends BaseResourceMapper<CompetitionAssessmentConfig, CompetitionAssessmentConfigResource> {

    @Override
    public abstract CompetitionAssessmentConfigResource mapToResource(CompetitionAssessmentConfig domain);
}
