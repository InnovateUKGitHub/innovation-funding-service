package org.innovateuk.ifs.competition.resource;

import java.util.List;

public class AssessmentPeriodResource extends MilestoneBaseResource {

    private List<MilestoneResource> children;

    public AssessmentPeriodResource() {
    }

    public AssessmentPeriodResource(MilestoneType type, Long competitionId) {
        super(type, competitionId);
    }

    public List<MilestoneResource> getChildren() {
        return children;
    }

    public void setChildren(List<MilestoneResource> children) {
        this.children = children;
    }
}
