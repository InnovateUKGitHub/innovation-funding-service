package org.innovateuk.ifs.project.documents.mapper;

import org.innovateuk.ifs.commons.mapper.BaseMapper;
import org.innovateuk.ifs.commons.mapper.GlobalMapperConfig;
import org.innovateuk.ifs.competitionsetup.mapper.CompetitionDocumentMapper;
import org.innovateuk.ifs.file.mapper.FileEntryMapper;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import org.mapstruct.Mapper;

@Mapper(
        config = GlobalMapperConfig.class,
        uses = {
                ProjectMapper.class,
                CompetitionDocumentMapper.class,
                FileEntryMapper.class
        }
)
public abstract class ProjectDocumentsMapper extends BaseMapper<ProjectDocument, ProjectDocumentResource, Long> {

    @Override
    public abstract ProjectDocumentResource mapToResource(ProjectDocument projectDocument);

    @Override
    public abstract ProjectDocument mapToDomain(ProjectDocumentResource projectDocumentResource);
}