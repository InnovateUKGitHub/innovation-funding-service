package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.monitoring.service.MonitoringOfficerRestService;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerSummaryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
import static org.innovateuk.ifs.sections.SectionStatus.MO_ACTION_REQUIRED;
import static org.innovateuk.ifs.sections.SectionStatus.TICK;
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
    private CompetitionRestService competitionRestService;

    @Mock
    private SetupSectionStatus setupSectionStatus;

    private UserResource user;
    private ProjectResource projectResourceInSetup;
    private ProjectResource projectResourceInLive;

    @Before
    public void setUp() {
        user = newUserResource().build();
        List<ProjectSetupStage> setUpStages = asList(PROJECT_DETAILS, PROJECT_TEAM, DOCUMENTS, MONITORING_OFFICER, BANK_DETAILS, FINANCE_CHECKS, SPEND_PROFILE, GRANT_OFFER_LETTER, PROJECT_SETUP_COMPLETE);
        List<CompetitionDocumentResource> competitionDocument = singletonList(newCompetitionDocumentResource()
                .withCompetition(9L)
                .withTitle("Exploitation Plan")
                .build());
        CompetitionResource competition = newCompetitionResource()
                .withId(9L)
                .withProjectSetupStages(setUpStages)
                .withProjectDocument(competitionDocument)
                .build();
        projectResourceInSetup = newProjectResource()
                .withId(88L)
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withMonitoringOfficerUser(user.getId())
                .withProjectState(ProjectState.SETUP)
                .withCollaborativeProject(false)
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument.get(0))
                        .withStatus(SUBMITTED)
                        .build()))
                .build();
        projectResourceInLive = newProjectResource()
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(2L)
                .withName("Project name")
                .withMonitoringOfficerUser(user.getId())
                .withCollaborativeProject(false)
                .withProjectState(ProjectState.LIVE)
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument.get(0))
                        .withStatus(APPROVED)
                        .build()))
                .build();

        when(competitionRestService.getCompetitionById(projectResourceInSetup.getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceInSetup.getId())).thenReturn(restSuccess(competition));
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceInSetup, competition, true)).thenReturn(MO_ACTION_REQUIRED);

        when(competitionRestService.getCompetitionById(projectResourceInLive.getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceInLive.getId())).thenReturn(restSuccess(competition));
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceInLive, competition, true)).thenReturn(TICK);
    }

    @Test
    public void populateByProjects() {

        MonitoringOfficerSummaryViewModel viewModel = populator.populate(asList(projectResourceInSetup, projectResourceInLive));

        assertEquals(1, viewModel.getInSetupProjectCount());
        assertEquals(1, viewModel.getPreviousProjectCount());
        assertEquals(1, viewModel.getDocumentsAwaitingReviewCount());
        assertEquals(1, viewModel.getDocumentsCompleteCount());
        assertEquals(0, viewModel.getDocumentsIncompleteCount());
    }

    @Test
    public void populateByUser() {

        when(monitoringOfficerRestService.getProjectsForMonitoringOfficer(user.getId()))
                .thenReturn(restSuccess(asList(projectResourceInSetup, projectResourceInLive)));

        MonitoringOfficerSummaryViewModel viewModel = populator.populate(user);

        assertEquals(1, viewModel.getInSetupProjectCount());
        assertEquals(1, viewModel.getPreviousProjectCount());
        assertEquals(1, viewModel.getDocumentsAwaitingReviewCount());
        assertEquals(1, viewModel.getDocumentsCompleteCount());
        assertEquals(0, viewModel.getDocumentsIncompleteCount());
    }
}
