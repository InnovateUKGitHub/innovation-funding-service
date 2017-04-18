package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

public class MilestoneRowFormTest {

    @Test
    public void testGetMilestoneViewModel() throws Exception {
        ZonedDateTime localDateTime = ZonedDateTime.now().plusDays(3);
        String dayOfWeek = localDateTime.getDayOfWeek().name().substring(0, 1)
                + localDateTime.getDayOfWeek().name().substring(1, 3).toLowerCase();
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.OPEN_DATE, localDateTime);

        assertEquals(MilestoneType.OPEN_DATE.name(), milestoneRowForm.getMilestoneNameType());
        assertEquals(MilestoneType.OPEN_DATE, milestoneRowForm.getMilestoneType());
        assertEquals(dayOfWeek, milestoneRowForm.getDayOfWeek());
        assertEquals(Integer.valueOf(localDateTime.getDayOfMonth()), milestoneRowForm.getDay());
        assertEquals(Integer.valueOf(localDateTime.getMonthValue()), milestoneRowForm.getMonth());
        assertEquals(Integer.valueOf(localDateTime.getYear()), milestoneRowForm.getYear());
    }
}