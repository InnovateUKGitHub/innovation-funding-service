package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ProjectDocumentResourceBuilder extends BaseBuilder<ProjectDocumentResource, ProjectDocumentResourceBuilder> {

    private ProjectDocumentResourceBuilder(List<BiConsumer<Integer, ProjectDocumentResource>> multiActions) {
        super(multiActions);
    }

    public static ProjectDocumentResourceBuilder newProjectDocumentResource() {
        return new ProjectDocumentResourceBuilder(emptyList()).with(uniqueIds());
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
        return withArray((id, projectDocumentResource) -> setField("id", id, projectDocumentResource), ids);
    }

    public ProjectDocumentResourceBuilder withCompetition(Long... competitionIds) {
        return withArray((competitionId, projectDocumentResource) -> setField("competition", competitionId, projectDocumentResource), competitionIds);
    }

    public ProjectDocumentResourceBuilder withTitle(String... titles) {
        return withArray((title, projectDocumentResource) -> setField("title", title, projectDocumentResource), titles);
    }

    public ProjectDocumentResourceBuilder withGuidance(String... guidances) {
        return withArray((guidance, projectDocumentResource) -> setField("guidance", guidance, projectDocumentResource), guidances);
    }

    public ProjectDocumentResourceBuilder withEditable(Boolean... editableFlags) {
        return withArray((editable, projectDocumentResource) -> setField("editable", editable, projectDocumentResource), editableFlags);
    }

    public ProjectDocumentResourceBuilder withEnabled(Boolean... enabledFlags) {
        return withArray((enabled, projectDocumentResource) -> setField("enabled", enabled, projectDocumentResource), enabledFlags);
    }


    public ProjectDocumentResourceBuilder withFileType(List<Long>... fileTypesLists) {
        return withArray((fileTypes, projectDocumentResource) -> setField("fileTypes", fileTypes, projectDocumentResource), fileTypesLists);
    }
}

