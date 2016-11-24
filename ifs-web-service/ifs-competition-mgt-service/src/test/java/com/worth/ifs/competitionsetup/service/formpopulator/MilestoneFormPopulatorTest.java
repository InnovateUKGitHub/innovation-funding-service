package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import org.apache.commons.collections4.map.LinkedMap;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class MilestoneFormPopulatorTest extends BaseUnitTestMocksTest {

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

        when(milestoneService.getAllMilestonesByCompetitionId(anyLong())).thenReturn(milestones);

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof MilestonesForm);

        MilestonesForm form = (MilestonesForm) result;
        LinkedMap<String, MilestoneViewModel> milestoneEntryLinkedMap = form.getMilestoneEntries();

        assertFalse(form.getMilestoneEntries().isEmpty());
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getMilestoneType().equals(MilestoneType.OPEN_DATE));
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getDay() == null);
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getMonth()== null);
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getYear() == null);
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getMilestoneType().getMilestoneDescription().equals("1. Open date"));
    }
}
