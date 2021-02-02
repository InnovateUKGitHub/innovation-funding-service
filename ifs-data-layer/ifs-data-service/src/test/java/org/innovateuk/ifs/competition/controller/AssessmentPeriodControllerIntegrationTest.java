package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.*;

public class AssessmentPeriodControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentPeriodController> {

    private static final Long COMPETITION_ID_VALID = 1L;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private MilestoneController milestoneController;

    @Before
    public void setLoggedInUserOnThread() {
        loginCompAdmin();
    }

    @Test
    public void testNewAssessmentPeriod() {
        RestResult<List<MilestoneResource>> result = controller.newAssessmentPeriod(COMPETITION_ID_VALID);
        assertTrue(result.isSuccess());
        List<MilestoneResource> milestones = result.getSuccess();
        assertNotNull(milestones);
        assertEquals(3, milestones.size());
    }

    @Test
    public void testUpdateAssessmentPeriodMilestones() {
        Competition newCompetition = competitionRepository.save(newCompetition().withId((Long) null).build());

        List<MilestoneResource> milestones = getMilestonesForCompetition(newCompetition.getId());
        assertNotNull(milestones);
        assertTrue(milestones.isEmpty());

        RestResult<Void> result = controller.updateAssessmentPeriodMilestones(milestones);
        assertNotNull(result.getSuccess());

    }

    @Override
    @Autowired
    protected void setControllerUnderTest(AssessmentPeriodController controller) {
        this.controller = controller;
    }

    private List<MilestoneResource> getMilestonesForCompetition(Long competitionId) {
        RestResult<List<MilestoneResource>> milestoneResult = milestoneController.getAllMilestonesByCompetitionId(competitionId);
        assertTrue(milestoneResult.isSuccess());
        return milestoneResult.getSuccess();
    }
}