package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.domain.CompetitionThirdPartyConfig;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionThirdPartyConfigMapper extends BaseResourceMapper<CompetitionThirdPartyConfig, CompetitionThirdPartyConfigResource> {

    @Override
    public abstract CompetitionThirdPartyConfigResource mapToResource(CompetitionThirdPartyConfig domain);
}
