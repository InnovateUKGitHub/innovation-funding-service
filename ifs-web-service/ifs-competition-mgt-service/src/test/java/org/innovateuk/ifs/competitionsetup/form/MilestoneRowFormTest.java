package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;

public class MilestoneRowFormTest {

    @Test
    public void testGetMilestoneViewModel() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(3);
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

    @Test
    public void testEditableForCompetition() {
        CompetitionResource nonIfsCompetition = newCompetitionResource().withNonIfs(true).build();
        CompetitionResource notSetupCompetition = newCompetitionResource().withSetupComplete(false).build();
        CompetitionResource setupCompetition = newCompetitionResource().withSetupComplete(true).withStartDate(LocalDateTime.now().minusDays(1)).build();


        MilestoneRowForm pastRow = new MilestoneRowForm(MilestoneType.OPEN_DATE, LocalDateTime.now().minusDays(1));
        MilestoneRowForm futureRow = new MilestoneRowForm(MilestoneType.OPEN_DATE, LocalDateTime.now().plusDays(1));

        //Non IFS competitions always dates editable
        assertTrue(pastRow.editableForCompetition(nonIfsCompetition));
        assertTrue(futureRow.editableForCompetition(nonIfsCompetition));

        //Competition in setup always dates editable
        assertTrue(pastRow.editableForCompetition(notSetupCompetition));
        assertTrue(futureRow.editableForCompetition(notSetupCompetition));

        //Open competition can only edit past dates
        assertFalse(pastRow.editableForCompetition(setupCompetition));
        assertTrue(futureRow.editableForCompetition(setupCompetition));
    }

}