package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AssessmentPeriodServiceTest {

    @InjectMocks
    private AssessmentPeriodService assessmentPeriodService;

    @Mock
    private AssessmentPeriodRestService assessmentPeriodRestService;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Test
    public void shouldGiveNullNameIfOnlyPeriodForCompetition() {
        // given
        long competitionId = 123L;
        long assessmentPeriodId = 101L;

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource().build();
        given(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId)).willReturn(restSuccess(singletonList(assessmentPeriodResource)));

        // when
        String result = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId, competitionId);

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

        List<AssessmentPeriodResource> assessmentPeriodResources = newAssessmentPeriodResource().build(3);
        given(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId)).willReturn(restSuccess(assessmentPeriodResources));

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

        List<AssessmentPeriodResource> assessmentPeriodResources = newAssessmentPeriodResource().build(3);
        given(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId)).willReturn(restSuccess(assessmentPeriodResources));

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

        List<AssessmentPeriodResource> assessmentPeriodResources = newAssessmentPeriodResource().build(3);
        given(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId)).willReturn(restSuccess(assessmentPeriodResources));

        // when
        String result = assessmentPeriodService.assessmentPeriodName(assessmentPeriodId2, competitionId);

        // then
        assertThat(result).isEqualTo("Assessment period 2: 6 February to 6 February 1987");
    }

}
