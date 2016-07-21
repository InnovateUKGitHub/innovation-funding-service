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

    private List<MilestoneResource> updateMilestonesForCompetition(List<MilestoneResource> milestones, MilestonesForm milestonesForm,
                                           List<Long> milestonesIdList, CompetitionResource competition) {
        List<MilestoneResource> updateMilestone = new ArrayList<>();
        milestones.forEach(milestone -> {
            if (MilestoneName.OPEN_DATE.equals(milestone.getName())){
                updateMilestone.add(updateOpenDay(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.BRIEFING_EVENT.equals(milestone.getName())) {
                updateMilestone.add(updateBriefingEvent(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.SUBMISSION_DATE.equals(milestone.getName())) {
                updateMilestone.add(updateSubmissionDate(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.ALLOCATE_ASSESSORS.equals(milestone.getName())) {
                updateMilestone.add(updateAllocateAssessors(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.ASSESSOR_BRIEFING.equals(milestone.getName())) {
                updateMilestone.add(updateAssessorBriefing(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.ASSESSOR_ACCEPTS.equals(milestone.getName())) {
                updateMilestone.add(updateAssessorAccepts(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.ASSESSOR_DEADLINE.equals(milestone.getName())) {
                updateMilestone.add(updateAssessorDeadline(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.LINE_DRAW.equals(milestone.getName())) {
                updateMilestone.add(updateLineDraw(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.ASSESSMENT_PANEL.equals(milestone.getName())) {
                updateMilestone.add(updateAssessmentPanel(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.PANEL_DATE.equals(milestone.getName())) {
                updateMilestone.add(updatePanelDate(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.FUNDERS_PANEL.equals(milestone.getName())) {
                updateMilestone.add(updateFundersPanel(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.NOTIFICATIONS.equals(milestone.getName())) {
                updateMilestone.add(updateNotifications(milestone, milestonesForm, milestonesIdList, competition));
            }else if (MilestoneName.RELEASE_FEEDBACK.equals(milestone.getName())) {
                updateMilestone.add(updateReleaseFeedback(milestone, milestonesForm, milestonesIdList, competition));
            }
        });
        milestoneService.update(updateMilestone, competition.getId());
        return null;
    }

    private LocalDateTime populateDate(Integer day, Integer month, Integer year){
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private MilestoneResource updateOpenDay(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getOpenDateDay(), milestonesForm.getOpenDateMonth(), milestonesForm.getOpenDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateBriefingEvent(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getBriefingEventDay(), milestonesForm.getBriefingEventMonth(), milestonesForm.getBriefingEventYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateSubmissionDate(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getSubmissionDateDay(), milestonesForm.getSubmissionDateMonth(), milestonesForm.getSubmissionDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateAllocateAssessors(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAllocateAssessorsDay(), milestonesForm.getAllocateAssessorsMonth(), milestonesForm.getAllocateAssessorsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateAssessorBriefing(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessorBriefingDay(), milestonesForm.getAssessorBriefingMonth(), milestonesForm.getAssessorBriefingYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateAssessorAccepts(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessorAcceptsDay(), milestonesForm.getAssessorAcceptsMonth(), milestonesForm.getAssessorAcceptsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateAssessorDeadline(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessorDeadlineDay(), milestonesForm.getAssessorDeadlineMonth(), milestonesForm.getAssessorDeadlineYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateLineDraw(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getLineDrawDay(), milestonesForm.getLineDrawMonth(), milestonesForm.getLineDrawYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateAssessmentPanel(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getAssessmentPanelDay(), milestonesForm.getAssessmentPanelMonth(), milestonesForm.getAssessmentPanelYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updatePanelDate(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getPanelDateDay(), milestonesForm.getPanelDateMonth(), milestonesForm.getPanelDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateFundersPanel(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getFundersPanelDay(), milestonesForm.getFundersPanelMonth(), milestonesForm.getFundersPanelYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateNotifications(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getNotificationsDay(), milestonesForm.getNotificationsMonth(), milestonesForm.getNotificationsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }

    private MilestoneResource updateReleaseFeedback(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setDate(populateDate(milestonesForm.getReleaseFeedbackDay(), milestonesForm.getReleaseFeedbackMonth(), milestonesForm.getReleaseFeedbackYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        return milestone;
    }
	
	@Override
	public boolean supportsForm(Class<? extends CompetitionSetupForm> clazz) {
		return MilestonesForm.class.equals(clazz);
	}

}
