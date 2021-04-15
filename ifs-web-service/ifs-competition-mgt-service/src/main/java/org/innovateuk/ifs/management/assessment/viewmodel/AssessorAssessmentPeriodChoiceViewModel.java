package org.innovateuk.ifs.management.assessment.viewmodel;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class AssessorAssessmentPeriodChoiceViewModel {
    private final long competitionId;
    private final String competitionName;
    private final List<AssessmentPeriodViewModel> assessmentPeriods;

    public AssessorAssessmentPeriodChoiceViewModel(long competitionId, String competitionName, List<AssessmentPeriodViewModel> assessmentPeriods) {
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
        private LinkedMap<String, MilestoneViewModel> milestoneEntries;

        public Long getAssessmentPeriodId() {
            return assessmentPeriodId;
        }

        public void setAssessmentPeriodId(Long assessmentPeriodId) {
            this.assessmentPeriodId = assessmentPeriodId;
        }

        public LinkedMap<String, MilestoneViewModel> getMilestoneEntries() {
            return milestoneEntries;
        }

        public void setMilestoneEntries(LinkedMap<String, MilestoneViewModel> milestoneEntries) {
            this.milestoneEntries = milestoneEntries;
        }

        public String getDisplayName() {
            MilestoneViewModel first = milestoneEntries.get(milestoneEntries.firstKey());
            MilestoneViewModel last = milestoneEntries.get(milestoneEntries.lastKey());
            ZonedDateTime start = first.getDate();
            ZonedDateTime end = last.getDate();
            if (first.getDate().getYear() == last.getDate().getYear()) {
                return String.format("%s %s to %s %s %s",
                        start.getDayOfMonth(),
                        start.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        end.getDayOfMonth(),
                        end.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        end.getYear());
            }
            return String.format("%s %s %s to %s %s %s",
                    start.getDayOfMonth(),
                    start.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    start.getYear(),
                    end.getDayOfMonth(),
                    end.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    end.getYear());
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
