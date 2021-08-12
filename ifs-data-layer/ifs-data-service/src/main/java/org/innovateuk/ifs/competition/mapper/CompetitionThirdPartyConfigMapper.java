package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionThirdPartyConfig;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class
        }
)
public abstract class CompetitionThirdPartyConfigMapper extends BaseMapper<CompetitionThirdPartyConfig, CompetitionThirdPartyConfigResource, Long> {

    @Mappings({
            @Mapping(source = "competition.id", target = "competitionId")
    })
    @Override
    public abstract CompetitionThirdPartyConfigResource mapToResource(CompetitionThirdPartyConfig domain);

    @Mappings({
            @Mapping(source = "competitionId", target = "competition")
    })
    @Override
    public abstract CompetitionThirdPartyConfig mapToDomain(CompetitionThirdPartyConfigResource resource);

    public Long mapCompetitionThirdPartyConfigToId(CompetitionThirdPartyConfig object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public CompetitionThirdPartyConfig build() {
        return createDefault(CompetitionThirdPartyConfig.class);
    }
}
