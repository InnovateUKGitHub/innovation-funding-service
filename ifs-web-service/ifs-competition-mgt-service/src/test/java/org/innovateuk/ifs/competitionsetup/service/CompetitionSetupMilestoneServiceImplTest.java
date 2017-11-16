package org.innovateuk.ifs.competitionsetup.service;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionSetupMilestoneServiceImplTest {

	@InjectMocks
	private CompetitionSetupMilestoneServiceImpl service;

	@Mock
	private MilestoneRestService milestoneRestService;

	@Test
	public void testCreateMilestonesForCompetition() {
        when(milestoneRestService.create(any(MilestoneType.class), anyLong())).thenReturn(restSuccess(newMilestoneResource().with(
				(integer, milestoneResource) -> milestoneResource.setType(MilestoneType.OPEN_DATE)).build()));

		List<MilestoneResource> result = service.createMilestonesForIFSCompetition(123L).getSuccessObject();

        result.forEach(milestoneResource -> assertEquals(MilestoneType.OPEN_DATE, milestoneResource.getType()));

        int numberOfMilestonesExpected = Arrays.stream(MilestoneType.presetValues())
                .filter(milestoneType -> !milestoneType.isOnlyNonIfs()).collect(toList()).size();

		assertEquals(numberOfMilestonesExpected, result.size());
		verify(milestoneRestService, times(numberOfMilestonesExpected)).create(any(MilestoneType.class), anyLong());
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
