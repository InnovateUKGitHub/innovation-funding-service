package org.innovateuk.ifs.management.competition.setup.core.service;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupMilestoneServiceImplTest {

	@InjectMocks
	private CompetitionSetupMilestoneServiceImpl service;

	@Mock
	private MilestoneRestService milestoneRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessmentPeriodRestService assessmentPeriodRestService;

	@Test
	public void testCreateMilestonesForCompetition() {
	    Long competitionId = 1L;
	    Long assessmentPeriodId = 2L;
	    Integer index = 1;

        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource()
                .withId(assessmentPeriodId).withIndex(index).withCompetitionId(competitionId).build();

        when(competitionRestService.getCompetitionById(competitionId))
                .thenReturn(restSuccess(newCompetitionResource()
                        .withId(competitionId).withAlwaysOpen(false).build()));

        when(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(competitionId))
                .thenReturn(restFailure(notFoundError(AssessmentPeriodResource.class, competitionId, index)))
                .thenReturn(restSuccess(Collections.singletonList(assessmentPeriodResource)))
                .thenReturn(restSuccess(Collections.singletonList(assessmentPeriodResource)));

        when(assessmentPeriodRestService.create(index, competitionId))
                .thenReturn(restSuccess(assessmentPeriodResource));

        when(milestoneRestService.create(any(MilestoneType.class), eq(competitionId)))
                .thenReturn(restSuccess(newMilestoneResource()
                        .with((integer, milestoneResource) -> milestoneResource.setType(MilestoneType.OPEN_DATE))
                        .build()));

        when(milestoneRestService.create(any(MilestoneType.class), eq(competitionId), eq(assessmentPeriodId)))
                .thenReturn(restSuccess(newMilestoneResource()
                        .with((integer, milestoneResource) -> milestoneResource.setType(MilestoneType.OPEN_DATE))
                        .build()));

		List<MilestoneResource> result = service.createMilestonesForIFSCompetition(competitionId).getSuccess();

        result.forEach(milestoneResource -> assertEquals(MilestoneType.OPEN_DATE, milestoneResource.getType()));

        int numberOfMilestonesExpected = Arrays.stream(MilestoneType.presetValues())
                .filter(milestoneType -> !milestoneType.isOnlyNonIfs()).collect(toList()).size();

        int numberOfAssessmentPeriodMilestonesExpected = MilestoneType.assessmentPeriodValues().size();

		assertEquals(numberOfMilestonesExpected, result.size());
		verify(milestoneRestService, times(numberOfMilestonesExpected-numberOfAssessmentPeriodMilestonesExpected)).create(any(MilestoneType.class), eq(competitionId));
        verify(competitionRestService, times(numberOfMilestonesExpected)).getCompetitionById(competitionId);
        verify(assessmentPeriodRestService, times(numberOfAssessmentPeriodMilestonesExpected)).getAssessmentPeriodByCompetitionId(competitionId);
        verify(assessmentPeriodRestService, times(1)).create(index, competitionId);
        verify(milestoneRestService, times(numberOfMilestonesExpected-numberOfAssessmentPeriodMilestonesExpected)).create(any(MilestoneType.class), eq(competitionId));
        verify(milestoneRestService, times(numberOfAssessmentPeriodMilestonesExpected)).create(any(MilestoneType.class), eq(competitionId), eq(assessmentPeriodId));
    }

    @Test
	public void testUpdateMilestonesForCompetition() {
        List<MilestoneResource> oldMilestones = singletonList(
                newMilestoneResource()
                .with(milestoneResource -> milestoneResource.setType(MilestoneType.SUBMISSION_DATE))
                .withDate(LocalDateTime.MAX.atZone(ZoneId.systemDefault()))
                .build());

        LinkedMap<String, GenericMilestoneRowForm> newMilestones = new LinkedMap<>();
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.SUBMISSION_DATE, LocalDateTime.MIN.atZone(ZoneId.systemDefault()));
        newMilestones.put(MilestoneType.SUBMISSION_DATE.name(), milestoneRowForm);

        when(milestoneRestService.updateMilestones(anyListOf(MilestoneResource.class))).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateMilestonesForCompetition(oldMilestones, newMilestones, 123L);

        assertTrue(result.isSuccess());
        GenericMilestoneRowForm newMilestone = newMilestones.get(MilestoneType.SUBMISSION_DATE.name());
        assertEquals(Integer.valueOf(LocalDate.MIN.getDayOfMonth()), newMilestone.getDay());
        assertEquals(Integer.valueOf(LocalDate.MIN.getMonthValue()), newMilestone.getMonth());
        assertEquals(Integer.valueOf(LocalDate.MIN.getYear()), newMilestone.getYear());
	}

    @Test
    public void validateMilestoneDatesTrue() {
        LinkedMap<String, GenericMilestoneRowForm> milestones = new LinkedMap<>();
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.SUBMISSION_DATE, LocalDateTime.MIN.atZone(ZoneId.systemDefault()));
        milestones.put(MilestoneType.SUBMISSION_DATE.name(), milestoneRowForm);

        List<Error> result = service.validateMilestoneDates(milestones);

        assertTrue(result.isEmpty());
    }

    @Test
    public void validateMilestoneDatesFalse() {
        LinkedMap<String, GenericMilestoneRowForm> milestones = new LinkedMap<>();
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.SUBMISSION_DATE, LocalDateTime.MAX.atZone(ZoneId.systemDefault()));
        milestones.put(MilestoneType.SUBMISSION_DATE.name(), milestoneRowForm);

        List<Error> result = service.validateMilestoneDates(milestones);

        assertTrue(!result.isEmpty());
    }

    @Test
    public void testisMilestoneDateValidTrue() {
        Boolean resultOne = service.isMilestoneDateValid(1, 1, 1);
        Boolean resultTwo = service.isMilestoneDateValid(1, 1, 2000);
        Boolean resultThree = service.isMilestoneDateValid(31, 12, 9999);

        assertTrue(resultOne);
        assertTrue(resultTwo);
        assertTrue(resultThree);
    }

    @Test
    public void testisMilestoneDateValidFalse() {
        Boolean resultOne = service.isMilestoneDateValid(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        Boolean resultTwo = service.isMilestoneDateValid(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        Boolean resultThree = service.isMilestoneDateValid(2019, 12, 31);

        assertFalse(resultOne);
        assertFalse(resultTwo);
        assertFalse(resultThree);
    }
}
