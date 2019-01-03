package org.innovateuk.ifs.project.documents.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.core.populator.BasicDetailsPopulator;
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
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UNSET;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UPLOADED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentsPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private DocumentsPopulator populator;

    @Mock
    private BasicDetailsPopulator basicDetailsPopulator;

    @Mock
    private ProjectService projectService;


    private long projectId = 1L;
    private String projectName = "Project 12";

    private long documentConfigId1 = 11L;
    private long documentConfigId2 = 12L;
    private long collaborationAgreementId = 13L;
    private String documentConfigTitle1 = "Risk Register";
    private String documentConfigTitle2 = "Plan Document";
    private String documentConfigGuidance1 = "Guidance Risk Register";
    private String documentConfigGuidance2 = "Guidance Plan Document";
    private String collaborationAgreement = COLLABORATION_AGREEMENT_TITLE;

    private UserResource user;

    @Before
    public void setup() {

        super.setup();
        long competitionId = 12L;

        user = UserResourceBuilder.newUserResource().build();

        List<CompetitionDocumentResource> configuredProjectDocuments = CompetitionDocumentResourceBuilder
                .neCompetitionDocumentResource()
                .withId(documentConfigId1, documentConfigId2, collaborationAgreementId)
                .withTitle(documentConfigTitle1, documentConfigTitle2, collaborationAgreement)
                .withGuidance(documentConfigGuidance1, documentConfigGuidance2)
                .build(3);

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
                .withCompetitionDocument(configuredProjectDocuments.get(0))
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
        OrganisationResource partnerOrganisationResource = newOrganisationResource().build();

        when(basicDetailsPopulator.populate(projectId)).thenReturn(basicDetails);
        when(projectService.isProjectManager(user.getId(), projectId)).thenReturn(true);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(singletonList(partnerOrganisationResource));

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

    @Test
    public void populateAllDocumentsWithMultiplePartnerOrganisation() {
        List<OrganisationResource> partnerOrganisations = newOrganisationResource()
                .withName("Organisation1", "Organisation2", "Organisation3", "Organisation4")
                .build(4);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);

        AllDocumentsViewModel viewModel = populator.populateAllDocuments(projectId, user);

        assertEquals(3, viewModel.getDocuments().size());

        verify(projectService).getPartnerOrganisationsForProject(projectId);
    }

    @Test
    public void populateAllDocumentsWithNoPartnerOrganisation() {
        List<OrganisationResource> partnerOrganisations = newOrganisationResource()
                .withName("abc")
                .build(1);
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);

        AllDocumentsViewModel viewModel = populator.populateAllDocuments(projectId, user);

        assertEquals(2, viewModel.getDocuments().size());

        verify(projectService).getPartnerOrganisationsForProject(projectId);
    }
}
