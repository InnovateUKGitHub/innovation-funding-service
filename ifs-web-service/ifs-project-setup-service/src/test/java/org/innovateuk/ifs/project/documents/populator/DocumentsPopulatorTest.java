package org.innovateuk.ifs.project.documents.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.core.populator.BasicDetailsPopulator;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder;
import org.innovateuk.ifs.project.documents.viewmodel.AllDocumentsViewModel;
import org.innovateuk.ifs.project.documents.viewmodel.DocumentViewModel;
import org.innovateuk.ifs.project.resource.BasicDetails;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.clearUniqueIds;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UNSET;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UPLOADED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class DocumentsPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private DocumentsPopulator populator;

    @Mock
    private BasicDetailsPopulator basicDetailsPopulator;

    @Mock
    private ProjectService projectService;


    private Long projectId = 1L;
    private String projectName = "Project 12";

    private Long documentConfigId1 = 11L;
    private Long documentConfigId2 = 12L;
    private String documentConfigTitle1 = "Risk Register";
    private String documentConfigTitle2 = "Plan Document";
    private String documentConfigGuidance1 = "Guidance Risk Register";
    private String documentConfigGuidance2 = "Guidance Plan Document";

    private UserResource user;

    @Before
    public void setup() {

        super.setup();

        projectId = 1L;
        projectName = "Project 12";
        Long competitionId = 12L;

        documentConfigId1 = 11L;
        documentConfigId2 = 12L;
        documentConfigTitle1 = "Risk Register";
        documentConfigTitle2 = "Plan Document";


        user = UserResourceBuilder.newUserResource().build();

        List<org.innovateuk.ifs.competition.resource.ProjectDocumentResource> configuredProjectDocuments = org.innovateuk.ifs.competition.builder.ProjectDocumentResourceBuilder
                .newProjectDocumentResource()
                .withId(documentConfigId1, documentConfigId2)
                .withTitle(documentConfigTitle1, documentConfigTitle2)
                .withGuidance(documentConfigGuidance1, documentConfigGuidance2)
                .build(2);

        CompetitionResource competition = CompetitionResourceBuilder
                .newCompetitionResource()
                .withId(competitionId)
                .withProjectDocument(configuredProjectDocuments)
                .build();
        ApplicationResource application = ApplicationResourceBuilder
                .newApplicationResource()
                .withCompetition(competitionId)
                .build();

        ProjectDocumentResource projectDocumentResource = ProjectDocumentResourceBuilder
                .newProjectResource()
                .withProjectDocument(configuredProjectDocuments.get(0))
                .withStatus(UPLOADED)
                .build();

        ProjectResource project = ProjectResourceBuilder
                .newProjectResource()
                .withId(projectId)
                .withName(projectName)
                .withApplication(application)
                .withProjectDocuments(singletonList(projectDocumentResource))
                .build();

        BasicDetails basicDetails = new BasicDetails(project, application, competition);

        when(basicDetailsPopulator.populate(projectId)).thenReturn(basicDetails);
        when(projectService.isProjectManager(user.getId(), projectId)).thenReturn(true);
    }

    @Test
    public void populateAllDocuments() {

        AllDocumentsViewModel viewModel = populator.populateAllDocuments(projectId, user);

        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(projectName, viewModel.getProjectName());
        assertEquals(2, viewModel.getDocuments().size());
        assertEquals(documentConfigTitle1, viewModel.getDocuments().get(0).getTitle());
        assertEquals(UPLOADED, viewModel.getDocuments().get(0).getStatus());
        assertEquals(documentConfigTitle2, viewModel.getDocuments().get(1).getTitle());
        assertEquals(UNSET, viewModel.getDocuments().get(1).getStatus());
        assertTrue(viewModel.isProjectManager());
    }

    @Test
    public void populateViewDocument() {

        DocumentViewModel viewModel = populator.populateViewDocument(projectId, documentConfigId1, user);

        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(projectName, viewModel.getProjectName());
        assertEquals(documentConfigId1, viewModel.getDocumentConfigId());
        assertEquals(documentConfigTitle1, viewModel.getTitle());
        assertEquals(documentConfigGuidance1, viewModel.getGuidance());
        assertNull(viewModel.getFileDetails());
        assertEquals(UPLOADED, viewModel.getStatus());
        assertTrue(viewModel.isProjectManager());
    }
}
