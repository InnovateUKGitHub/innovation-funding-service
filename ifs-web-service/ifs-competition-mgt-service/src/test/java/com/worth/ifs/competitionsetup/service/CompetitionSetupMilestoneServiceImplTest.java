package com.worth.ifs.competitionsetup.service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import org.apache.commons.collections4.map.LinkedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupMilestoneServiceImplTest {

	@InjectMocks
	private CompetitionSetupMilestoneServiceImpl service;
	
	@Mock
	private MilestoneService milestoneService;

	@Test
	public void testCreateMilestonesForCompetition() {
        when(milestoneService.create(any(MilestoneType.class), anyLong())).thenReturn(newMilestoneResource().with(
				(integer, milestoneResource) -> {
					milestoneResource.setType(MilestoneType.OPEN_DATE);
				}
		).build());

		List<MilestoneResource> result = service.createMilestonesForCompetition(123L);

        result.forEach(milestoneResource -> assertEquals(MilestoneType.OPEN_DATE, milestoneResource.getType()));
		assertEquals(MilestoneType.presetValues().length, result.size());
		verify(milestoneService, times(MilestoneType.presetValues().length)).create(any(MilestoneType.class), anyLong());
	}

	@Test
	public void testUpdateMilestonesForCompetition() {
        List<MilestoneResource> oldMilestones = asList(
                newMilestoneResource()
                .with(milestoneResource -> milestoneResource.setType(MilestoneType.SUBMISSION_DATE))
                .withDate(LocalDateTime.MAX)
                .build());

        LinkedMap<String, MilestoneViewModel> newMilestones = new LinkedMap<>();
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(MilestoneType.SUBMISSION_DATE, LocalDateTime.MIN);
        newMilestones.put(MilestoneType.SUBMISSION_DATE.name(), milestoneViewModel);

        when(milestoneService.updateMilestones(anyListOf(MilestoneResource.class), anyLong())).thenReturn(Collections.emptyList());

        List<Error> result = service.updateMilestonesForCompetition(oldMilestones, newMilestones, 123L);

        assertTrue(result.isEmpty());
        MilestoneViewModel newMilestone = newMilestones.get(MilestoneType.SUBMISSION_DATE.name());
        assertEquals(Integer.valueOf(LocalDate.MIN.getDayOfMonth()), newMilestone.getDay());
        assertEquals(Integer.valueOf(LocalDate.MIN.getMonthValue()), newMilestone.getMonth());
        assertEquals(Integer.valueOf(LocalDate.MIN.getYear()), newMilestone.getYear());
	}

    @Test
    public void validateMilestoneDatesTrue() {
        LinkedMap<String, MilestoneViewModel> milestones = new LinkedMap<>();
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(MilestoneType.SUBMISSION_DATE, LocalDateTime.MIN);
        milestones.put(MilestoneType.SUBMISSION_DATE.name(), milestoneViewModel);

        List<Error> result = service.validateMilestoneDates(milestones);

        assertTrue(result.isEmpty());
    }

    @Test
    public void validateMilestoneDatesFalse() {
        LinkedMap<String, MilestoneViewModel> milestones = new LinkedMap<>();
        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(MilestoneType.SUBMISSION_DATE, LocalDateTime.MAX);
        milestones.put(MilestoneType.SUBMISSION_DATE.name(), milestoneViewModel);

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
