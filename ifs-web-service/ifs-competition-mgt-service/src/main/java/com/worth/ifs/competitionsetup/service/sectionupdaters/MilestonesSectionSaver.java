package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Competition setup section saver for the milestones section.
 */
@Service
public class MilestonesSectionSaver implements CompetitionSetupSectionSaver {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private MilestoneService milestoneService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.MILESTONES;
	}

	@Override
	public void saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {

        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;

        List<MilestoneResource> milestones = milestoneService.getAllDatesByCompetitionId(competitionResource.getId());

        MilestoneResource newMilestone=null;
        List<Long> milestonesIdList = new ArrayList<>();

        if (milestones == null || milestones.isEmpty()) {
            milestones.add(createMilestones());
        }
        if (milestones != null ){
        for (MilestoneResource mr : milestones) {
            if (mr.getName().equals("Open day")) {

            } else {
                newMilestone = mr;
            }
         //   new LocalDateTime.of(2012, 12, 5, 0 ,0);
            newMilestone.setName("OpenTestDate");
            newMilestone.setDate(populateDate(milestonesForm.getOpenDateDay(), milestonesForm.getOpenDateMonth(), milestonesForm.getOpenDateYear()));
            newMilestone.setCompetition(competitionResource.getId());

            milestonesIdList.add(newMilestone.getId());
            milestoneService.update(newMilestone, competitionResource.getId());
            competitionResource.setMilestones(milestonesIdList);
            competitionService.update(competitionResource);
        }
    }}

    private MilestoneResource createMilestones() {
        MilestoneResource newMilestone = milestoneService.create();
        newMilestone.setName("create New Milestone");
        return newMilestone;
    }

    private LocalDateTime populateDate(Integer day, Integer month, Integer year){
        return LocalDateTime.of(year, month, day, 0, 0);
    }
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return MilestonesForm.class.equals(clazz);
	}

}
