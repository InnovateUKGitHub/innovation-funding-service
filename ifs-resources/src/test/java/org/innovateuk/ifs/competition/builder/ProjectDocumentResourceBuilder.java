package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder;

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

    public ProjectDocumentResourceBuilder withTitle(String... titles) {
        return withArray((title, projectDocumentResource) -> setField("title", title, projectDocumentResource), titles);
    }

    public ProjectDocumentResourceBuilder withGuidance(String... guidances) {
        return withArray((guidance, projectDocumentResource) -> setField("guidance", guidance, projectDocumentResource), guidances);
    }

    public ProjectDocumentResourceBuilder withPdf(Boolean... pdfs) {
        return withArray((pdf, projectDocumentResource) -> setField("pdf", pdf, projectDocumentResource), pdfs);
    }
}

