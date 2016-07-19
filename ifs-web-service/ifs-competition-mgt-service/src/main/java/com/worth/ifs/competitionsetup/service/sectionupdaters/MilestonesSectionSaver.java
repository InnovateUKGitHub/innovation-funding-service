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
	public void saveSection(CompetitionResource competition, CompetitionSetupForm competitionSetupForm) {

        MilestonesForm milestonesForm = (MilestonesForm) competitionSetupForm;

        List<MilestoneResource> milestones = milestoneService.getAllDatesByCompetitionId(competition.getId());
        List<Long> milestonesIdList = new ArrayList<>();

        if (milestones == null || milestones.isEmpty()) {
            milestones.addAll(createMilestonesForCompetition(populateList(milestonesForm)));
        }

        if (milestones != null ) {
            updateMilestonesForCompetition(milestones, milestonesForm, milestonesIdList, competition);
            competition.setMilestones(milestonesIdList);
            competitionService.update(competition);
        }
    }

    private List<MilestoneResource> createMilestonesForCompetition(List<String> milestoneNames) {
       List<MilestoneResource> newMilestones = new ArrayList<>();
        milestoneNames.forEach(name -> {
            MilestoneResource newMilestone = milestoneService.create();
            newMilestone.setName(name);
            newMilestones.add(newMilestone);
        } );
        return newMilestones;
    }

    private void updateMilestonesForCompetition(List<MilestoneResource> milestones, MilestonesForm milestonesForm,
                                           List<Long> milestonesIdList, CompetitionResource competition) {
        milestones.forEach(milestone -> {
            if(milestone.getName().equals(milestonesForm.getOpenDate())){
                updateOpenDay(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getBriefingEvent())) {
                updateBriefingEvent(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getSubmissionDate())) {
                updateSubmissionDate(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getAllocateAssessors())) {
                updateAllocateAssessors(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getAssessorBriefing())) {
                updateAssessorBriefing(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getAssessorAccepts())) {
                updateAssessorAccepts(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getAssessorDeadline())) {
                updateAssessorDeadline(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getLineDraw())) {
                updateLineDraw(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getAssessmentPanel())) {
                updateAssessmentPanel(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getPanelDate())) {
                updatePanelDate(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getFundersPanel())) {
                updateFundersPanel(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getNotifications())) {
                updateNotifications(milestone, milestonesForm, milestonesIdList, competition);
            }if (milestone.getName().equals(milestonesForm.getReleaseFeedback())) {
                updateReleaseFeedback(milestone, milestonesForm, milestonesIdList, competition);
            }
        });
    }

    private LocalDateTime populateDate(Integer day, Integer month, Integer year){
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    private List<String> populateList(MilestonesForm milestonesForm) {
        List<String> milestoneNames = new ArrayList<>(12);
        milestoneNames.add(milestonesForm.getOpenDate());
        milestoneNames.add(milestonesForm.getBriefingEvent());
        milestoneNames.add(milestonesForm.getSubmissionDate());
        milestoneNames.add(milestonesForm.getAllocateAssessors());
        milestoneNames.add(milestonesForm.getAssessorBriefing());
        milestoneNames.add(milestonesForm.getAssessorAccepts());
        milestoneNames.add(milestonesForm.getAssessorDeadline());
        milestoneNames.add(milestonesForm.getLineDraw());
        milestoneNames.add(milestonesForm.getAssessmentPanel());
        milestoneNames.add(milestonesForm.getPanelDate());
        milestoneNames.add(milestonesForm.getFundersPanel());
        milestoneNames.add(milestonesForm.getNotifications());
        milestoneNames.add(milestonesForm.getReleaseFeedback());
        return milestoneNames;
    }
    
    private void updateOpenDay(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getOpenDate());
        milestone.setDate(populateDate(milestonesForm.getOpenDateDay(), milestonesForm.getOpenDateMonth(), milestonesForm.getOpenDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateBriefingEvent(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getBriefingEvent());
        milestone.setDate(populateDate(milestonesForm.getBriefingEventDay(), milestonesForm.getBriefingEventMonth(), milestonesForm.getBriefingEventYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateSubmissionDate(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getSubmissionDate());
        milestone.setDate(populateDate(milestonesForm.getSubmissionDateDay(), milestonesForm.getSubmissionDateMonth(), milestonesForm.getSubmissionDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAllocateAssessors(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getAllocateAssessors());
        milestone.setDate(populateDate(milestonesForm.getAllocateAssessorsDay(), milestonesForm.getAllocateAssessorsMonth(), milestonesForm.getAllocateAssessorsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessorBriefing(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getAssessorBriefing());
        milestone.setDate(populateDate(milestonesForm.getAssessorBriefingDay(), milestonesForm.getAssessorBriefingMonth(), milestonesForm.getAssessorBriefingYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessorAccepts(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getAssessorAccepts());
        milestone.setDate(populateDate(milestonesForm.getAssessorAcceptsDay(), milestonesForm.getAssessorAcceptsMonth(), milestonesForm.getAssessorAcceptsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessorDeadline(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getAssessorDeadline());
        milestone.setDate(populateDate(milestonesForm.getAssessorDeadlineDay(), milestonesForm.getAssessorDeadlineMonth(), milestonesForm.getAssessorDeadlineYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateLineDraw(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getLineDraw());
        milestone.setDate(populateDate(milestonesForm.getLineDrawDay(), milestonesForm.getLineDrawMonth(), milestonesForm.getLineDrawYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateAssessmentPanel(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getAssessmentPanel());
        milestone.setDate(populateDate(milestonesForm.getAssessmentPanelDay(), milestonesForm.getAssessmentPanelMonth(), milestonesForm.getAssessmentPanelYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updatePanelDate(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getPanelDate());
        milestone.setDate(populateDate(milestonesForm.getPanelDateDay(), milestonesForm.getPanelDateMonth(), milestonesForm.getPanelDateYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateFundersPanel(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getFundersPanel());
        milestone.setDate(populateDate(milestonesForm.getFundersPanelDay(), milestonesForm.getFundersPanelMonth(), milestonesForm.getFundersPanelYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateNotifications(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getNotifications());
        milestone.setDate(populateDate(milestonesForm.getNotificationsDay(), milestonesForm.getNotificationsMonth(), milestonesForm.getNotificationsYear()));
        milestone.setCompetition(competition.getId());
        milestonesIdList.add(milestone.getId());
        milestoneService.update(milestone, competition.getId());
    }

    private void updateReleaseFeedback(MilestoneResource milestone, MilestonesForm milestonesForm, List<Long> milestonesIdList, CompetitionResource competition) {
        milestone.setName(milestonesForm.getReleaseFeedback());
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
