package org.innovateuk.ifs.competitionsetup.viewmodel;

public class QuestionSetupViewModel extends CompetitionSetupViewModel {
    private CompetitionSetupSubsectionViewModel subsectionViewModel;
    private String competitionName;
    private boolean editable;

    public QuestionSetupViewModel(CompetitionSetupSubsectionViewModel subsectionViewModel, String competitionName, boolean editable) {
        this.subsectionViewModel = subsectionViewModel;
        this.competitionName = competitionName;
        this.editable = editable;
    }

    public CompetitionSetupSubsectionViewModel getSubsectionViewModel() {
        return subsectionViewModel;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isEditable() {
        return editable;
    }
}
