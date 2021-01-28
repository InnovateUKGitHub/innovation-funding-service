package org.innovateuk.ifs.management.assessmentperiod.service;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentPeriodServiceImplTest extends BaseUnitTest {

    @InjectMocks
    private AssessmentPeriodServiceImpl assessmentPeriodService;

    @Mock
    private MilestoneRestService milestoneRestService;

    private final List<MilestoneResource> allMilestones = new ArrayList<>();

    @Before
    public void setup() {
        List<MilestoneResource> assessmentPeriodMilestones = newMilestoneResource()
                .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE)
                .withDate(ZonedDateTime.now().plusYears(1))
                .withAssessmentPeriod(1L, 1L, 1L)
                .build(3);

        List<MilestoneResource> otherMilestones = newMilestoneResource()
                .withType(OPEN_DATE, BRIEFING_EVENT, REGISTRATION_DATE, SUBMISSION_DATE, PANEL_DATE)
                .withDate(ZonedDateTime.now().plusYears(1L))
                .build(5);

        allMilestones.addAll(assessmentPeriodMilestones);
        allMilestones.addAll(otherMilestones);

    }

    @Test
    public void testGetAssessmentPeriodsForOverview() {
        long competitionId = 1L;

        when(milestoneRestService.getAllMilestonesByCompetitionId(competitionId))
                .thenReturn(restSuccess(allMilestones));

        List<MilestonesForm> assessmentPeriodsForOverview =
                assessmentPeriodService.getAssessmentPeriodsForOverview(competitionId);

        assertThat(assessmentPeriodsForOverview, hasSize(1));
    }

    @Test
    public void testGetAssessmentPeriodMilestoneForms(){

    }

    @Test
    public void testExtractMilestoneResourcesFromForm(){

    }
}