package org.innovateuk.ifs.management.assessment.viewmodel;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class AssessmentPeriodChoiceViewModel {
    private final long competitionId;
    private final String competitionName;
    private final List<AssessmentPeriodViewModel> assessmentPeriods;

    public AssessmentPeriodChoiceViewModel(long competitionId, String competitionName, List<AssessmentPeriodViewModel> assessmentPeriods) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessmentPeriods = assessmentPeriods;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<AssessmentPeriodViewModel> getAssessmentPeriods() {
        return assessmentPeriods;
    }

    public static class AssessmentPeriodViewModel {
        private Long assessmentPeriodId;
        private Map<String, MilestoneViewModel> milestoneEntries;
        private String displayName;

        public Long getAssessmentPeriodId() {
            return assessmentPeriodId;
        }

        public void setAssessmentPeriodId(Long assessmentPeriodId) {
            this.assessmentPeriodId = assessmentPeriodId;
        }

        public Map<String, MilestoneViewModel> getMilestoneEntries() {
            return milestoneEntries;
        }

        public void setMilestoneEntries(Map<String, MilestoneViewModel> milestoneEntries) {
            this.milestoneEntries = milestoneEntries;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class MilestoneViewModel {
        private MilestoneType type;
        private ZonedDateTime date;

        public MilestoneViewModel(MilestoneType type, ZonedDateTime date) {
            this.type = type;
            this.date = date;
        }

        public MilestoneType getType() {
            return type;
        }

        public ZonedDateTime getDate() {
            return date;
        }
    }
}
