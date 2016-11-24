package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class MilestoneViewModelTest {

    @Test
    public void testGetMilestoneViewModel() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2017,1,1,0,0);

        MilestoneViewModel milestoneViewModel = new MilestoneViewModel(MilestoneType.OPEN_DATE, localDateTime);

        assertEquals(MilestoneType.OPEN_DATE.name(), milestoneViewModel.getMilestoneNameType());
        assertEquals(MilestoneType.OPEN_DATE, milestoneViewModel.getMilestoneType());
        assertEquals("Sun", milestoneViewModel.getDayOfWeek());
        assertEquals(Integer.valueOf(localDateTime.getDayOfMonth()), milestoneViewModel.getDay());
        assertEquals(Integer.valueOf(localDateTime.getMonthValue()), milestoneViewModel.getMonth());
        assertEquals(Integer.valueOf(localDateTime.getYear()), milestoneViewModel.getYear());
    }
}