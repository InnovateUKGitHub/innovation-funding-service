package org.innovateuk.ifs.management.assessmentperiod.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestoneRowForm;

import java.util.List;

public class AssessmentPeriodMilestonesForm extends BaseBindingResultTarget {

    List<MilestoneRowForm> milestoneEntries;

    public List<MilestoneRowForm> getMilestoneEntries() {
        return milestoneEntries;
    }

    public void setMilestoneEntries(List<MilestoneRowForm> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }
}
