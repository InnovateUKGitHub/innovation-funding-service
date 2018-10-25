package org.innovateuk.ifs.project.documents.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.documents.domain.ProjectDocument;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link ProjectDocument}s.
 */
public class ProjectDocumentBuilder extends BaseBuilder<ProjectDocument, ProjectDocumentBuilder> {

    public static ProjectDocumentBuilder newProjectDocument() {
        return new ProjectDocumentBuilder(emptyList()).with(uniqueIds());
    }

    private ProjectDocumentBuilder(List<BiConsumer<Integer, ProjectDocument>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ProjectDocumentBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectDocument>> actions) {
        return new ProjectDocumentBuilder(actions);
    }

    @Override
    protected ProjectDocument createInitial() {
        return createDefault(ProjectDocument.class);
    }

    public ProjectDocumentBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public ProjectDocumentBuilder withProject(Project... projects) {
        return withArray((project, p) -> setField("project", project, p), projects);
    }

    public ProjectDocumentBuilder withProjectDocument(org.innovateuk.ifs.competitionsetup.domain.ProjectDocument... projectDocuments) {
        return withArray((projectDocument, p) -> setField("projectDocument", projectDocument, p), projectDocuments);
    }

    public ProjectDocumentBuilder withFileEntry(FileEntry... fileEntries) {
        return withArray((fileEntry, p) -> setField("fileEntry", fileEntry, p), fileEntries);
    }

    public ProjectDocumentBuilder withStatus(DocumentStatus... statuses) {
        return withArray((status, p) -> setField("status", status, p), statuses);
    }
}


