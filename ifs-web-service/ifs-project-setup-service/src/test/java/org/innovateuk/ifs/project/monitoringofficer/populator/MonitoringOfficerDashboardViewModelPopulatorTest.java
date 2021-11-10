package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerDashboardPageResource;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.form.MonitoringOfficerDashboardForm;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerDashboardViewModel;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.spendprofile.service.SpendProfileRestService;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.APPROVED;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.SUBMITTED;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.sections.SectionStatus.MO_ACTION_REQUIRED;
import static org.innovateuk.ifs.sections.SectionStatus.TICK;
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
    private ProjectFilterPopulator projectFilterPopulator;

    @Mock
    private ProjectService projectService;

    @Mock
    private SpendProfileRestService spendProfileRestService;

    private UserResource user;
    private CompetitionResource competition;
    private CompetitionDocumentResource competitionDocument;
    private final List<ProjectResource> projectResourceList = new ArrayList<>();

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
        when(monitoringOfficerSummaryViewModelPopulator.populate(anyList())).thenReturn(monitoringOfficerSummaryViewModel);

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
        ReflectionTestUtils.setField(populator, "isMOJourneyUpdateEnabled", true);
        ReflectionTestUtils.setField(populator, "isMOSpendProfileUpdateEnabled", true);
        ReflectionTestUtils.setField(populator, "moDashboardFilterEnabled", true);

        ProjectResource projectResourceInSetup = newProjectResource()
                .withId(88L)
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name 1")
                .withMonitoringOfficerUser(user.getId())
                .withProjectState(ProjectState.SETUP)
                .withCollaborativeProject(false)
                .withMonitoringOfficerUser(user.getId())
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument)
                        .withStatus(SUBMITTED)
                        .build()))
                .withSpendProfileGenerated(true)
                .withSpendProfileSubmittedDate(now())
                .build();

        ProjectResource projectResourceInLive = newProjectResource()
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name 2")
                .withMonitoringOfficerUser(user.getId())
                .withCollaborativeProject(false)
                .withProjectState(ProjectState.LIVE)
                .withMonitoringOfficerUser(user.getId())
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument)
                        .withStatus(APPROVED)
                        .build()))
                .withSpendProfileGenerated(true)
                .withSpendProfileSubmittedDate(now())
                .build();
        projectResourceList.add(projectResourceInSetup);
        projectResourceList.add(projectResourceInLive);

        MonitoringOfficerDashboardPageResource monitoringOfficerDashboardPageResource = new MonitoringOfficerDashboardPageResource();
        monitoringOfficerDashboardPageResource.setContent(projectResourceList);
        monitoringOfficerDashboardPageResource.setNumber(0);
        monitoringOfficerDashboardPageResource.setSize(10);
        monitoringOfficerDashboardPageResource.setTotalElements(projectResourceList.size());
        monitoringOfficerDashboardPageResource.setTotalPages(1);

        MonitoringOfficerDashboardForm monitoringOfficerDashboardForm = new MonitoringOfficerDashboardForm();
        monitoringOfficerDashboardForm.setKeywordSearch("Competition name");
        monitoringOfficerDashboardForm.setProjectInSetup(true);
        monitoringOfficerDashboardForm.setPreviousProject(true);
        monitoringOfficerDashboardForm.setDocumentsComplete(true);
        monitoringOfficerDashboardForm.setDocumentsIncomplete(false);
        monitoringOfficerDashboardForm.setDocumentsAwaitingReview(true);
        monitoringOfficerDashboardForm.setSpendProfileComplete(true);
        monitoringOfficerDashboardForm.setSpendProfileIncomplete(false);
        monitoringOfficerDashboardForm.setSpendProfileAwaitingReview(false);

        when(monitoringOfficerRestService.filterProjectsForMonitoringOfficer(user.getId(), 0, 10, "Competition name", true, true))
                .thenReturn(restSuccess(monitoringOfficerDashboardPageResource));
        when(projectFilterPopulator.getProjectsWithDocumentsComplete(projectResourceList)).thenReturn(singletonList(projectResourceList.get(1)));
        when(projectFilterPopulator.getProjectsWithDocumentsInComplete(projectResourceList)).thenReturn(emptyList());
        when(projectFilterPopulator.getProjectsWithDocumentsAwaitingReview(projectResourceList)).thenReturn(singletonList(projectResourceList.get(0)));
        when(projectFilterPopulator.getProjectsWithSpendProfileComplete(anyList())).thenReturn(projectResourceList);

        MonitoringOfficerSummaryViewModel monitoringOfficerSummaryViewModel = new MonitoringOfficerSummaryViewModel(1, 1, 1, 0, 1, 2, 0, 0);
        when(monitoringOfficerSummaryViewModelPopulator.populate(user)).thenReturn(monitoringOfficerSummaryViewModel);

        when(projectFilterPopulator.hasDocumentSection(projectResourceList.get(1))).thenReturn(true);
        when(projectFilterPopulator.hasDocumentSection(projectResourceList.get(0))).thenReturn(true);

        when(competitionRestService.getCompetitionForProject(projectResourceList.get(1).getId())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(0).getId())).thenReturn(restSuccess(competition));

        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(0), competition, true)).thenReturn(MO_ACTION_REQUIRED);
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(1), competition, true)).thenReturn(TICK);

        when(projectFilterPopulator.getSpendProfileSectionStatus(projectResourceList.get(0))).thenReturn(TICK);
        when(projectFilterPopulator.getSpendProfileSectionStatus(projectResourceList.get(1))).thenReturn(TICK);
        when(projectService.getLeadOrganisation(projectResourceList.get(0).getId())).thenReturn(newOrganisationResource().build());
        when(projectService.getLeadOrganisation(projectResourceList.get(1).getId())).thenReturn(newOrganisationResource().build());

        when(projectFilterPopulator.hasSpendProfileSection(projectResourceList.get(0))).thenReturn(true);
        when(projectFilterPopulator.hasSpendProfileSection(projectResourceList.get(1))).thenReturn(true);

        MonitoringOfficerDashboardViewModel viewModel = populator.populate(user, monitoringOfficerDashboardForm, 0, 10);

        assertEquals(2, viewModel.getProjects().size());

        assertEquals((long) projectResourceInSetup.getId(), viewModel.getProjects().get(0).getProjectId());
        assertEquals(projectResourceInSetup.getApplication(), viewModel.getProjects().get(0).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(0).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResourceInSetup.getId()), viewModel.getProjects().get(0).getLinkUrl());
        assertEquals("Project name 1", viewModel.getProjects().get(0).getProjectTitle());
        assertEquals(ProjectState.SETUP, viewModel.getProjects().get(0).getProjectState());
        assertTrue(viewModel.getProjects().get(0).getMonitoringDashboardSectionsViewModel().getDocumentSectionViewModel().isHasDocumentSection());
        assertEquals("mo-action-required", viewModel.getProjects().get(0).getMonitoringDashboardSectionsViewModel().getDocumentSectionViewModel().getDocumentSectionStatus());

        assertEquals((long) projectResourceInLive.getId(), viewModel.getProjects().get(1).getProjectId());
        assertEquals(projectResourceInLive.getApplication(), viewModel.getProjects().get(1).getApplicationNumber());
        assertEquals("Competition name", viewModel.getProjects().get(1).getCompetitionTitle());
        assertEquals(String.format("/project-setup/project/%d", projectResourceInLive.getId()), viewModel.getProjects().get(1).getLinkUrl());
        assertEquals("Project name 2", viewModel.getProjects().get(1).getProjectTitle());
        assertEquals(ProjectState.LIVE, viewModel.getProjects().get(1).getProjectState());
        assertTrue(viewModel.getProjects().get(1).getMonitoringDashboardSectionsViewModel().getDocumentSectionViewModel().isHasDocumentSection());
        assertEquals("complete", viewModel.getProjects().get(1).getMonitoringDashboardSectionsViewModel().getDocumentSectionViewModel().getDocumentSectionStatus());

        assertTrue(viewModel.getProjects().get(0).getMonitoringDashboardSectionsViewModel().getSpendProfileSectionViewModel().isHasSpendProfileSection());
        assertTrue(viewModel.getProjects().get(1).getMonitoringDashboardSectionsViewModel().getSpendProfileSectionViewModel().isHasSpendProfileSection());
        assertEquals(TICK.getStatus(), viewModel.getProjects().get(0).getMonitoringDashboardSectionsViewModel().getSpendProfileSectionViewModel().getSpendProfileStatus());
        assertEquals(TICK.getStatus(), viewModel.getProjects().get(1).getMonitoringDashboardSectionsViewModel().getSpendProfileSectionViewModel().getSpendProfileStatus());    }
}
