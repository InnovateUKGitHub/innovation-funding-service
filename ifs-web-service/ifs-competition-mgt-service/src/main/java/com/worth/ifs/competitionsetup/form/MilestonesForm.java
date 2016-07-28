package com.worth.ifs.competitionsetup.form;

import java.util.ArrayList;
import java.util.List;

/**
 * List of fields for the milestones competition setup section.
 */
public class MilestonesForm extends CompetitionSetupForm {

    private List<MilestonesFormEntry> milestonesFormEntryList = new ArrayList<>();

    public List<MilestonesFormEntry> getMilestonesFormEntryList() {
        return milestonesFormEntryList;
    }

    public void setMilestonesFormEntryList(List<MilestonesFormEntry> milestonesFormEntryList) {
        this.milestonesFormEntryList = milestonesFormEntryList;
    }
}
