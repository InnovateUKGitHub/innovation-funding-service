package com.worth.ifs.competitionsetup.form;

import com.worth.ifs.competitionsetup.viewmodel.MilestoneViewModel;
import org.apache.commons.collections4.map.LinkedMap;

/**
 * List of Milestone Form Entries for the Milestone form.
 */
public class MilestonesForm extends CompetitionSetupForm {

    private LinkedMap<String, MilestoneViewModel> milestoneEntries = new LinkedMap<>();

    public LinkedMap<String, MilestoneViewModel> getMilestoneEntries() {
        return milestoneEntries;
    }

    public void setMilestoneEntries(LinkedMap<String, MilestoneViewModel> milestoneEntries) {
        this.milestoneEntries = milestoneEntries;
    }
}
