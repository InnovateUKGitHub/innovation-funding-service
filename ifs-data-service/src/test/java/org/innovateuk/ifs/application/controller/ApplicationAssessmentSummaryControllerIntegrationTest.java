package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApplicationAssessmentSummaryControllerIntegrationTest extends BaseControllerIntegrationTest<ApplicationAssessmentSummaryController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(ApplicationAssessmentSummaryController controller) {
        this.controller = controller;
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Competition competition = competitionRepository.save(newCompetition()
                .with(id(null))
                .withName("Connected digital additive manufacturing")
                .build());

        Application application = applicationRepository.save(newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withName("Progressive machines")
                .build());

        flushAndClearSession();

        loginCompAdmin();

        RestResult<ApplicationAssessmentSummaryResource> serviceResult = controller.getApplicationAssessmentSummary(application.getId());
        assertTrue(serviceResult.isSuccess());

        ApplicationAssessmentSummaryResource applicationAssessmentSummary = serviceResult.getSuccessObjectOrThrowException();

        assertEquals(application.getId(), applicationAssessmentSummary.getId());
        assertEquals(application.getName(), applicationAssessmentSummary.getName());
        assertEquals(competition.getId(), applicationAssessmentSummary.getCompetitionId());
        assertEquals(competition.getName(), applicationAssessmentSummary.getCompetitionName());
    }
}