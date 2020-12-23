package org.innovateuk.ifs.competition.resource;

import java.time.ZonedDateTime;

public class MilestoneResource extends MilestoneBaseResource {

    private Long assessmentPeriod;

    public MilestoneResource() {
    }

    public MilestoneResource(MilestoneType type, ZonedDateTime date, Long competitionId) {
        super(type, date, competitionId);
    }

    public Long getAssessmentPeriod() {
        return assessmentPeriod;
    }

    public void setAssessmentPeriod(Long assessmentPeriod) {
        this.assessmentPeriod = assessmentPeriod;
    }
}
