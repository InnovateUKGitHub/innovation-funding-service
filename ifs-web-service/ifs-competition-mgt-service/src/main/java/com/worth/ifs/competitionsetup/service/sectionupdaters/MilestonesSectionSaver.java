package com.worth.ifs.competitionsetup.service.sectionupdaters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.application.service.MilestoneService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.MilestoneResource;
import com.worth.ifs.competition.resource.MilestoneResource.MilestoneName;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.MilestonesForm;

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
	public void saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;

        List<MilestoneResource> milestones = milestoneService.getAllDatesByCompetitionId(competition.getId());
        List<Long> milestonesIdList = new ArrayList<>();

        if (milestones == null || milestones.isEmpty()) {
            milestones.addAll(createMilestonesForCompetition());
        }

        if (milestones != null ) {
            updateMilestonesForCompetition(milestones, milestonesForm, milestonesIdList, competition);
            competition.setMilestones(milestonesIdList);
            competitionService.update(competition);
        }
    }

    private List<MilestoneResource> createMilestonesForCompetition() {
       List<MilestoneResource> newMilestones = new ArrayList<>();
       Stream.of(MilestoneName.values()).forEach(name -> {
            MilestoneResource newMilestone = milestoneService.create();
            newMilestone.setName(name);
            newMilestones.add(newMilestone);
        });
        return newMilestones;
    }

    private void updateMilestonesForCompetition(List<MilestoneResource> milestones, MilestonesForm milestonesForm,
                                           List<Long> milestonesIdList, CompetitionResource competition) {
        milestones.forEach(milestone -> {
            if (MilestoneName.OPEN_DATE.equals(milestone.getName())){
                updateOpenDay(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.BRIEFING_EVENT.equals(milestone.getName())) {
                updateBriefingEvent(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.SUBMISSION_DATE.equals(milestone.getName())) {
                updateSubmissionDate(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.ALLOCATE_ASSESSORS.equals(milestone.getName())) {
                updateAllocateAssessors(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.ASSESSOR_BRIEFING.equals(milestone.getName())) {
                updateAssessorBriefing(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.ASSESSOR_ACCEPTS.equals(milestone.getName())) {
                updateAssessorAccepts(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.ASSESSOR_DEADLINE.equals(milestone.getName())) {
                updateAssessorDeadline(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.LINE_DRAW.equals(milestone.getName())) {
                updateLineDraw(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.ASSESSMENT_PANEL.equals(milestone.getName())) {
                updateAssessmentPanel(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.PANEL_DATE.equals(milestone.getName())) {
                updatePanelDate(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.FUNDERS_PANEL.equals(milestone.getName())) {
                updateFundersPanel(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.NOTIFICATIONS.equals(milestone.getName())) {
                updateNotifications(milestone, milestonesForm, milestonesIdList, competition);
            }else if (MilestoneName.RELEASE_FEEDBACK.equals(milestone.getName())) {
                updateReleaseFeedback(milestone, milestonesForm, milestonesIdList, competition);
            }
        });
    }

    private LocalDateTime populateDate(Integer day, Integer month, Integer year){
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private void updateOpenDay(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getOpenDateDay(), milestonesForm.getOpenDateMonth(), milestonesForm.getOpenDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateBriefingEvent(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getBriefingEventDay(), milestonesForm.getBriefingEventMonth(), milestonesForm.getBriefingEventYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateSubmissionDate(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getSubmissionDateDay(), milestonesForm.getSubmissionDateMonth(), milestonesForm.getSubmissionDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAllocateAssessors(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAllocateAssessorsDay(), milestonesForm.getAllocateAssessorsMonth(), milestonesForm.getAllocateAssessorsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessorBriefing(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessorBriefingDay(), milestonesForm.getAssessorBriefingMonth(), milestonesForm.getAssessorBriefingYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessorAccepts(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessorAcceptsDay(), milestonesForm.getAssessorAcceptsMonth(), milestonesForm.getAssessorAcceptsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessorDeadline(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessorDeadlineDay(), milestonesForm.getAssessorDeadlineMonth(), milestonesForm.getAssessorDeadlineYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateLineDraw(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getLineDrawDay(), milestonesForm.getLineDrawMonth(), milestonesForm.getLineDrawYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessmentPanel(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessmentPanelDay(), milestonesForm.getAssessmentPanelMonth(), milestonesForm.getAssessmentPanelYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updatePanelDate(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getPanelDateDay(), milestonesForm.getPanelDateMonth(), milestonesForm.getPanelDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateFundersPanel(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getFundersPanelDay(), milestonesForm.getFundersPanelMonth(), milestonesForm.getFundersPanelYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateNotifications(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getNotificationsDay(), milestonesForm.getNotificationsMonth(), milestonesForm.getNotificationsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateReleaseFeedback(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getReleaseFeedbackDay(), milestonesForm.getReleaseFeedbackMonth(), milestonesForm.getReleaseFeedbackYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return MilestonesForm.class.equals(clazz);
	}

}
