package org.innovateuk.ifs.management.assessment.populator;

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

import java.util.*;
import java.util.Map.Entry;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.CREATED;
import static org.innovateuk.ifs.commons.resource.PageResource.fromListZeroBased;

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

    public ManageAssessmentsViewModel populateModel(long competitionId, int page, int pageSize) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics =
                competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition
                        (competitionId).getSuccess();
        List<AssessmentPeriodViewModel> assessmentPeriodViewModels = assessmentPeriodViewModels(competitionId);
        return new ManageAssessmentsViewModel(competition, keyStatistics, fromListZeroBased(assessmentPeriodViewModels, page, pageSize));
    }

    private List<AssessmentPeriodViewModel> assessmentPeriodViewModels(long competitionId){
        SortedMap<Long, List<MilestoneResource>> periodIdToMilestone = new TreeMap<>(periodIdToMilestone(competitionId)); // Default ordering by assessment period id.
        List<AssessmentPeriodViewModel> assessmentPeriodViewModels = new ArrayList<>();
        long index = 0;
        for (Entry<Long, List<MilestoneResource>> entry: periodIdToMilestone.entrySet()){
            assessmentPeriodViewModels.add(assessmentPeriodViewModel(index++, entry.getKey(), entry.getValue()));
        }
        return assessmentPeriodViewModels;
    }

    private Map<Long, List<MilestoneResource>> periodIdToMilestone(long competitionId){
        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();
        return  milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(groupingBy(MilestoneResource::getAssessmentPeriodId));
    }

    private AssessmentPeriodViewModel assessmentPeriodViewModel(long index,long assessmentPeriodId, List<MilestoneResource> milestones){
        long assessmentsToNotify = assessmentRestService.countByStateAndAssessmentPeriod(CREATED, assessmentPeriodId).getSuccess();
        List<AssessmentMilestoneViewModel> assessmentMilestoneViewModel = milestones
                .stream()
                .map(milestone -> new AssessmentMilestoneViewModel(milestone.getType(), milestone.getDate()))
                .collect(toList());
        AssessmentPeriodViewModel assessmentPeriodViewModel = new AssessmentPeriodViewModel();
        assessmentPeriodViewModel.setMilestones(assessmentMilestoneViewModel);
        assessmentPeriodViewModel.setHasAssessorsToNotify(assessmentsToNotify > 0);
        assessmentPeriodViewModel.setAssessmentPeriodId(assessmentPeriodId);
        assessmentPeriodViewModel.setPeriodNumber(index + 1);
        return assessmentPeriodViewModel;
    }
}
