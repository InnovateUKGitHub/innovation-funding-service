package org.innovateuk.ifs.management.competition.setup.milestone.form;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class MilestoneRowFormTest {

    @Test
    public void testGetMilestoneViewModel() throws Exception {
        ZonedDateTime localDateTime = ZonedDateTime.now().plusDays(1);
        DayOfWeek target = DayOfWeek.MONDAY;
        while (localDateTime.getDayOfWeek() != target) {
            localDateTime = localDateTime.plusDays(1);
        }
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.OPEN_DATE, localDateTime);

        assertEquals(MilestoneType.OPEN_DATE.name(), milestoneRowForm.getMilestoneNameType());
        assertEquals(MilestoneType.OPEN_DATE, milestoneRowForm.getMilestoneType());
        assertEquals("Monday", milestoneRowForm.getDayOfWeek());
        assertEquals(Integer.valueOf(localDateTime.getDayOfMonth()), milestoneRowForm.getDay());
        assertEquals(Integer.valueOf(localDateTime.getMonthValue()), milestoneRowForm.getMonth());
        assertEquals(Integer.valueOf(localDateTime.getYear()), milestoneRowForm.getYear());
    }
}