package org.innovateuk.ifs.application.forms.sections.procurement.milestones.form;

import java.util.Map;

public class ProcurementMilestonesForm {

    private Map<String, ProcurementMilestoneForm> milestones;

    public Map<String, ProcurementMilestoneForm> getMilestones() {
        return milestones;
    }

    public void setMilestones(Map<String, ProcurementMilestoneForm> milestones) {
        this.milestones = milestones;
    }
}
