package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.EnumSet.of;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;

public class AssessmentPeriodViewModel {

    private Long assessmentPeriodId;
    private boolean hasAssessorsToNotify;

    private List<AssessmentMilestoneViewModel> milestones;

    public List<AssessmentMilestoneViewModel> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<AssessmentMilestoneViewModel> milestones) {
        this.milestones = milestones;
    }

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public void setAssessmentPeriodId(Long assessmentPeriodId) {
        this.assessmentPeriodId = assessmentPeriodId;
    }

    public boolean hasAssessorsNotifiedMilestone() {
        return this.milestones.stream().anyMatch(milestone -> ASSESSORS_NOTIFIED.equals(milestone.getMilestoneType()));
    }

    public boolean hasAssessmentClosedMilestone() {
        return this.milestones.stream().anyMatch(milestone -> ASSESSMENT_CLOSED.equals(milestone.getMilestoneType()));
    }

    public boolean hasAssessorsToNotify() {
        return hasAssessorsToNotify;
    }

    public void setHasAssessorsToNotify(boolean hasAssessorsToNotify) {
        this.hasAssessorsToNotify = hasAssessorsToNotify;
    }

    public boolean isValid(){
        return milestones
                .stream()
                .filter(milestone -> milestone.getDate() != null)
                .map(AssessmentMilestoneViewModel::getMilestoneNameType)
                .collect(toList())
                .containsAll(of(ASSESSOR_BRIEFING, ASSESSOR_DEADLINE, ASSESSOR_ACCEPTS));
    }
}
