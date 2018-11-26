package org.innovateuk.ifs.project.documents.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectDocumentResourceBuilder extends BaseBuilder<ProjectDocumentResource, ProjectDocumentResourceBuilder> {

    private ProjectDocumentResourceBuilder(List<BiConsumer<Integer, ProjectDocumentResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectDocumentResourceBuilder newProjectResource() {
        return new ProjectDocumentResourceBuilder(emptyList()).
                with(uniqueIds());
    }

    @Override
    protected ProjectDocumentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProjectDocumentResource>> actions) {
        return new ProjectDocumentResourceBuilder(actions);
    }

    @Override
    protected ProjectDocumentResource createInitial() {
        return new ProjectDocumentResource();
    }

    public ProjectDocumentResourceBuilder withId(Long... ids) {
        return withArray((id, project) -> setField("id", id, project), ids);
    }

    public ProjectDocumentResourceBuilder withProjectDocument(org.innovateuk.ifs.competition.resource.ProjectDocumentResource... projectDocumentResources){
        return withArray((projectConfigDocumentResource, projectDocumentResource) -> projectDocumentResource.setProjectDocument(projectConfigDocumentResource), projectDocumentResources);
    }

    public ProjectDocumentResourceBuilder withStatus(DocumentStatus... statuses){
        return withArray((status, projectDocumentResource) -> projectDocumentResource.setStatus(status), statuses);
    }
}

