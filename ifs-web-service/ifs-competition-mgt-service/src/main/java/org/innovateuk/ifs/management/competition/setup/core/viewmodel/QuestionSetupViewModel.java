package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.competition.resource.ApplicationFinanceType;
import org.springframework.beans.factory.annotation.Value;

public class QuestionSetupViewModel extends CompetitionSetupViewModel {
    private CompetitionSetupSubsectionViewModel subsectionViewModel;
    private String competitionName;
    private boolean editable;
    private String templateFilename;
    private boolean displayAssessmentOptions;
    private boolean ifsLoanPartBEnabled;
    private String salesForceURL;

    public QuestionSetupViewModel(GeneralSetupViewModel generalSetupViewModel,
                                  CompetitionSetupSubsectionViewModel subsectionViewModel,
                                  String competitionName, boolean editable,
                                  String templateFilename,
                                  boolean displayAssessmentOptions,
                                  boolean ifsLoanPartBEnabled,
                                  String salesForceURL) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.subsectionViewModel = subsectionViewModel;
        this.competitionName = competitionName;
        this.editable = editable;
        this.templateFilename = templateFilename;
        this.displayAssessmentOptions = displayAssessmentOptions;
        this.ifsLoanPartBEnabled = ifsLoanPartBEnabled;
        this.salesForceURL = salesForceURL;
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

    @JsonIgnore
    public ApplicationFinanceType standardApplicationFinanceType() {
        return ApplicationFinanceType.STANDARD;
    }

    @JsonIgnore
    public ApplicationFinanceType noFinancesApplicationFinanceType() {
        return ApplicationFinanceType.NO_FINANCES;
    }

    public String getSalesForceURL() {
        return salesForceURL;
    }

    public boolean isIFSLoanPartBEnabled() {
        return ifsLoanPartBEnabled;
    }
}
