package org.innovateuk.ifs.competitionsetup.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.file.mapper.FileTypeMapper;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class,
                FileTypeMapper.class
        }
)
public abstract class CompetitionDocumentMapper extends BaseMapper<CompetitionDocument, CompetitionDocumentResource, Long> {

    @Override
    public abstract CompetitionDocumentResource mapToResource(CompetitionDocument domain);

    @Override
    public abstract CompetitionDocument mapToDomain(CompetitionDocumentResource resource);

    public Long mapCompetitionDocumentToId(CompetitionDocument object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}