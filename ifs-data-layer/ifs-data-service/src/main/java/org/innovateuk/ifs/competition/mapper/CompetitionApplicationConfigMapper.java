package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionApplicationConfig;
import org.innovateuk.ifs.competition.resource.CompetitionApplicationConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionApplicationConfigMapper extends BaseResourceMapper<CompetitionApplicationConfig, CompetitionApplicationConfigResource> {

    @Override
    public abstract CompetitionApplicationConfigResource mapToResource(CompetitionApplicationConfig domain);
}