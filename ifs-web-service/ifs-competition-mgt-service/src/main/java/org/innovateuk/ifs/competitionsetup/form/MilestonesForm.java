package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competitionsetup.viewmodel.MilestoneViewModel;
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
