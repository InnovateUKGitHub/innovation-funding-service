package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.form.MilestoneRowForm;
import org.apache.commons.collections4.map.LinkedMap;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class MilestoneFormPopulatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private MilestonesFormPopulator service;

    @Mock
    private MilestoneRestService milestoneRestService;

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

        when(milestoneRestService.getAllMilestonesByCompetitionId(anyLong())).thenReturn(restSuccess(milestones));

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof MilestonesForm);

        MilestonesForm form = (MilestonesForm) result;
        LinkedMap<String, MilestoneRowForm> milestoneEntryLinkedMap = form.getMilestoneEntries();

        assertFalse(form.getMilestoneEntries().isEmpty());
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getMilestoneType().equals(MilestoneType.OPEN_DATE));
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getDay() == null);
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getMonth()== null);
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getYear() == null);
        assertTrue(milestoneEntryLinkedMap.get(MilestoneType.OPEN_DATE.name()).getMilestoneType().getMilestoneDescription().equals("1. Open date"));
    }
}
