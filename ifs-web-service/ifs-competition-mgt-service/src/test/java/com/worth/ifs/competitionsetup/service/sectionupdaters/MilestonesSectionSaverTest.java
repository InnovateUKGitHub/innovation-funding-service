package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.MilestonesForm;
import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.el.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MilestonesSectionSaverTest {

    @InjectMocks
    private MilestonesSectionSaver service;

    @Mock
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Mock
    private CompetitionService competitionService;

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
                .withCompetitionId(1L).build();

        List<MilestoneResource> resourceList = new ArrayList<>();
        resourceList.add(milestoneresource);

        competitionSetupForm.setMilestoneEntries(populateMilestoneFormEntry());

        when(milestoneService.getAllMilestonesByCompetitionId(anyLong())).thenReturn(resourceList);
        service.saveSection(competition, competitionSetupForm);
        List<Long> milestones = competition.getMilestones();

        assertEquals(1L, milestones.get(0).longValue());
        assertTrue(resourceList.get(0).getCompetition() == 1L);
        assertNotNull(resourceList.get(0).getDate());
        assertTrue(resourceList.get(0).getType().equals(MilestoneType.OPEN_DATE));
    }

    private LinkedMap<String, MilestoneViewModel> populateMilestoneFormEntry() {
        LinkedMap<String, MilestoneViewModel>  milestoneList = new LinkedMap<>();

        MilestoneViewModel milestone = new MilestoneViewModel();

        milestone.setMilestoneType(MilestoneType.OPEN_DATE);
        milestone.setDay(1);
        milestone.setMonth(1);
        milestone.setYear(2017);
        milestone.setDayOfWeek("Wed");

        milestoneList.put(MilestoneType.OPEN_DATE.name(), milestone);

        return milestoneList;
    }

    @Test
    public void testAutoSaveCompetitionSetupSection() throws ParseException {
        List<Error> errors = new ArrayList<>();
        String fieldName =  "milestoneEntries[BRIEFING_EVENT].milestoneType";
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L)).thenReturn(getBriefingEventMilestone());

        CompetitionResource competition = newCompetitionResource().build();
        competition.setMilestones(Arrays.asList(10L));

        service.updateCompetitionResourceWithAutoSave(errors, competition, fieldName, "20-10-2020");

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testAutoSaveCompetitionSetupSectionDateNotInFuture() throws ParseException {
        String fieldName =  "milestoneEntries[BRIEFING_EVENT].milestoneType";
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.BRIEFING_EVENT, 1L)).thenReturn(getBriefingEventMilestone());

        CompetitionResource competition = newCompetitionResource().withId(1L).build();
        competition.setMilestones(Arrays.asList(10L));

        List<Error> errors = service.autoSaveSectionField(competition, fieldName, "20-10-2015", null);

        assertTrue(!errors.isEmpty());
        assertEquals(errors.get(0).getErrorKey(), "error.milestone.invalid");
    }

    private MilestoneResource getBriefingEventMilestone(){
        return newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate(LocalDateTime.of(2020, 12, 1, 0, 0))
                .withCompetitionId(1L).build();
    }
}
