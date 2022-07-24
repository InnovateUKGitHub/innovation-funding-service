package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class,
                FileTypeMapper.class
        }
)
public abstract class CompetitionEoiEvidenceConfigMapper extends BaseMapper<CompetitionEoiEvidenceConfig, CompetitionEoiEvidenceConfigResource, Long>  {

    @Mappings({
            @Mapping(source = "competition.id", target = "competitionId")
    })
    @Override
    public abstract CompetitionEoiEvidenceConfigResource mapToResource(CompetitionEoiEvidenceConfig domain);

    @Mappings({
            @Mapping(source = "competitionId", target = "competition")
    })
    @Override
    public abstract CompetitionEoiEvidenceConfig mapToDomain(CompetitionEoiEvidenceConfigResource resource);

    public Long mapCompetitionEoiEvidenceConfigToId(CompetitionEoiEvidenceConfig object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public CompetitionEoiEvidenceConfig build() {
        return createDefault(CompetitionEoiEvidenceConfig.class);
    }
}
