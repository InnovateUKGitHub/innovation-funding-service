package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionOrganisationConfigMapper extends BaseMapper<CompetitionOrganisationConfig, CompetitionOrganisationConfigResource, Long> {

    @Override
    public abstract CompetitionOrganisationConfigResource mapToResource(CompetitionOrganisationConfig domain);
}