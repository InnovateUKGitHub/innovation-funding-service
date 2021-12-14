package org.innovateuk.ifs.management.competition.setup.milestone.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.form.GenericMilestoneRowForm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * List of Milestone Form Entries for the Milestone form.
 */
public class MilestonesForm extends CompetitionSetupForm {

    private Map<String, GenericMilestoneRowForm> milestoneEntries = new LinkedHashMap<>();

    public Map<String, GenericMilestoneRowForm> getMilestoneEntries() {
        return milestoneEntries;
    }

    public void setMilestoneEntries(Map<String, GenericMilestoneRowForm> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }
}
