package org.innovateuk.ifs.management.assessmentperiod.saver;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.competition.resource.MilestoneType.assessmentPeriodValues;

@Component
public class AssessmentPeriodSaver {

    @Autowired
    private MilestoneRestService milestoneRestService;

    @Autowired
    private AssessmentPeriodRestService assessmentPeriodRestService;

    public ServiceResult<Void> save(long competitionId, ManageAssessmentPeriodsForm form) {
        List<MilestoneResource> existingMilestones = getExistingAssessmentPeriodMilestoneResources(competitionId);

        return aggregate(form.getAssessmentPeriods().stream().flatMap(assessmentPeriodForm -> {
            Long assessmentPeriodId = assessmentPeriodForm.getAssessmentPeriodId();
            return assessmentPeriodForm.getMilestoneEntries().entrySet().stream()
                    .map(e -> {
                MilestoneRowForm milestoneRowForm = e.getValue();
                Optional<MilestoneResource> matchingMilestoneResource = existingMilestones.stream()
                        .filter(m ->
                                isEditable(m)
                                && m.getAssessmentPeriodId().equals(assessmentPeriodId)
                                && m.getType() == milestoneRowForm.getMilestoneType())
                        .findAny();

                ZonedDateTime date = milestoneRowForm.getMilestoneAsZonedDateTime();
                if (matchingMilestoneResource.isPresent()) {
                    matchingMilestoneResource.get().setDate(date);
                    return milestoneRestService.updateMilestone(matchingMilestoneResource.get()).toServiceResult();
                } else {
                    MilestoneResource milestone = new MilestoneResource();
                    milestone.setDate(date);
                    milestone.setType(milestoneRowForm.getMilestoneType());
                    milestone.setAssessmentPeriodId(assessmentPeriodId);
                    return milestoneRestService.create(milestone).toServiceResult().andOnSuccessReturnVoid();
                }
            });
        })
        .collect(Collectors.toList()))
                .andOnSuccessReturnVoid();
}

    private boolean isEditable(MilestoneResource milestone) {
        return milestone.getDate() == null || milestone.getDate().isAfter(ZonedDateTime.now());
    }

    private List<MilestoneResource> getExistingAssessmentPeriodMilestoneResources(long competitionId) {
        return milestoneRestService.getAllMilestonesByCompetitionId(competitionId).getSuccess()
                .stream()
                .filter(e -> MilestoneType.assessmentPeriodValues().contains(e.getType()))
                .collect(Collectors.toList());
    }

    public void createNewAssessmentPeriod(long competitionId) {
        AssessmentPeriodResource period = new AssessmentPeriodResource();
        period.setCompetitionId(competitionId);
        AssessmentPeriodResource savedPeriod = assessmentPeriodRestService.create(period).getSuccess();
        assessmentPeriodValues().forEach(t -> milestoneRestService.create(new MilestoneResource(t, null, competitionId, savedPeriod.getId())).getSuccess());
    }
}
