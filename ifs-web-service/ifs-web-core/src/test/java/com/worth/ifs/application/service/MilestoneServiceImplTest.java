package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competition.service.MilestoneRestService;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        LocalDateTime milestoneDate = LocalDateTime.now();

        List<MilestoneResource> milestonesList = new ArrayList<>();
        milestonesList.add(getNewOpenDateMilestone(milestoneDate));

        when(milestoneRestService.getAllMilestonesByCompetitionId(1L)).thenReturn(restSuccess(milestonesList));

        List<MilestoneResource> found = service.getAllMilestonesByCompetitionId(1L);

        MilestoneResource foundMilestone = found.get(0);
        assertEquals(Long.valueOf(1L), foundMilestone.getId());
        assertEquals(MilestoneType.OPEN_DATE, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetition());
    }

    @Test
    public void test_getMilestoneByTypeAndCompetitionId() {

        LocalDateTime milestoneDate = LocalDateTime.now();
        MilestoneResource milestoneResource = getNewBriefingEventMilestone(milestoneDate);

        when(milestoneRestService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L)).thenReturn(restSuccess(milestoneResource));

        MilestoneResource foundMilestone = service.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L);
        assertEquals(Long.valueOf(2L), foundMilestone.getId());
        assertEquals(MilestoneType.BRIEFING_EVENT, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetition());
    }

    @Test
    public void test_create() {
        LocalDateTime milestoneDate = LocalDateTime.now();

        when(milestoneRestService.create(MilestoneType.OPEN_DATE, 1L)).thenReturn(restSuccess(getNewOpenDateMilestone(milestoneDate)));

        MilestoneResource foundMilestone = service.create(MilestoneType.OPEN_DATE, 1L);

        assertEquals(Long.valueOf(1L), foundMilestone.getId());
        assertEquals(MilestoneType.OPEN_DATE, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetition());
    }

    @Test
    public void test_updateMilestones() {
        LocalDateTime milestoneDate = LocalDateTime.now();

        List<MilestoneResource> milestonesList = new ArrayList<>();
        milestonesList.add(getNewOpenDateMilestone(milestoneDate));
        milestonesList.get(0).setId(2L);

        when(milestoneRestService.updateMilestones(milestonesList, 1L)).thenReturn(restSuccess());

        List<Error> errorList = service.updateMilestones(milestonesList, 1L);
        assertTrue(errorList.isEmpty());
    }

    @Test
    public void test_updateMilestone() {
        LocalDateTime milestoneDate = LocalDateTime.now();
        MilestoneResource milestone = getNewBriefingEventMilestone(milestoneDate);

        when(milestoneRestService.updateMilestone(milestone)).thenReturn(restSuccess());

        List<Error> errors = service.updateMilestone(milestone);
        assertTrue(errors.isEmpty());
    }

    private MilestoneResource getNewOpenDateMilestone(LocalDateTime milestoneDate) {

        MilestoneResource milestoneResource = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(milestoneDate)
                .withCompetitionId(1L).build();

        return milestoneResource;
    }

    private MilestoneResource getNewBriefingEventMilestone(LocalDateTime milestoneDate) {

        MilestoneResource milestoneResource = newMilestoneResource()
                .withId(2L)
                .withName(MilestoneType.BRIEFING_EVENT)
                .withDate(milestoneDate)
                .withCompetitionId(1L).build();

        return milestoneResource;
    }
}