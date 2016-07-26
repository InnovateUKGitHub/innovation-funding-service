package com.worth.ifs.competitionsetup.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the milestones competition setup section.
 */
public class MilestonesForm extends CompetitionSetupForm {

    public List<MilestonesFormEntry> milestonesFormEntryList = new ArrayList<>();

    public List<MilestonesFormEntry> getMilestonesFormEntryList() {
        return milestonesFormEntryList;
    }

    public void setMilestonesFormEntryList(List<MilestonesFormEntry> milestonesFormEntryList) {
        this.milestonesFormEntryList = milestonesFormEntryList;
    }
}
