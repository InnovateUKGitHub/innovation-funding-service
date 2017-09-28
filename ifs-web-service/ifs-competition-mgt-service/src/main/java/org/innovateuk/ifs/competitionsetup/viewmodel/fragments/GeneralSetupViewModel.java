package org.innovateuk.ifs.competitionsetup.viewmodel.fragments;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionStateSetupViewModel;

public class GeneralSetupViewModel {
    private boolean editable;
    private CompetitionResource competition;
    private CompetitionSetupSection section;
    private String currentSectionFragment;
    private CompetitionSetupSection[] allSections;
    private boolean isInitialComplete;
    private CompetitionStateSetupViewModel state;

    public GeneralSetupViewModel(boolean editable, CompetitionResource competition,
                                 CompetitionSetupSection section,
                                 CompetitionSetupSection[] allSections, boolean isInitialComplete) {
        this.editable = editable;
        this.competition = competition;
        this.section = section;
        this.allSections = allSections;
        this.isInitialComplete = isInitialComplete;
    }

    public void setCurrentSectionFragment(String currentSectionFragment) {
        this.currentSectionFragment = currentSectionFragment;
    }

    public void setState(CompetitionStateSetupViewModel state) {
        this.state = state;
    }

    public boolean isEditable() {
        return editable;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public CompetitionSetupSection getSection() {
        return section;
    }

    public String getCurrentSectionFragment() {
        return currentSectionFragment;
    }

    public CompetitionSetupSection[] getAllSections() {
        return allSections;
    }

    public boolean isInitialComplete() {
        return isInitialComplete;
    }

    public CompetitionStateSetupViewModel getState() {
        return state;
    }
}
