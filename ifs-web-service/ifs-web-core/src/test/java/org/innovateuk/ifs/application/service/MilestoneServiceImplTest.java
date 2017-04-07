package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MilestoneServiceImplTest extends BaseServiceUnitTest<MilestoneService> {

    @Mock
    private MilestoneRestService milestoneRestService;

    @Override
    protected MilestoneService supplyServiceUnderTest() {
        return new MilestoneServiceImpl();
    }

    @Test
    public void test_getAllMilestonesByCompetitionId() {
        ZonedDateTime milestoneDate = ZonedDateTime.now();

        List<MilestoneResource> milestonesList = new ArrayList<>();
        milestonesList.add(getNewOpenDateMilestone(milestoneDate));

        when(milestoneRestService.getAllMilestonesByCompetitionId(1L)).thenReturn(restSuccess(milestonesList));

        List<MilestoneResource> found = service.getAllMilestonesByCompetitionId(1L);

        MilestoneResource foundMilestone = found.get(0);
        assertEquals(Long.valueOf(1L), foundMilestone.getId());
        assertEquals(MilestoneType.OPEN_DATE, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetitionId());
    }

    @Test
    public void test_getMilestoneByTypeAndCompetitionId() {

        ZonedDateTime milestoneDate = ZonedDateTime.now();
        MilestoneResource milestoneResource = getNewBriefingEventMilestone(milestoneDate);

        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L)).thenReturn(restSuccess(milestoneResource));

        MilestoneResource foundMilestone = service.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L);
        assertEquals(Long.valueOf(2L), foundMilestone.getId());
        assertEquals(MilestoneType.BRIEFING_EVENT, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetitionId());
    }

    @Test
    public void test_create() {
        ZonedDateTime milestoneDate = ZonedDateTime.now();

        when(milestoneRestService.create(MilestoneType.OPEN_DATE, 1L)).thenReturn(restSuccess(getNewOpenDateMilestone(milestoneDate)));

        MilestoneResource foundMilestone = service.create(MilestoneType.OPEN_DATE, 1L).getSuccessObjectOrThrowException();

        assertEquals(Long.valueOf(1L), foundMilestone.getId());
        assertEquals(MilestoneType.OPEN_DATE, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetitionId());
    }

    @Test
    public void test_updateMilestones() {
        ZonedDateTime milestoneDate = ZonedDateTime.now();

        List<MilestoneResource> milestonesList = new ArrayList<>();
        milestonesList.add(getNewOpenDateMilestone(milestoneDate));
        milestonesList.get(0).setId(2L);

        when(milestoneRestService.updateMilestones(milestonesList)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateMilestones(milestonesList);
        assertTrue(result.isSuccess());
    }

    @Test
    public void test_updateMilestone() {
        ZonedDateTime milestoneDate = ZonedDateTime.now();
        MilestoneResource milestone = getNewBriefingEventMilestone(milestoneDate);

        when(milestoneRestService.updateMilestone(milestone)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.updateMilestone(milestone);
        assertTrue(result.isSuccess());
    }

    private MilestoneResource getNewOpenDateMilestone(ZonedDateTime milestoneDate) {

        MilestoneResource milestoneResource = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(milestoneDate)
                .withCompetitionId(1L).build();

        return milestoneResource;
    }

    private MilestoneResource getNewBriefingEventMilestone(ZonedDateTime milestoneDate) {

        MilestoneResource milestoneResource = newMilestoneResource()
                .withId(2L)
                .withName(MilestoneType.BRIEFING_EVENT)
                .withDate(milestoneDate)
                .withCompetitionId(1L).build();

        return milestoneResource;
    }
}
