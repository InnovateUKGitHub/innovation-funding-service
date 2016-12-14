package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class MilestoneRowFormTest {

    @Test
    public void testGetMilestoneViewModel() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2017,1,1,0,0);

        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.OPEN_DATE, localDateTime);

        assertEquals(MilestoneType.OPEN_DATE.name(), milestoneRowForm.getMilestoneNameType());
        assertEquals(MilestoneType.OPEN_DATE, milestoneRowForm.getMilestoneType());
        assertEquals("Sun", milestoneRowForm.getDayOfWeek());
        assertEquals(Integer.valueOf(localDateTime.getDayOfMonth()), milestoneRowForm.getDay());
        assertEquals(Integer.valueOf(localDateTime.getMonthValue()), milestoneRowForm.getMonth());
        assertEquals(Integer.valueOf(localDateTime.getYear()), milestoneRowForm.getYear());
    }
}
