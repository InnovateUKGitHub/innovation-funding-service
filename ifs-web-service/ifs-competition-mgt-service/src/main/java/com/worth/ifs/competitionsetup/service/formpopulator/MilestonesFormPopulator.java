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

		//JH @todo null check on getDates from Repo
		competitionSetupForm.setOpenDateDay(currentDate.getDayOfMonth());
		competitionSetupForm.setOpenDateMonth(currentDate.getMonthValue());
		competitionSetupForm.setOpenDateYear(currentDate.getYear());

		competitionSetupForm.setBriefingEventDay(currentDate.getDayOfMonth());
		competitionSetupForm.setBriefingEventMonth(currentDate.getMonthValue());
		competitionSetupForm.setBriefingEventYear(currentDate.getYear());

		competitionSetupForm.setSubmissionDateDay(currentDate.getDayOfMonth());
		competitionSetupForm.setSubmissionDateMonth(currentDate.getMonthValue());
		competitionSetupForm.setSubmissionDateYear(currentDate.getYear());

		competitionSetupForm.setAllocateAssessorsDay(currentDate.getDayOfMonth());
		competitionSetupForm.setAllocateAssessorsMonth(currentDate.getMonthValue());
		competitionSetupForm.setAllocateAssessorsYear(currentDate.getYear());

		competitionSetupForm.setAssessorBriefingDay(currentDate.getDayOfMonth());
		competitionSetupForm.setAssessorBriefingMonth(currentDate.getMonthValue());
		competitionSetupForm.setAssessorBriefingYear(currentDate.getYear());

		competitionSetupForm.setAssessorAcceptsDay(currentDate.getDayOfMonth());
		competitionSetupForm.setAssessorAcceptsMonth(currentDate.getMonthValue());
		competitionSetupForm.setAssessorAcceptsYear(currentDate.getYear());

		competitionSetupForm.setAssessorDeadlineDay(currentDate.getDayOfMonth());
		competitionSetupForm.setAssessorDeadlineMonth(currentDate.getMonthValue());
		competitionSetupForm.setAssessorDeadlineYear(currentDate.getYear());

		competitionSetupForm.setLineDrawDay(currentDate.getDayOfMonth());
		competitionSetupForm.setLineDrawMonth(currentDate.getMonthValue());
		competitionSetupForm.setLineDrawYear(currentDate.getYear());

		competitionSetupForm.setAssessmentPanelDay(currentDate.getDayOfMonth());
		competitionSetupForm.setAssessmentPanelMonth(currentDate.getMonthValue());
		competitionSetupForm.setAssessmentPanelYear(currentDate.getYear());

		competitionSetupForm.setPanelDateDay(currentDate.getDayOfMonth());
		competitionSetupForm.setPanelDateMonth(currentDate.getMonthValue());
		competitionSetupForm.setPanelDateYear(currentDate.getYear());

		competitionSetupForm.setFundersPanelDay(currentDate.getDayOfMonth());
		competitionSetupForm.setFundersPanelMonth(currentDate.getMonthValue());
		competitionSetupForm.setFundersPanelYear(currentDate.getYear());

		competitionSetupForm.setNotificationsDay(currentDate.getDayOfMonth());
		competitionSetupForm.setNotificationsMonth(currentDate.getMonthValue());
		competitionSetupForm.setNotificationsYear(currentDate.getYear());

		competitionSetupForm.setReleaseFeedbackDay(currentDate.getDayOfMonth());
		competitionSetupForm.setReleaseFeedbackMonth(currentDate.getMonthValue());
		competitionSetupForm.setReleaseFeedbackYear(currentDate.getYear());


		return competitionSetupForm;
	}

}
