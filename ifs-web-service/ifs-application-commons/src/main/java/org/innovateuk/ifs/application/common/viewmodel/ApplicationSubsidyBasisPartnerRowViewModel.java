package org.innovateuk.ifs.application.common.viewmodel;

public class ApplicationSubsidyBasisPartnerRowViewModel {
    private final String name;
    private final boolean lead;
    private final boolean northernIslandDeclaration;
    private final boolean questionnareMarkedAsComplete;
    private final String questionnaireResponseId;


    public ApplicationSubsidyBasisPartnerRowViewModel(String name,
                                                      boolean lead,
                                                      boolean northernIslandDeclaration,
                                                      String questionnaireResponseId,
                                                      boolean questionnaireMarkedAsComplete) {
        this.name = name;
        this.lead = lead;
        this.northernIslandDeclaration = northernIslandDeclaration;
        this.questionnaireResponseId = questionnaireResponseId;
        this.questionnareMarkedAsComplete = questionnaireMarkedAsComplete;
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

    public boolean isQuestionnareMarkedAsComplete() {
        return questionnareMarkedAsComplete;
    }

    public String getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }
}