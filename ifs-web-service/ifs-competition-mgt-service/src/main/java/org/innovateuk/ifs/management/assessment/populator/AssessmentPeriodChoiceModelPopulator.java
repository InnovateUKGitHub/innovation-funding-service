package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.assessment.service.AssessmentPeriodService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessmentPeriodChoiceViewModel;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessmentPeriodChoiceViewModel.AssessmentPeriodViewModel;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessmentPeriodChoiceViewModel.MilestoneViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssessmentPeriodChoiceModelPopulator {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private AssessmentPeriodService assessmentPeriodService;

    public AssessmentPeriodChoiceViewModel populate(long competitionId) {

        List<MilestoneResource> milestones = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess();

        Map<Long, List<MilestoneResource>> periodIdToMilestoneMap = milestones
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));
        List<AssessmentPeriodViewModel> assessmentPeriods = periodIdToMilestoneMap.entrySet().stream()
                .map(e -> {
                    Map<String, MilestoneViewModel> milestoneFormEntries = new LinkedHashMap<>();
                    e.getValue().forEach(milestone ->
                            milestoneFormEntries.put(milestone.getType().getMilestoneDescription(), populateMilestoneFormEntries(milestone))
                    );
                    AssessmentPeriodViewModel assessmentPeriodViewModel = new AssessmentPeriodViewModel();
                    assessmentPeriodViewModel.setAssessmentPeriodId(e.getKey());
                    assessmentPeriodViewModel.setMilestoneEntries(milestoneFormEntries);
                    for (Map.Entry<String, MilestoneViewModel> assessmentPeriodsList : milestoneFormEntries.entrySet()) {
                        String typeDesc = assessmentPeriodsList.getKey();
                        if (typeDesc.equals(MilestoneType.ASSESSMENT_CLOSED.getMilestoneDescription())) {
                            assessmentPeriodViewModel.setAssessmentClosed(true);
                            break;
                        }
                    }

                    String displayName = assessmentPeriodService.displayName(e.getKey(), competitionId);
                    assessmentPeriodViewModel.setDisplayName(displayName);

                    return assessmentPeriodViewModel;
                })
                .collect(Collectors.toList());
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return new AssessmentPeriodChoiceViewModel(
                competitionId,
                competitionResource.getName(),
                assessmentPeriods
        );
    }

    private MilestoneViewModel populateMilestoneFormEntries(MilestoneResource milestone) {
        return new MilestoneViewModel(milestone.getType(), milestone.getDate());
    }
}
