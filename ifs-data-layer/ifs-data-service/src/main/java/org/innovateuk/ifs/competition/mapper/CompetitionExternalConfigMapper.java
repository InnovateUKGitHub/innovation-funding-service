package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionExternalConfig;
import org.innovateuk.ifs.competition.resource.CompetitionExternalConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionExternalConfigMapper extends BaseResourceMapper<CompetitionExternalConfig, CompetitionExternalConfigResource> {
    @Override
    public abstract CompetitionExternalConfigResource mapToResource(CompetitionExternalConfig domain);
}