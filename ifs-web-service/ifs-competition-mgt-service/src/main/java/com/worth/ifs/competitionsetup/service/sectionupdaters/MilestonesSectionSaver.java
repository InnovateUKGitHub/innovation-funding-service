package com.worth.ifs.competitionsetup.service.sectionupdaters;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Competition setup section saver for the milestones section.
 */
@Service
public class MilestonesSectionSaver implements CompetitionSetupSectionSaver {

    @Autowired
    private CompetitionService competitionService;

	@Override
	public CompetitionSetupSection sectionToSave() {
		return CompetitionSetupSection.MILESTONES;
	}

	@Override
	public void saveSection(CompetitionResource competitionResource, CompetitionSetupForm competitionSetupForm) {

		MilestonesForm milestonesForm =  (MilestonesForm) competitionSetupForm;
      //  List<Milestones> milestones = new ArrayList<>();
       // competitionResource.getMilestones().
        try {
            LocalDate localDate =  LocalDate.of(milestonesForm.getOpenDateYear(), milestonesForm.getOpenDateMonth(), milestonesForm.getOpenDateDay());
            if (localDate != null) {
        //        milestones.add(6, localDate);
       //         milestones.
             //   competitionResource.setMilestones(milestones);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        //competitionService;
        MilestoneResource milestoneResource = new MilestoneResource();
        milestoneResource.setName("nsmr");
	}
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return MilestonesForm.class.equals(clazz);
	}

}
