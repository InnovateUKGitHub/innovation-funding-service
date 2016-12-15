package org.innovateuk.ifs.competitionsetup.form;

import org.apache.commons.collections4.map.LinkedMap;

/**
 * List of Milestone Form Entries for the Milestone form.
 */
public class MilestonesForm extends CompetitionSetupForm {

    private LinkedMap<String, MilestoneRowForm> milestoneEntries = new LinkedMap<>();

    public LinkedMap<String, MilestoneRowForm> getMilestoneEntries() {
        return milestoneEntries;
    }

    public void setMilestoneEntries(LinkedMap<String, MilestoneRowForm> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }
}
