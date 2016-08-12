package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.form.MilestonesFormEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MilestonesSectionSaverTest {

    @InjectMocks
    private MilestonesSectionSaver service;

    @Mock
    private MilestoneService milestoneService;

    @Test
    public void testSaveMilestone() {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        LocalDateTime milestoneDate = LocalDateTime.of(2017, 1, 1, 0, 0);

        CompetitionResource competition = newCompetitionResource()
                .withMilestones(asList(1L))
                .withId(1L).build();

        MilestoneResource milestoneresource = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(milestoneDate)
                .withCompetitionId().build();

        List<MilestoneResource> resourceList = new ArrayList<>();
        resourceList.add(milestoneresource);

        competitionSetupForm.setMilestonesFormEntryList(populateMilestoneFormEntry());

        when(milestoneService.getAllDatesByCompetitionId(anyLong())).thenReturn(resourceList);

        service.saveSection(competition, competitionSetupForm);
        List<Long> milestones = competition.getMilestones();

        assertEquals(1L, milestones.get(0).longValue());
        assertTrue(resourceList.get(0).getCompetition() == 1L);
        assertNotNull(resourceList.get(0).getDate());
        assertTrue(resourceList.get(0).getType().equals(MilestoneType.OPEN_DATE));
    }

    private List<MilestonesFormEntry> populateMilestoneFormEntry() {
        List<MilestonesFormEntry> milestoneList = new ArrayList<>();

        MilestonesFormEntry milestone = new MilestonesFormEntry();

        milestone.setMilestoneType(MilestoneType.OPEN_DATE);
        milestone.setDay(1);
        milestone.setMonth(1);
        milestone.setYear(2017);
        milestone.setDayOfWeek("Wed");

        milestoneList.add(milestone);

        return milestoneList;
    }
}
