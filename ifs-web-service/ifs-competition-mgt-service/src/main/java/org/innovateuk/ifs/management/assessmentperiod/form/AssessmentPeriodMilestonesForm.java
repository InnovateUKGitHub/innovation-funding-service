package org.innovateuk.ifs.management.assessmentperiod.form;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;

import java.util.Map;

public class AssessmentPeriodMilestonesForm extends BaseBindingResultTarget {

    private Long assessmentPeriodId;
    LinkedMap<String, MilestoneRowForm> milestoneEntries;

    public void setMilestoneEntries(LinkedMap<String, MilestoneRowForm> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }

    public Map<String, MilestoneRowForm> getMilestoneEntries() {
        return milestoneEntries;
    }

    public Long getAssessmentPeriodId() {
        return assessmentPeriodId;
    }

    public void setAssessmentPeriodId(Long assessmentPeriodId) {
        this.assessmentPeriodId = assessmentPeriodId;
    }
}
