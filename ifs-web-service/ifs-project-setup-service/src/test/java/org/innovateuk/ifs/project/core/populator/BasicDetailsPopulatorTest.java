package org.innovateuk.ifs.project.core.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.ProjectResourceBuilder;
import org.innovateuk.ifs.project.resource.BasicDetails;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class BasicDetailsPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private BasicDetailsPopulator populator;

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populate() {

        long projectId = 1L;
        long competitionId = 12L;

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().withId(competitionId).build();
        ApplicationResource application = ApplicationResourceBuilder
                .newApplicationResource()
                .withCompetition(competitionId)
                .build();
        ProjectResource project = ProjectResourceBuilder
                .newProjectResource()
                .withId(projectId)
                .withApplication(application)
                .build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(applicationService.getById(project.getApplication())).thenReturn(application);
        when(competitionRestService.getCompetitionById(application.getCompetition())).thenReturn(restSuccess(competition));
        BasicDetails basicDetails = populator.populate(projectId);

        assertEquals(project, basicDetails.getProject());
        assertEquals(application, basicDetails.getApplication());
        assertEquals(competition, basicDetails.getCompetition());
    }
}
