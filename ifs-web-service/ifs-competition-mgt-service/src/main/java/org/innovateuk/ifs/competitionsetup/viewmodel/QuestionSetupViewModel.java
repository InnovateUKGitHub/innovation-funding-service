package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

public class QuestionSetupViewModel extends CompetitionSetupViewModel {
    private CompetitionSetupSubsectionViewModel subsectionViewModel;
    private String competitionName;
    private boolean editable;

    public QuestionSetupViewModel(GeneralSetupViewModel generalSetupViewModel,
                                  CompetitionSetupSubsectionViewModel subsectionViewModel,
                                  String competitionName, boolean editable) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.subsectionViewModel = subsectionViewModel;
        this.competitionName = competitionName;
        this.editable = editable;
    }

    public CompetitionSetupSubsectionViewModel getSubsection() {
        return subsectionViewModel;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isEditable() {
        return editable;
    }
}
