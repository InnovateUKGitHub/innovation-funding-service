package org.innovateuk.ifs.application.common.viewmodel;

public class ApplicationSubsidyBasisPartnerRowViewModel {
    private final String name;
    private final boolean lead;
    private final Boolean northernIslandDeclaration; // This can be null when not set.
    private final boolean questionnaireMarkedAsComplete;
    private final String questionnaireResponseId;


    public ApplicationSubsidyBasisPartnerRowViewModel(String name,
                                                      boolean lead,
                                                      Boolean northernIslandDeclaration,
                                                      String questionnaireResponseId,
                                                      boolean questionnaireMarkedAsComplete) {
        this.name = name;
        this.lead = lead;
        this.northernIslandDeclaration = northernIslandDeclaration;
        this.questionnaireResponseId = questionnaireResponseId;
        this.questionnaireMarkedAsComplete = questionnaireMarkedAsComplete;
    }

    public String getName() {
        return name;
    }

    public boolean isLead() {
        return lead;
    }

    public String getQuestionaireResponseId() {
        return questionnaireResponseId;
    }

    public boolean isNorthernIslandDeclaration() {
        return northernIslandDeclaration;
    }

    public boolean isQuestionnaireMarkedAsComplete() {
        return questionnaireMarkedAsComplete;
    }

    public String getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }
}