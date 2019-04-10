package org.innovateuk.ifs.project.resource;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;

public class DocumentsResource {

    private ProjectDocumentResource projectDocumentResource;

    private CompetitionDocumentResource competitionDocumentResource;

    public DocumentsResource(ProjectDocumentResource projectDocumentResource, CompetitionDocumentResource competitionDocumentResource) {
        this.projectDocumentResource = projectDocumentResource;
        this.competitionDocumentResource = competitionDocumentResource;
    }

    public ProjectDocumentResource getProjectDocumentResource() {
        return projectDocumentResource;
    }

    public void setProjectDocumentResource(ProjectDocumentResource projectDocumentResource) {
        this.projectDocumentResource = projectDocumentResource;
    }

    public CompetitionDocumentResource getCompetitionDocumentResource() {
        return competitionDocumentResource;
    }

    public void setCompetitionDocumentResource(CompetitionDocumentResource competitionDocumentResource) {
        this.competitionDocumentResource = competitionDocumentResource;
    }
}
