package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

public class QuestionSetupViewModel extends CompetitionSetupViewModel {
    private CompetitionSetupSubsectionViewModel subsectionViewModel;
    private String competitionName;
    private boolean editable;
    private String templateFilename;
    private boolean displayAssessmentOptions;

    public QuestionSetupViewModel(GeneralSetupViewModel generalSetupViewModel,
                                  CompetitionSetupSubsectionViewModel subsectionViewModel,
                                  String competitionName, boolean editable,
                                  String templateFilename,
                                  boolean displayAssessmentOptions) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.subsectionViewModel = subsectionViewModel;
        this.competitionName = competitionName;
        this.editable = editable;
        this.templateFilename = templateFilename;
        this.displayAssessmentOptions = displayAssessmentOptions;
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

    public boolean isDisplayAssessmentOptions() {
        return displayAssessmentOptions;
    }
}
