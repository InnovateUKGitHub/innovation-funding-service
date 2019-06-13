package org.innovateuk.ifs.documents;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.documents.populator.DocumentsPopulator;
import org.innovateuk.ifs.documents.viewModel.AllDocumentsViewModel;
import org.innovateuk.ifs.documents.viewModel.DocumentViewModel;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentResource;
import org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionDocumentResource.COLLABORATION_AGREEMENT_TITLE;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UNSET;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.UPLOADED;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentsPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private DocumentsPopulator populator;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    private long competitionId = 18L;
    private long applicationId = 19L;

    private long projectId = 1L;
    private long loggedInUserId = 2L;
    private String projectName = "Project 12";

    private long documentConfigId1 = 11L;
    private long documentConfigId2 = 12L;
    private long collaborationAgreementId = 13L;
    private String documentConfigTitle1 = "Risk Register";
    private String documentConfigTitle2 = "Plan Document";
    private String documentConfigGuidance1 = "Guidance Risk Register";
    private String documentConfigGuidance2 = "Guidance Plan Document";
    private String collaborationAgreement = COLLABORATION_AGREEMENT_TITLE;

    @Before
    public void setup() {

        super.setup();

        List<CompetitionDocumentResource> configuredProjectDocuments = CompetitionDocumentResourceBuilder
                .newCompetitionDocumentResource()
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
                .withId(applicationId)
                .withCompetition(competitionId)
                .build();

        ProjectDocumentResource projectDocumentResource = ProjectDocumentResourceBuilder
                .newProjectDocumentResource()
                .withCompetitionDocument(configuredProjectDocuments.get(0))
                .withStatus(UPLOADED)
                .build();

        ProjectUserResource projectUserResource = newProjectUserResource()
                .withUser(loggedInUserId)
                .build();

        PartnerOrganisationResource partnerOrganisationResource = newPartnerOrganisationResource().build();

        ProjectResource project = ProjectResourceBuilder
                .newProjectResource()
                .withId(projectId)
                .withName(projectName)
                .withProjectState(SETUP)
                .withCompetition(competitionId)
                .withApplication(application)
                .withProjectDocuments(singletonList(projectDocumentResource))
                .build();

        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(singletonList(partnerOrganisationResource)));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(projectRestService.getProjectManager(projectId)).thenReturn(restSuccess(projectUserResource));

    }

    @Test
    public void populateAllDocuments() {

        AllDocumentsViewModel viewModel = populator.populateAllDocuments(projectId, loggedInUserId);

        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(applicationId, viewModel.getApplicationId());
        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(projectName, viewModel.getProjectName());
        assertEquals(2, viewModel.getDocuments().size());
        assertEquals(documentConfigTitle1, viewModel.getDocuments().get(0).getTitle());
        assertEquals(UPLOADED, viewModel.getDocuments().get(0).getStatus());
        assertEquals(documentConfigTitle2, viewModel.getDocuments().get(1).getTitle());
        assertEquals(UNSET, viewModel.getDocuments().get(1).getStatus());
    }

    @Test
    public void populateViewDocument() {

        DocumentViewModel viewModel = populator.populateViewDocument(projectId, loggedInUserId, documentConfigId1);

        assertEquals(projectId, viewModel.getProjectId());
        assertEquals(projectName, viewModel.getProjectName());
        assertEquals(applicationId, viewModel.getApplicationId());
        assertEquals(documentConfigId1, viewModel.getDocumentConfigId());
        assertEquals(documentConfigTitle1, viewModel.getTitle());
        assertNull(viewModel.getFileDetails());
        assertEquals(UPLOADED, viewModel.getStatus());
        assertEquals("", viewModel.getStatusComments());
    }

    @Test
    public void populateAllDocumentsWithMultiplePartnerOrganisation() {
        List<PartnerOrganisationResource> partnerOrganisationResource = newPartnerOrganisationResource().build(4);

        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisationResource));

        AllDocumentsViewModel viewModel = populator.populateAllDocuments(projectId, loggedInUserId);

        assertEquals(3, viewModel.getDocuments().size());

        verify(partnerOrganisationRestService).getProjectPartnerOrganisations(projectId);
    }

    @Test
    public void populateAllDocumentsWithNoPartnerOrganisation() {
        PartnerOrganisationResource partnerOrganisationResource = newPartnerOrganisationResource().build();

        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(singletonList(partnerOrganisationResource)));

        AllDocumentsViewModel viewModel = populator.populateAllDocuments(projectId, loggedInUserId);

        assertEquals(2, viewModel.getDocuments().size());

        verify(partnerOrganisationRestService).getProjectPartnerOrganisations(projectId);
    }
}
