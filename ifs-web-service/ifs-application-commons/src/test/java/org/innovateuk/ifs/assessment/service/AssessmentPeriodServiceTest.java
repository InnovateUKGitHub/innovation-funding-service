package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentPeriodServiceTest {

    @InjectMocks
    private AssessmentPeriodService assessmentPeriodService;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Test
    public void shouldGiveNullForNullAssessmentPeriodId() {
        // when
        String result = assessmentPeriodService.assessmentPeriodName(null, 123L);

        // then
        assertThat(result).isNull();
    }

    @Test
    public void shouldGiveNameForPeriodWithMilestonesInDifferentYears() {
        // given
        long competitionId = 123L;
        long assessmentPeriodId1 = 101L;
        long assessmentPeriodId2 = 105L;
        long assessmentPeriodId3 = 108L;

        List<MilestoneResource> milestones = newMilestoneResource()
                .withAssessmentPeriod(assessmentPeriodId1, assessmentPeriodId2, assessmentPeriodId2, assessmentPeriodId3)
                .withDate(ZonedDateTime.of(1986, 1, 1, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1987, 2, 6, 10, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1988, 3, 10, 11, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1989, 4, 16, 12, 0, 0, 0, ZoneId.of("UTC")))
                .build(4);

        given(milestoneRestService.getAllMilestonesByCompetitionId(competitionId)).willReturn(restSuccess(milestones));

        // when
        String result = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId2, competitionId);

        // then
        assertThat(result).isEqualTo("Assessment period 2: 6 February 1987 to 10 March 1988");
    }

    @Test
    public void shouldGiveNameForPeriodWithMilestonesInSameYear() {
        // given
        long competitionId = 123L;
        long assessmentPeriodId1 = 101L;
        long assessmentPeriodId2 = 105L;
        long assessmentPeriodId3 = 108L;

        List<MilestoneResource> milestones = newMilestoneResource()
                .withAssessmentPeriod(assessmentPeriodId1, assessmentPeriodId2, assessmentPeriodId2, assessmentPeriodId3)
                .withDate(ZonedDateTime.of(1986, 1, 1, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1987, 2, 6, 10, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1987, 3, 10, 11, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1989, 4, 16, 12, 0, 0, 0, ZoneId.of("UTC")))
                .build(4);

        given(milestoneRestService.getAllMilestonesByCompetitionId(competitionId)).willReturn(restSuccess(milestones));

        // when
        String result = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId2, competitionId);

        // then
        assertThat(result).isEqualTo("Assessment period 2: 6 February to 10 March 1987");
    }

    @Test
    public void shouldGiveNameForPeriodWithSingleMilestone() {
        // given
        long competitionId = 123L;
        long assessmentPeriodId1 = 101L;
        long assessmentPeriodId2 = 105L;
        long assessmentPeriodId3 = 108L;

        List<MilestoneResource> milestones = newMilestoneResource()
                .withAssessmentPeriod(assessmentPeriodId1, assessmentPeriodId2, assessmentPeriodId3)
                .withDate(ZonedDateTime.of(1986, 1, 1, 9, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1987, 2, 6, 10, 0, 0, 0, ZoneId.of("UTC")),
                        ZonedDateTime.of(1989, 4, 16, 12, 0, 0, 0, ZoneId.of("UTC")))
                .build(3);

        given(milestoneRestService.getAllMilestonesByCompetitionId(competitionId)).willReturn(restSuccess(milestones));

        // when
        String result = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId2, competitionId);

        // then
        assertThat(result).isEqualTo("Assessment period 2: 6 February to 6 February 1987");
    }

}
