package org.innovateuk.ifs.management.assessmentperiod.model;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.util.List;

public class AssessmentPeriodViewModel {

    private Long assessmentPeriodId;
    private boolean hasAssessorsToNotify;

    private List<AssessmentMilestoneViewModel> milestoneEntries;

    public void setMilestoneEntries(List<AssessmentMilestoneViewModel> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }

    public List<AssessmentMilestoneViewModel> getMilestoneEntries() {
        return milestoneEntries;
    }

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public void setAssessmentPeriodId(Long assessmentPeriodId) {
        this.assessmentPeriodId = assessmentPeriodId;
    }

    public boolean hasAssessorsNotifiedMilestone() {
        return this.milestoneEntries.stream().anyMatch(milestoneEntries -> MilestoneType.ASSESSORS_NOTIFIED.equals(milestoneEntries.getMilestoneType()));
    }

    public boolean hasAssessmentClosedMilestone() {
        return this.milestoneEntries.stream().anyMatch(milestoneEntries -> MilestoneType.ASSESSMENT_CLOSED.equals(milestoneEntries.getMilestoneType()));
    }

    public boolean hasAssessorsToNotify() {
        return hasAssessorsToNotify;
    }

    public void setHasAssessorsToNotify(boolean hasAssessorsToNotify) {
        this.hasAssessorsToNotify = hasAssessorsToNotify;
    }
}
