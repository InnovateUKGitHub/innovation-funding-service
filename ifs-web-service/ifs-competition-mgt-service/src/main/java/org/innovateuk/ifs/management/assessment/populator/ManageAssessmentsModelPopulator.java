package org.innovateuk.ifs.management.assessment.populator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.model.AssessmentMilestoneViewModel;
import org.innovateuk.ifs.management.assessmentperiod.model.AssessmentPeriodViewModel;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.MilestonesRowViewModel;
import org.innovateuk.ifs.management.competition.setup.milestone.viewmodel.MilestonesViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Populates the model for the 'Manage assessments' page.
 */
@Component
public class ManageAssessmentsModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionKeyAssessmentStatisticsRestService competitionKeyAssessmentStatisticsRestService;

    @Autowired
    MilestoneRestService milestoneRestService;

    public ManageAssessmentsViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics =
                competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition
                        (competitionId).getSuccess();
        return new ManageAssessmentsViewModel(competition, keyStatistics, assessmentPeriodViewModels(competitionId));
    }

    private Map<Long, List<MilestoneResource>> periodIdToMilestone(long competitionId){
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        Map<Long, List<MilestoneResource>> periodIdToMilestoneMap = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(groupingBy(MilestoneResource::getAssessmentPeriodId));
        return  periodIdToMilestoneMap;
    }

    private List<AssessmentPeriodViewModel> assessmentPeriodViewModels(long competitionId){
        return periodIdToMilestone(competitionId).entrySet().stream()
                .map(e -> assessmentPeriodViewModel(e.getValue(), e.getKey())).collect(toList());
    }

    private AssessmentPeriodViewModel assessmentPeriodViewModel(List<MilestoneResource> milestones, long assessmentPeriodId){

        List<AssessmentMilestoneViewModel> assessmentMilestoneViewModel = milestones
                .stream()
                .map(milestone -> new AssessmentMilestoneViewModel(milestone.getType(), milestone.getDate()))
                .collect(toList());
        AssessmentPeriodViewModel assessmentPeriodViewModel = new AssessmentPeriodViewModel();
        assessmentPeriodViewModel.setMilestoneEntries(assessmentMilestoneViewModel);
        assessmentPeriodViewModel.setHasAssessorsToNotify(true); // TODO
        return assessmentPeriodViewModel;
    }
}
