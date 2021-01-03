package org.innovateuk.ifs.competition.resource;

import java.time.ZonedDateTime;

public class MilestoneResource extends MilestoneBaseResource {

    private Long assessmentPeriodId;

    public MilestoneResource() {
    }

    public MilestoneResource(MilestoneType type, Long competitionId) {
        super(type, competitionId);
    }

    public MilestoneResource(MilestoneType type, ZonedDateTime date, Long competitionId) {
        super(type, date, competitionId);
    }

    public MilestoneResource(MilestoneType type, ZonedDateTime date, Long competitionId, Long assessmentPeriodId) {
        super(type, date, competitionId);
        this.assessmentPeriodId = assessmentPeriodId;
    }

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public void setAssessmentPeriodId(Long assessmentPeriodId) {
        this.assessmentPeriodId = assessmentPeriodId;
    }
}
