package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorPageResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.ApplicationStatus.CREATED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;

public class ApplicationAssessmentSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationAssessmentSummaryController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(ApplicationAssessmentSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        loginCompAdmin();
        ApplicationAssessorPageResource applicationAssessorResources = controller
                .getAvailableAssessors(1L, 0, 20, null)
                .getSuccessObjectOrThrowException();

        assertEquals(Collections.emptyList(), applicationAssessorResources.getContent());
    }
    @Test
    public void getAssignedAssessors() throws Exception {
        loginCompAdmin();
        List<ApplicationAssessorResource> applicationAssessorResources = controller
                .getAssignedAssessors(1L)
                .getSuccessObjectOrThrowException();

        assertEquals(Collections.emptyList(), applicationAssessorResources);
    }


    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Competition competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .withName("Connected digital additive manufacturing")
                .build());

        Application application = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("Progressive machines")
                .withApplicationStatus(CREATED)
                .build();
        application.getApplicationProcess().setActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.CREATED));

        applicationRepository.save(application);


        flushAndClearSession();

        loginCompAdmin();

        ApplicationAssessmentSummaryResource applicationAssessmentSummary = controller
                .getApplicationAssessmentSummary(application.getId())
                .getSuccessObjectOrThrowException();

        assertEquals(application.getId().longValue(), applicationAssessmentSummary.getId());
        assertEquals(application.getName(), applicationAssessmentSummary.getName());
        assertEquals(competition.getId(), applicationAssessmentSummary.getCompetitionId());
        assertEquals(competition.getName(), applicationAssessmentSummary.getCompetitionName());
    }
}