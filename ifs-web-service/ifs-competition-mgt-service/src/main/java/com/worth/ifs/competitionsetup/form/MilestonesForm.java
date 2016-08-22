package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.competitionsetup.model.MilestoneEntry;
import org.apache.commons.collections4.map.LinkedMap;

/**
 * List of Milestone Form Entries for the Milestone form.
 */
public class MilestonesForm extends CompetitionSetupForm {

    private LinkedMap<String, MilestoneEntry> milestoneEntries = new LinkedMap<>();

    public LinkedMap<String, MilestoneEntry> getMilestoneEntries() {
        return milestoneEntries;
    }

    public void setMilestoneEntries(LinkedMap<String, MilestoneEntry> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }
}
