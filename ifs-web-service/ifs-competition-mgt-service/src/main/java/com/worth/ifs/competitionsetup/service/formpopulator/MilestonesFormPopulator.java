package com.worth.ifs.competitionsetup.service.formpopulator;

import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

import java.time.LocalDate;

/**
 * Form populator for the milestones competition setup section.
 */
@Service
public class MilestonesFormPopulator implements CompetitionSetupFormPopulator {

	@Override
	public CompetitionSetupSection sectionToFill() {
		return CompetitionSetupSection.MILESTONES;
	}

	@Override
	public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
		MilestonesForm competitionSetupForm = new MilestonesForm();

		//temporary date
		LocalDate currentDate = LocalDate.now();

		//JH @todo null check on dates compResource
		competitionSetupForm.setOpenDate(currentDate);
		competitionSetupForm.setBriefingEvent(currentDate);
		competitionSetupForm.setSubmissionDate(currentDate);
		competitionSetupForm.setAllocateAssessors(currentDate);
		competitionSetupForm.setAssessorBriefing(currentDate);
		competitionSetupForm.setAssessorAccepts(currentDate);
		competitionSetupForm.setAssessorDeadline(currentDate);
		competitionSetupForm.setLineDraw(currentDate);
		competitionSetupForm.setAssessmentPanel(currentDate);
		competitionSetupForm.setPanelDate(currentDate);
		competitionSetupForm.setFundersPanel(currentDate);
		competitionSetupForm.setNotifications(currentDate);
		competitionSetupForm.setReleaseFeedback(currentDate);

		return competitionSetupForm;
	}

}
