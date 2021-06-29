package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.SUBMITTED;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.sections.SectionStatus.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerDashboardViewModelPopulatorTest {

    @InjectMocks
    private MonitoringOfficerDashboardViewModelPopulator populator;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Mock
    private MonitoringOfficerSummaryViewModelPopulator monitoringOfficerSummaryViewModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private SetupSectionStatus setupSectionStatus;

    @Mock
    private FilterDocumentsPopulator filterDocumentsPopulator;

    private UserResource user;
    private CompetitionResource competition;
    private CompetitionDocumentResource competitionDocument;
    private List<ProjectResource> projectResourceList = new ArrayList<>();

    @Before
    public void setUp() {
        user = newUserResource().build();
        List<ProjectSetupStage> setUpStages = asList(PROJECT_DETAILS, PROJECT_TEAM, DOCUMENTS, MONITORING_OFFICER, BANK_DETAILS, FINANCE_CHECKS, SPEND_PROFILE, GRANT_OFFER_LETTER, PROJECT_SETUP_COMPLETE);
        competitionDocument = newCompetitionDocumentResource()
                .withCompetition(9L)
                .withTitle("Exploitation Plan")
                .build();
        competition = newCompetitionResource()
                .withId(9L)
                .withProjectSetupStages(setUpStages)
                .withProjectDocument(singletonList(competitionDocument))
                .build();
    }

    @Test
    public void populate() {
        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withProjectState(ProjectState.UNSUCCESSFUL)
                .build();

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = Mockito.mock(MonitoringOfficerSummaryViewModel.class);

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId())).thenReturn(restSuccess(singletonList(project)));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(monitoringOfficerSummaryViewModelPopulator.populate(anyList())).thenReturn(monitoringOfficerSummaryViewModel);
        when(setupSectionStatus.documentsSectionStatus(false, project, competition, true)).thenReturn(EMPTY);
        when(filterDocumentsPopulator.hasDocumentSection(project)).thenReturn(false);

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user);
        assertEquals(1, viewModel.getProjects().size());

        assertEquals((long) project.getId(), viewModel.getProjects().get(0).getProjectId());
        assertEquals(project.getApplication(), viewModel.getProjects().get(0).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(0).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", project.getId()), viewModel.getProjects().get(0).getLinkUrl());
        assertEquals("Project name", viewModel.getProjects().get(0).getProjectTitle());
        assertTrue(viewModel.getProjects().get(0).isUnsuccessful());
        assertFalse(viewModel.getProjects().get(0).isLiveOrCompletedOffline());
        assertFalse(viewModel.getProjects().get(0).isWithdrawn());
    }

    @Test
    public void populateApplyFilterAndSorting() {
        ProjectResource projectResourceInSetup = newProjectResource()
                .withId(88L)
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withMonitoringOfficerUser(user.getId())
                .withProjectState(ProjectState.SETUP)
                .withCollaborativeProject(false)
                .withMonitoringOfficerUser(user.getId())
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument)
                        .withStatus(SUBMITTED)
                        .build()))
                .build();
        ProjectResource projectResourceInLive = newProjectResource()
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withMonitoringOfficerUser(user.getId())
                .withCollaborativeProject(false)
                .withProjectState(ProjectState.LIVE)
                .withMonitoringOfficerUser(user.getId())
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument)
                        .withStatus(APPROVED)
                        .build()))
                .build();
        projectResourceList.add(projectResourceInSetup);
        projectResourceList.add(projectResourceInLive);

        when(monitoringOfficerRestService.filterProjectsForMonitoringOfficer(user.getId(), true, true))
                .thenReturn(restSuccess(projectResourceList));

        when(competitionRestService.getCompetitionById(projectResourceList.get(0).getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(0).getId())).thenReturn(restSuccess(competition));
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(0), competition, true)).thenReturn(MO_ACTION_REQUIRED);

        when(competitionRestService.getCompetitionById(projectResourceList.get(1).getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(1).getId())).thenReturn(restSuccess(competition));
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(1), competition, true)).thenReturn(TICK);

        when(filterDocumentsPopulator.hasDocumentSection(projectResourceList.get(1))).thenReturn(true);
        when(filterDocumentsPopulator.hasDocumentSection(projectResourceList.get(0))).thenReturn(true);
        when(filterDocumentsPopulator.getProjectsWithDocumentsComplete(projectResourceList)).thenReturn(singletonList(projectResourceList.get(1)));
        when(filterDocumentsPopulator.getProjectsWithDocumentsInComplete(projectResourceList)).thenReturn(null);
        when(filterDocumentsPopulator.getProjectsWithDocumentsAwaitingReview(projectResourceList)).thenReturn(singletonList(projectResourceList.get(0)));

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = new MonitoringOfficerSummaryViewModel(1, 1, 1, 0, 1);

        when(monitoringOfficerSummaryViewModelPopulator.populate(user)).thenReturn(monitoringOfficerSummaryViewModel);

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user, true, true, true, false, true);
        assertEquals(2, viewModel.getProjects().size());

        assertEquals((long) projectResourceInSetup.getId(), viewModel.getProjects().get(0).getProjectId());
        assertEquals(projectResourceInSetup.getApplication(), viewModel.getProjects().get(0).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(0).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResourceInSetup.getId()), viewModel.getProjects().get(0).getLinkUrl());
        assertEquals("Project name", viewModel.getProjects().get(0).getProjectTitle());
        assertEquals(ProjectState.SETUP, viewModel.getProjects().get(0).getProjectState());
        assertTrue(viewModel.getProjects().get(0).getDocumentSectionViewModel().isHasDocumentSection());
        assertEquals("mo-action-required", viewModel.getProjects().get(0).getDocumentSectionViewModel().getDocumentSectionStatus());

        assertEquals((long) projectResourceInLive.getId(), viewModel.getProjects().get(1).getProjectId());
        assertEquals(projectResourceInLive.getApplication(), viewModel.getProjects().get(1).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(1).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResourceInLive.getId()), viewModel.getProjects().get(1).getLinkUrl());
        assertEquals("Project name", viewModel.getProjects().get(1).getProjectTitle());
        assertEquals(ProjectState.LIVE, viewModel.getProjects().get(1).getProjectState());
        assertTrue(viewModel.getProjects().get(1).getDocumentSectionViewModel().isHasDocumentSection());
        assertEquals("complete", viewModel.getProjects().get(1).getDocumentSectionViewModel().getDocumentSectionStatus());
    }
}
