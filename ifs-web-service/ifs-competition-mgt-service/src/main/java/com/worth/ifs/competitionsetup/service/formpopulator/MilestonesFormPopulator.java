package com.worth.ifs.competitionsetup.service.formpopulator;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

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
    
    public String shortDayName;
    
    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        MilestonesForm competitionSetupForm = new MilestonesForm();

        List<MilestoneResource> allDatesByCompetitionId = milestoneService.getAllDatesByCompetitionId(competitionResource.getId());

        allDatesByCompetitionId.forEach(milestone -> {
            if (MilestoneName.OPEN_DATE.equals(milestone.getName())) {
                populateOpenDay(competitionSetupForm, milestone);
            }
            else if (MilestoneName.BRIEFING_EVENT.equals(milestone.getName())) {
                populateBriefingEvent(competitionSetupForm, milestone);
            }
            else if (MilestoneName.SUBMISSION_DATE.equals(milestone.getName())) {
                populateSubmissionDate(competitionSetupForm, milestone);
            }
            else if (MilestoneName.ALLOCATE_ASSESSORS.equals(milestone.getName())) {
                populateAllocateAssessorsDay(competitionSetupForm, milestone);
            }
            else if (MilestoneName.ASSESSOR_BRIEFING.equals(milestone.getName())) {
                populateAssessorBriefingDay(competitionSetupForm, milestone);
            }
            else if (MilestoneName.ASSESSOR_ACCEPTS.equals(milestone.getName())) {
                populateAssessorAccepts(competitionSetupForm, milestone);
            }
            else if (MilestoneName.ASSESSOR_DEADLINE.equals(milestone.getName())) {
                populateAssessorDeadline(competitionSetupForm, milestone);
            }
            else if (MilestoneName.LINE_DRAW.equals(milestone.getName())) {
                populateLineDraw(competitionSetupForm, milestone);
            }
            else if (MilestoneName.ASSESSMENT_PANEL.equals(milestone.getName())) {
                populateAssessmentPanel(competitionSetupForm, milestone);
            }
            else if (MilestoneName.PANEL_DATE.equals(milestone.getName())) {
                populatePanelDate(competitionSetupForm, milestone);
            }
            else if (MilestoneName.FUNDERS_PANEL.equals(milestone.getName())) {
                populateFundersPanel(competitionSetupForm, milestone);
            }
            else if (MilestoneName.NOTIFICATIONS.equals(milestone.getName())) {
                populateNotifications(competitionSetupForm, milestone);
            }
            else if (MilestoneName.RELEASE_FEEDBACK.equals(milestone.getName())) {
                populateReleaseFeedback(competitionSetupForm, milestone);
            }
        });
        return competitionSetupForm;
    }

    /*
	 * Returns the first free letters of the name of the weekday
	 */
    private String getNameOfDay(LocalDateTime localDateTime, String shortDayName) {
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
        competitionSetupForm.setOpenDateDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateBriefingEvent(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setBriefingEventDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setBriefingEventMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setBriefingEventYear(milestone.getDate().getYear());
        competitionSetupForm.setBriefingEventDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateSubmissionDate(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setSubmissionDateDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setSubmissionDateMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setSubmissionDateYear(milestone.getDate().getYear());
        competitionSetupForm.setSubmissionDateDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));

    }

    private void populateAllocateAssessorsDay(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAllocateAssessorsDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAllocateAssessorsMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAllocateAssessorsYear(milestone.getDate().getYear());
        competitionSetupForm.setAllocateAssessorsDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateAssessorBriefingDay(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessorBriefingDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessorBriefingMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessorBriefingYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessorBriefingDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateAssessorAccepts(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessorAcceptsDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessorAcceptsMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessorAcceptsYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessorAcceptsDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateAssessorDeadline(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessorDeadlineDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessorDeadlineMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessorDeadlineYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessorDeadlineDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateLineDraw(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setLineDrawDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setLineDrawMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setLineDrawYear(milestone.getDate().getYear());
        competitionSetupForm.setLineDrawDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateAssessmentPanel(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setAssessmentPanelDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setAssessmentPanelMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setAssessmentPanelYear(milestone.getDate().getYear());
        competitionSetupForm.setAssessmentPanelDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populatePanelDate(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setPanelDateDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setPanelDateMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setPanelDateYear(milestone.getDate().getYear());
        competitionSetupForm.setPanelDateDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateFundersPanel(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setFundersPanelDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setFundersPanelMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setFundersPanelYear(milestone.getDate().getYear());
        competitionSetupForm.setFundersPanelDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateNotifications(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setNotificationsDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setNotificationsMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setNotificationsYear(milestone.getDate().getYear());
        competitionSetupForm.setNotificationsDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }

    private void populateReleaseFeedback(MilestonesForm competitionSetupForm, MilestoneResource milestone)  {
        competitionSetupForm.setReleaseFeedbackDay(milestone.getDate().getDayOfMonth());
        competitionSetupForm.setReleaseFeedbackMonth(milestone.getDate().getMonthValue());
        competitionSetupForm.setReleaseFeedbackYear(milestone.getDate().getYear());
        competitionSetupForm.setReleaseFeedbackDayOfWeek(getNameOfDay(milestone.getDate(), shortDayName));
    }
}