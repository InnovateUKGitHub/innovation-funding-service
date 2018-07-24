package org.innovateuk.ifs.competitionsetup.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competitionsetup.domain.ProjectDocument;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                CompetitionMapper.class,
        }
)
public abstract class ProjectDocumentMapper extends BaseMapper<ProjectDocument, ProjectDocumentResource, Long> {

    @Override
    public abstract ProjectDocumentResource mapToResource(ProjectDocument domain);

    @Override
    public abstract ProjectDocument mapToDomain(ProjectDocumentResource resource);

    public Long mapProjectDocumentToId(ProjectDocument object) {
        if (object == null) {
            return null;
        }
        return object.getId();
    }
}
