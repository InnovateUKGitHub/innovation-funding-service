package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static java.time.ZonedDateTime.now;
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
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerSummaryViewModelPopulatorTest {

    @InjectMocks
    private MonitoringOfficerSummaryViewModelPopulator populator;

    @Mock
    private MonitoringOfficerRestService monitoringOfficerRestService;

    @Mock
    private ProjectFilterPopulator projectFilterPopulator;

    private UserResource user;
    private List<ProjectResource> projectResourceList = new ArrayList<>();

    @Before
    public void setUp() {
        user = newUserResource().build();
        List<ProjectSetupStage> setUpStages = asList(PROJECT_DETAILS, PROJECT_TEAM, DOCUMENTS, MONITORING_OFFICER, BANK_DETAILS, FINANCE_CHECKS, SPEND_PROFILE, GRANT_OFFER_LETTER, PROJECT_SETUP_COMPLETE);
        CompetitionDocumentResource competitionDocument = newCompetitionDocumentResource()
                .withCompetition(9L)
                .withTitle("Exploitation Plan")
                .build();
        CompetitionResource competition = newCompetitionResource()
                .withId(9L)
                .withProjectSetupStages(setUpStages)
                .withProjectDocument(singletonList(competitionDocument))
                .build();
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
                .withSpendProfileGenerated(true)
                .withSpendProfileSubmittedDate(now())
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
                .withSpendProfileGenerated(true)
                .withSpendProfileSubmittedDate(now())
                .build();
        projectResourceList.add(projectResourceInSetup);
        projectResourceList.add(projectResourceInLive);

        ReflectionTestUtils.setField(populator, "moDashboardFilterEnabled", true);
        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId())).thenReturn(restSuccess(projectResourceList));

        when(projectFilterPopulator.getInSetupProjects(projectResourceList)).thenReturn(singletonList(projectResourceList.get(0)));
        when(projectFilterPopulator.getPreviousProject(projectResourceList)).thenReturn(singletonList(projectResourceList.get(1)));
    }

    @Test
    public void populateByProjects() {

        MonitoringOfficerSummaryViewModel viewModel = populator.populate(projectResourceList);

        assertEquals(1, viewModel.getInSetupProjectCount());
        assertEquals(1, viewModel.getPreviousProjectCount());
        assertEquals(0, viewModel.getDocumentsAwaitingReviewCount());
        assertEquals(0, viewModel.getDocumentsCompleteCount());
        assertEquals(0, viewModel.getDocumentsIncompleteCount());
        assertEquals(0, viewModel.getSpendProfileCompleteCount());
        assertEquals(0, viewModel.getSpendProfileIncompleteCount());
        assertEquals(0, viewModel.getSpendProfileAwaitingReviewCount());
    }

    @Test
    public void populateByUser() {

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()))
                .thenReturn(restSuccess(projectResourceList));

        MonitoringOfficerSummaryViewModel viewModel = populator.populate(user);

        assertEquals(1, viewModel.getInSetupProjectCount());
        assertEquals(1, viewModel.getPreviousProjectCount());
        assertEquals(0, viewModel.getDocumentsAwaitingReviewCount());
        assertEquals(0, viewModel.getDocumentsCompleteCount());
        assertEquals(0, viewModel.getDocumentsIncompleteCount());
        assertEquals(0, viewModel.getSpendProfileCompleteCount());
        assertEquals(0, viewModel.getSpendProfileIncompleteCount());
        assertEquals(0, viewModel.getSpendProfileAwaitingReviewCount());
    }
}
