package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.model.AssessmentMilestoneViewModel;
import org.innovateuk.ifs.management.assessmentperiod.model.AssessmentPeriodViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    private MilestoneRestService milestoneRestService;

    @Autowired
    private AssessmentRestService assessmentRestService;

    public ManageAssessmentsViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics =
                competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition
                        (competitionId).getSuccess();
        return new ManageAssessmentsViewModel(competition, keyStatistics, assessmentPeriodViewModels(competitionId));
    }

    private Map<Long, List<MilestoneResource>> periodIdToMilestone(long competitionId){
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        return  milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(groupingBy(MilestoneResource::getAssessmentPeriodId));
    }

    private List<AssessmentPeriodViewModel> assessmentPeriodViewModels(long competitionId){
        return periodIdToMilestone(competitionId).entrySet().stream()
                .map(e -> assessmentPeriodViewModel(e.getValue(), e.getKey())).collect(toList());
    }

    private AssessmentPeriodViewModel assessmentPeriodViewModel(List<MilestoneResource> milestones, long assessmentPeriodId){
        long assessmentsToNofity = assessmentRestService.countByStateAndAssessmentPeriod(AssessmentState.CREATED, assessmentPeriodId).getSuccess();
        List<AssessmentMilestoneViewModel> assessmentMilestoneViewModel = milestones
                .stream()
                .map(milestone -> new AssessmentMilestoneViewModel(milestone.getType(), milestone.getDate()))
                .collect(toList());
        AssessmentPeriodViewModel assessmentPeriodViewModel = new AssessmentPeriodViewModel();
        assessmentPeriodViewModel.setMilestones(assessmentMilestoneViewModel);
        assessmentPeriodViewModel.setHasAssessorsToNotify(assessmentsToNofity > 0);
        assessmentPeriodViewModel.setAssessmentPeriodId(assessmentPeriodId);
        return assessmentPeriodViewModel;
    }
}
