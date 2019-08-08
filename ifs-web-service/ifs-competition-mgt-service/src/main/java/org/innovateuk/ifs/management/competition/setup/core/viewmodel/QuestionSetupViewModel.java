package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

public class QuestionSetupViewModel extends CompetitionSetupViewModel {
    private CompetitionSetupSubsectionViewModel subsectionViewModel;
    private String competitionName;
    private boolean editable;
    private String templateFilename;

    public QuestionSetupViewModel(GeneralSetupViewModel generalSetupViewModel,
                                  CompetitionSetupSubsectionViewModel subsectionViewModel,
                                  String competitionName, boolean editable,
                                  String templateFilename) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.subsectionViewModel = subsectionViewModel;
        this.competitionName = competitionName;
        this.editable = editable;
        this.templateFilename = templateFilename;
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

    public String getTemplateFilename() {
        return templateFilename;
    }

    public boolean isTemplateFileUploaded() {
        return templateFilename != null;
    }

}
