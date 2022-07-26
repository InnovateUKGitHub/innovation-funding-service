package org.innovateuk.ifs.competition.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.domain.CompetitionEoiDocument;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionEoiEvidenceConfigMapper.class,
                FileTypeMapper.class
        }
)
public abstract class CompetitionEoiDocumentMapper extends BaseMapper<CompetitionEoiDocument, CompetitionEoiDocumentResource, Long> {

    @Mappings({
        @Mapping(source = "competitionEoiEvidenceConfig.id", target = "competitionEoiEvidenceConfigId"),
        @Mapping(source = "fileType.id", target = "fileTypeId")
    })
    @Override
    public abstract CompetitionEoiDocumentResource mapToResource(CompetitionEoiDocument domain);

    @Mappings({
        @Mapping(source = "competitionEoiEvidenceConfigId", target = "competitionEoiEvidenceConfig"),
        @Mapping(source = "fileTypeId", target = "fileType")
    })
    @Override
    public abstract CompetitionEoiDocument mapToDomain(CompetitionEoiDocumentResource resource);

    public Long mapCompetitionEoiDocumentToId(CompetitionEoiDocument object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }

    public CompetitionEoiDocument build() {
        return createDefault(CompetitionEoiDocument.class);
    }
}
