package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MilestoneFormPopulatorTest {

    @InjectMocks
    private MilestonesFormPopulator service;

    @Mock
    private MilestoneService milestoneService;

    @Test
    public void testSectionToFill() {
        CompetitionSetupSection result = service.sectionToFill();
        assertEquals(CompetitionSetupSection.MILESTONES, result);
    }

    @Test
    public void testGetSectionFormDataMilestones() {

        MilestoneResource milestone = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate()
                .withCompetitionId(1L).build();

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(milestone);

        List<Long> milestoneList = new ArrayList<>();
        milestoneList.add(1L);

        CompetitionResource competition = newCompetitionResource()
                .withMilestones(milestoneList)
                .build();

        when(milestoneService.getAllDatesByCompetitionId(anyLong())).thenReturn(milestones);

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof MilestonesForm);

        MilestonesForm form = (MilestonesForm) result;
        List<MilestonesFormEntry> milestonesFormEntryList = form.getMilestonesFormEntryList();

        assertFalse(form.getMilestonesFormEntryList().isEmpty());
        assertTrue(milestonesFormEntryList.get(0).getMilestoneType().equals(MilestoneType.OPEN_DATE));
        assertTrue(milestonesFormEntryList.get(0).getDay() == null);
        assertTrue(milestonesFormEntryList.get(0).getMonth()== null);
        assertTrue(milestonesFormEntryList.get(0).getYear() == null);
        assertTrue(milestonesFormEntryList.get(0).getMilestoneType().getMilestoneDescription().equals("1. Open date"));
    }
}
