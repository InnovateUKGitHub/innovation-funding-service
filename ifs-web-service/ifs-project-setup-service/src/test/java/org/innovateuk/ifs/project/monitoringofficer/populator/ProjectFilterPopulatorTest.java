package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.internal.ProjectSetupStage;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.status.populator.SetupSectionStatus;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder.newCompetitionDocumentResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.document.resource.DocumentStatus.*;
import static org.innovateuk.ifs.project.documents.builder.ProjectDocumentResourceBuilder.newProjectDocumentResource;
import static org.innovateuk.ifs.project.internal.ProjectSetupStage.*;
import static org.innovateuk.ifs.sections.SectionStatus.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFilterPopulatorTest {

    @InjectMocks
    private ProjectFilterPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private SetupSectionStatus setupSectionStatus;

    private final List<ProjectResource> projectResourceList = new ArrayList<>();

    @Before
    public void setup() {
        UserResource user = newUserResource().build();
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
                .withApplication(21L)
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
        ProjectResource projectResourceWithDocumentsComplete = newProjectResource()
                .withId(89L)
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(15L)
                .withName("Project name")
                .withMonitoringOfficerUser(user.getId())
                .withProjectState(ProjectState.SETUP)
                .withCollaborativeProject(false)
                .withMonitoringOfficerUser(user.getId())
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument)
                        .withStatus(APPROVED)
                        .build()))
                .build();
        ProjectResource projectResourceWithDocumentsInComplete = newProjectResource()
                .withId(87L)
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(12L)
                .withName("Project name")
                .withMonitoringOfficerUser(user.getId())
                .withProjectState(ProjectState.SETUP)
                .withCollaborativeProject(false)
                .withMonitoringOfficerUser(user.getId())
                .withProjectDocuments(singletonList(newProjectDocumentResource()
                        .withProject(88L)
                        .withCompetitionDocument(competitionDocument)
                        .withStatus(UNSET)
                        .build()))
                .build();
        ProjectResource projectResourceWithDocumentsAwaitingReview = newProjectResource()
                .withId(86L)
                .withCompetition(competition.getId())
                .withCompetitionName("Competition name")
                .withApplication(8L)
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
                .withId(53L)
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
        projectResourceList.add(projectResourceWithDocumentsComplete);
        projectResourceList.add(projectResourceWithDocumentsInComplete);
        projectResourceList.add(projectResourceWithDocumentsAwaitingReview);

        when(competitionRestService.getCompetitionById(projectResourceList.get(0).getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionById(projectResourceList.get(1).getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionById(projectResourceList.get(2).getCompetition())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionById(projectResourceList.get(3).getCompetition())).thenReturn(restSuccess(competition));

        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(0), competition, true)).thenReturn(MO_ACTION_REQUIRED);
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(3), competition, true)).thenReturn(MO_ACTION_REQUIRED);
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(1), competition, true)).thenReturn(TICK);
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(4), competition, true)).thenReturn(TICK);
        when(setupSectionStatus.documentsSectionStatus(false, projectResourceList.get(2), competition, true)).thenReturn(INCOMPLETE);

        when(competitionRestService.getCompetitionForProject(projectResourceList.get(0).getId())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(1).getId())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(2).getId())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(3).getId())).thenReturn(restSuccess(competition));
        when(competitionRestService.getCompetitionForProject(projectResourceList.get(4).getId())).thenReturn(restSuccess(competition));
    }

    @Test
    public void getInSetupProjects() {
        List<ProjectResource> projectResource = populator.getInSetupProjects(projectResourceList);
        assertEquals(4, projectResource.size());
    }

    @Test
    public void getPreviousProject() {
        List<ProjectResource> projectResource = populator.getPreviousProject(projectResourceList);
        assertEquals(1, projectResource.size());
    }

    @Test
    public void getProjectsWithDocumentsComplete() {
        List<ProjectResource> projectResource = populator.getProjectsWithDocumentsComplete(projectResourceList);
        assertEquals(2, projectResource.size());
    }

    @Test
    public void getProjectsWithDocumentsInComplete() {
        List<ProjectResource> projectResource = populator.getProjectsWithDocumentsInComplete(projectResourceList);
        assertEquals(1, projectResource.size());
    }

    @Test
    public void getProjectsWithDocumentsAwaitingReview() {
        List<ProjectResource> projectResource = populator.getProjectsWithDocumentsAwaitingReview(projectResourceList);
        assertEquals(2, projectResource.size());
    }

    @Test
    public void hasDocumentSection() {
        assertTrue(populator.hasDocumentSection(projectResourceList.get(0)));
    }
}