package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competition.service.MilestoneRestService;
import org.junit.Before;
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
    public void test_getAllDatesByCompetitionId() {

        LocalDateTime milestoneDate = LocalDateTime.now();

        List<MilestoneResource> milestonesList = new ArrayList<>();
        milestonesList.add(getNewMilestoneResource(milestoneDate));

        when(milestoneRestService.getAllDatesByCompetitionId(1L)).thenReturn(restSuccess(milestonesList));

        List<MilestoneResource> found = service.getAllDatesByCompetitionId(1L);

        MilestoneResource foundMilestone = found.get(0);
        assertEquals(Long.valueOf(1L), foundMilestone.getId());
        assertEquals(MilestoneType.OPEN_DATE, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetition());
    }

    @Test
    public void test_create() {
        LocalDateTime milestoneDate = LocalDateTime.now();

        when(milestoneRestService.create(MilestoneType.OPEN_DATE, 1L)).thenReturn(restSuccess(getNewMilestoneResource(milestoneDate)));

        MilestoneResource foundMilestone = service.create(MilestoneType.OPEN_DATE, 1L);

        assertEquals(Long.valueOf(1L), foundMilestone.getId());
        assertEquals(MilestoneType.OPEN_DATE, foundMilestone.getType());
        assertEquals(milestoneDate, foundMilestone.getDate());
        assertEquals(Long.valueOf(1L), foundMilestone.getCompetition());
    }

    @Test
    public void test_update() {
        LocalDateTime milestoneDate = LocalDateTime.now();

        List<MilestoneResource> milestonesList = new ArrayList<>();
        milestonesList.add(getNewMilestoneResource(milestoneDate));
        milestonesList.get(0).setId(2L);

        when(milestoneRestService.update(milestonesList, 1L)).thenReturn(restSuccess());

        List<Error> errorList = service.update(milestonesList, 1L);
        assertTrue(errorList.size() == 0);
    }

    private MilestoneResource getNewMilestoneResource(LocalDateTime milestoneDate) {

        MilestoneResource milestoneResource = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(milestoneDate)
                .withCompetitionId(1L).build();

        return milestoneResource;
    }
}