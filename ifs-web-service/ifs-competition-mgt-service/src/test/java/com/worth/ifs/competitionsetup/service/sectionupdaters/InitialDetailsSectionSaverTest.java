package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneType;
import com.worth.ifs.competitionsetup.form.InitialDetailsForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InitialDetailsSectionSaverTest {

    @InjectMocks
    private InitialDetailsSectionSaver service;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private MilestoneService milestoneService;

    @Mock
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Test
    public void testSaveCompetitionSetupSection() {
        InitialDetailsForm competitionSetupForm = new InitialDetailsForm();
        competitionSetupForm.setTitle("title");
        competitionSetupForm.setExecutiveUserId(1L);
        competitionSetupForm.setOpeningDateDay(1);
        competitionSetupForm.setOpeningDateMonth(12);
        competitionSetupForm.setOpeningDateYear(2000);
        competitionSetupForm.setCompetitionTypeId(2L);
        competitionSetupForm.setLeadTechnologistUserId(3L);
        competitionSetupForm.setInnovationAreaCategoryId(4L);
        competitionSetupForm.setInnovationSectorCategoryId(5L);

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(getMilestone());

        List<Long> milestonesIds = new ArrayList<>();
        milestonesIds.add(10L);

        CompetitionResource competition = newCompetitionResource()
                .withCompetitionCode("compcode").build();
        competition.setMilestones(milestonesIds);

        when(milestoneService.getAllDatesByCompetitionId(1L)).thenReturn(milestones);
        service.saveSection(competition, competitionSetupForm);

        assertEquals("title", competition.getName());
        assertEquals(Long.valueOf(1L), competition.getExecutive());
        assertEquals(Long.valueOf(2L), competition.getCompetitionType());
        assertEquals(Long.valueOf(3L), competition.getLeadTechnologist());
        assertEquals(Long.valueOf(4L), competition.getInnovationArea());
        assertEquals(Long.valueOf(5L), competition.getInnovationSector());
        assertEquals(LocalDateTime.of(2000, 12, 1, 0, 0), competition.getStartDate());

        verify(competitionService).update(competition);
    }

    private MilestoneResource getMilestone(){
        MilestoneResource milestone = new MilestoneResource();
        milestone.setId(10L);
        milestone.setType(MilestoneType.OPEN_DATE);
        milestone.setDate(LocalDateTime.of(2000, 12, 1, 0, 0));
        milestone.setCompetition(1L);
        return milestone;
    }
}