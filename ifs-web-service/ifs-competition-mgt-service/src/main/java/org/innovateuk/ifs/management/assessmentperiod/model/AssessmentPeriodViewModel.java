package org.innovateuk.ifs.management.assessmentperiod.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.util.List;
import java.util.Optional;

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
        return assessmentMilestoneViewModel(ASSESSORS_NOTIFIED).isPresent();
    }

    public boolean hasAssessmentClosedMilestone() {
        return assessmentMilestoneViewModel(ASSESSMENT_CLOSED).isPresent();
    }

    private Optional<AssessmentMilestoneViewModel> assessmentMilestoneViewModel(MilestoneType milestoneType){
        return milestones.stream().filter(milestone -> milestoneType.equals(milestone.getMilestoneType())).findFirst();
    }

    public boolean hasAssessorsToNotify() {
        return hasAssessorsToNotify;
    }

    public void setHasAssessorsToNotify(boolean hasAssessorsToNotify) {
        this.hasAssessorsToNotify = hasAssessorsToNotify;
    }

    public boolean canNotifyAssessors(){
        return hasAssessorsToNotify || !hasAssessorsNotifiedMilestone();
    }

    public boolean canCloseAssessment() {
        return !hasAssessorsToNotify &&
                hasAssessorsNotifiedMilestone() &&
                !hasAssessmentClosedMilestone() &&
                assessmentMilestoneViewModel(ASSESSOR_DEADLINE).map(AssessmentMilestoneViewModel::isPast).orElse(false);
    }

    public boolean isValid(){
        return milestones
                .stream()
                .filter(milestone -> milestone.getDate() != null)
                .map(AssessmentMilestoneViewModel::getMilestoneType)
                .collect(toList())
                .containsAll(of(ASSESSOR_BRIEFING, ASSESSOR_DEADLINE, ASSESSOR_ACCEPTS));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentPeriodViewModel that = (AssessmentPeriodViewModel) o;

        return new EqualsBuilder()
                .append(hasAssessorsToNotify, that.hasAssessorsToNotify)
                .append(assessmentPeriodId, that.assessmentPeriodId)
                .append(milestones, that.milestones)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentPeriodId)
                .append(hasAssessorsToNotify)
                .append(milestones)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "AssessmentPeriodViewModel{" +
                "assessmentPeriodId=" + assessmentPeriodId +
                ", hasAssessorsToNotify=" + hasAssessorsToNotify +
                ", milestones=" + milestones +
                '}';
    }
}
