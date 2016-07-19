package com.worth.ifs.competitionsetup.service.formpopulator;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.MilestoneResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Form populator for the milestones competition setup section.
 */
@Service
public class MilestonesFormPopulator implements CompetitionSetupFormPopulator {

    @Autowired
    private MilestoneService milestoneService;

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.MILESTONES;
    }
    
    private String shortDayName;
    
    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        List<MilestoneResource> allDatesByCompetitionId = milestoneService.getAllDatesByCompetitionId(competitionResource.getId());

        allDatesByCompetitionId.forEach(milestone -> {
            if (milestone.getName().equals(competitionSetupForm.getOpenDate())) {
                populateOpenDay(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getBriefingEvent())) {
                populateBriefingEvent(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getSubmissionDate())) {
                populateSubmissionDate(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getAllocateAssessors())) {
                populateAllocateAssessorsDay(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getAssessorBriefing())) {
                populateAssessorBriefingDay(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getAssessorAccepts())) {
                populateAssessorAccepts(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getAssessorDeadline())) {
                populateAssessorDeadline(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getLineDraw())) {
                populateLineDraw(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getAssessmentPanel())) {
                populateAssessmentPanel(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getPanelDate())) {
                populatePanelDate(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getFundersPanel())) {
                populateFundersPanel(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getNotifications())) {
                populateNotifications(competitionSetupForm, milestone);
            }
            if (milestone.getName().equals(competitionSetupForm.getReleaseFeedback())) {
                populateReleaseFeedback(competitionSetupForm, milestone);
            }
        });
        return competitionSetupForm;
    }

    /*
	 * Returns the first free letters of the name of the weekday
	 */
    private String getNameOfWeek(LocalDateTime localDateTime, String shortDayName) {
        if (shortDayName == null) {
             shortDayName = "- ";
        } try {
            String dayOfWeek = localDateTime.getDayOfWeek().name();
            shortDayName = dayOfWeek.substring(0, 1) + dayOfWeek.substring(1, 3).toLowerCase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return shortDayName;
    }
    
    private void populateOpenDay(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setOpenDateDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setOpenDateMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setOpenDateYear(milestone.getDate().getYear());
        competitionSetupForm.setOpenDateDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateBriefingEvent(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setBriefingEventDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setBriefingEventMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setBriefingEventYear(milestone.getDate().getYear());
        competitionSetupForm.setBriefingEventDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateSubmissionDate(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setSubmissionDateDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setSubmissionDateMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setSubmissionDateYear(milestone.getDate().getYear());
        competitionSetupForm.setSubmissionDateDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));

    }

    private void populateAllocateAssessorsDay(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAllocateAssessorsDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAllocateAssessorsMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAllocateAssessorsYear(milestone.getDate().getYear());
        competitionSetupForm.setAllocateAssessorsDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateAssessorBriefingDay(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessorBriefingDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessorBriefingMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessorBriefingYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessorBriefingDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateAssessorAccepts(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessorAcceptsDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessorAcceptsMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessorAcceptsYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessorAcceptsDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateAssessorDeadline(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessorDeadlineDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessorDeadlineMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessorDeadlineYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessorDeadlineDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateLineDraw(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setLineDrawDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setLineDrawMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setLineDrawYear(milestone.getDate().getYear());
        competitionSetupForm.setLineDrawDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateAssessmentPanel(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessmentPanelDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessmentPanelMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessmentPanelYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessmentPanelDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populatePanelDate(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setPanelDateDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setPanelDateMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setPanelDateYear(milestone.getDate().getYear());
        competitionSetupForm.setPanelDateDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateFundersPanel(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setFundersPanelDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setFundersPanelMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setFundersPanelYear(milestone.getDate().getYear());
        competitionSetupForm.setFundersPanelDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateNotifications(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setNotificationsDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setNotificationsMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setNotificationsYear(milestone.getDate().getYear());
        competitionSetupForm.setNotificationsDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }

    private void populateReleaseFeedback(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setReleaseFeedbackDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setReleaseFeedbackMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setReleaseFeedbackYear(milestone.getDate().getYear());
        competitionSetupForm.setReleaseFeedbackDayOfWeek(getNameOfWeek(milestone.getDate(), shortDayName));
    }
}