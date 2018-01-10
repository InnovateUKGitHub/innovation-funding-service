package org.innovateuk.ifs.competitionsetup.form;

import org.apache.commons.collections4.map.LinkedMap;

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
