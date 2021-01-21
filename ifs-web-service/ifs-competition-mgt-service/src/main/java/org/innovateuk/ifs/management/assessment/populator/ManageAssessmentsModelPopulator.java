package org.innovateuk.ifs.management.assessment.populator;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public ManageAssessmentsViewModel populateModel(long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatistics =
                competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition
                        (competitionId).getSuccess();

        Map<Long, List<MilestoneResource>> assessmentPeriods = milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                .stream()
                .filter(milestone -> milestone.getAssessmentPeriodId() != null)
                .collect(Collectors.groupingBy(MilestoneResource::getAssessmentPeriodId));

        List<MilestonesForm> milestonesForms = new ArrayList<>();
        assessmentPeriods.forEach((key, value) -> {
            LinkedMap<String, GenericMilestoneRowForm> milestoneFormEntries = new LinkedMap<>();
            value.stream().forEachOrdered(milestone ->
                    milestoneFormEntries.put(milestone.getType().name(), populateMilestoneFormEntries(milestone, competition))
            );
            MilestonesForm milestonesForm = new MilestonesForm();
            milestonesForm.setMilestoneEntries(milestoneFormEntries);
            milestonesForms.add(milestonesForm);
        });

        return new ManageAssessmentsViewModel(competition, keyStatistics, milestonesForms);
    }

    private MilestoneRowForm populateMilestoneFormEntries(MilestoneResource milestone, CompetitionResource competitionResource) {
        return new MilestoneRowForm(milestone.getType(), milestone.getDate(), false);
    }

    private boolean isEditable(MilestoneResource milestone, CompetitionResource competitionResource) {
        return !competitionResource.isSetupAndLive() || milestone.getDate().isAfter(ZonedDateTime.now());
    }

}
