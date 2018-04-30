package org.innovateuk.ifs.competitionsetup.milestone.form;

import org.apache.commons.collections4.map.LinkedMap;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.GenericMilestoneRowForm;

/**
 * List of Milestone Form Entries for the Milestone form.
 */
public class MilestonesForm extends CompetitionSetupForm {

    private LinkedMap<String, GenericMilestoneRowForm> milestoneEntries = new LinkedMap<>();

    public LinkedMap<String, GenericMilestoneRowForm> getMilestoneEntries() {
        return milestoneEntries;
    }

    public void setMilestoneEntries(LinkedMap<String, GenericMilestoneRowForm> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }
}
