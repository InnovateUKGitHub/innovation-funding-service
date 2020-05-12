package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseResourceMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionOrganisationConfig;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public abstract class CompetitionOrganisationConfigMapper extends BaseResourceMapper<CompetitionOrganisationConfig, CompetitionOrganisationConfigResource> {

    @Override
    public abstract CompetitionOrganisationConfigResource mapToResource(CompetitionOrganisationConfig domain);
}