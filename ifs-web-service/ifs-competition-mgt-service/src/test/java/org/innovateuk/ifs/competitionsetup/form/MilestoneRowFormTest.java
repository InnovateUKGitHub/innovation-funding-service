package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class MilestoneRowFormTest {

    @Test
    public void testGetMilestoneViewModel() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1);
        MilestoneRowForm milestoneRowForm = new MilestoneRowForm(MilestoneType.OPEN_DATE, localDateTime);

        assertEquals(MilestoneType.OPEN_DATE.name(), milestoneRowForm.getMilestoneNameType());
        assertEquals(MilestoneType.OPEN_DATE, milestoneRowForm.getMilestoneType());
        assertEquals("Fri", milestoneRowForm.getDayOfWeek());
        assertEquals(Integer.valueOf(localDateTime.getDayOfMonth()), milestoneRowForm.getDay());
        assertEquals(Integer.valueOf(localDateTime.getMonthValue()), milestoneRowForm.getMonth());
        assertEquals(Integer.valueOf(localDateTime.getYear()), milestoneRowForm.getYear());
        assertEquals(true, milestoneRowForm.isEditable());
    }
}
