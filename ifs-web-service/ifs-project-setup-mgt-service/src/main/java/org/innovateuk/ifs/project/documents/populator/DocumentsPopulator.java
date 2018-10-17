package org.innovateuk.ifs.project.documents.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentStatus;
import org.innovateuk.ifs.project.documents.viewmodel.AllDocumentsViewModel;
import org.innovateuk.ifs.project.resource.BasicDetails;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindAny;

@Component
public class DocumentsPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    public AllDocumentsViewModel populateAllDocuments(long projectId) {

        BasicDetails basicDetails = populateBasicDetails(projectId);
        ProjectResource project = basicDetails.getProject();
        CompetitionResource competition = basicDetails.getCompetition();

        List<org.innovateuk.ifs.competition.resource.ProjectDocumentResource> configuredProjectDocuments = competition.getProjectDocuments();
        List<ProjectDocumentResource> projectDocuments = project.getProjectDocuments();

        List<ProjectDocumentStatus> documents = new ArrayList<>();

        for (org.innovateuk.ifs.competition.resource.ProjectDocumentResource configuredDocument : configuredProjectDocuments) {
            documents.add(new ProjectDocumentStatus(configuredDocument.getId(),
                    configuredDocument.getTitle(),
                    getProjectDocumentStatus(projectDocuments, configuredDocument.getId())));
        }

        return new AllDocumentsViewModel(projectId, project.getName(), competition.getId(), basicDetails.getApplication().getId(), documents);
    }

    private BasicDetails populateBasicDetails(long projectId) {

        ProjectResource project = projectService.getById(projectId);

        ApplicationResource application = applicationService.getById(project.getApplication());

        CompetitionResource competition = competitionRestService.getCompetitionById(application.getCompetition()).getSuccess();

        return new BasicDetails(project, application, competition);

    }

    private DocumentStatus getProjectDocumentStatus(List<ProjectDocumentResource> projectDocuments, Long documentConfigId) {

        return simpleFindAny(projectDocuments, projectDocumentResource -> projectDocumentResource.getProjectDocument().getId().equals(documentConfigId))
                .map(projectDocumentResource -> projectDocumentResource.getStatus())
                .orElse(DocumentStatus.UNSET);
    }
}
