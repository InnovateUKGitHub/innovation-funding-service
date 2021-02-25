package org.innovateuk.ifs.application.common.viewmodel;

public class ApplicationSubsidyBasisPartnerRowViewModel {
    private final String name;
    private final boolean lead;
    private final Boolean northernIslandDeclaration; // This can be null when not set.
    private final boolean questionnaireMarkedAsComplete;
    private final long applicationId;
    private final long organisationId;
    private final long questionId;


    public ApplicationSubsidyBasisPartnerRowViewModel(String name,
                                                      boolean lead,
                                                      Boolean northernIslandDeclaration,
                                                      boolean questionnaireMarkedAsComplete,
                                                      long applicationId,
                                                      long organisationId,
                                                      long questionId) {
        this.name = name;
        this.lead = lead;
        this.northernIslandDeclaration = northernIslandDeclaration;
        this.questionnaireMarkedAsComplete = questionnaireMarkedAsComplete;
        this.applicationId = applicationId;
        this.organisationId = organisationId;
        this.questionId = questionId;
    }

    public String getName() {
        return name;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isNorthernIslandDeclaration() {
        return northernIslandDeclaration;
    }

    public boolean isQuestionnaireMarkedAsComplete() {
        return questionnaireMarkedAsComplete;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public long getQuestionId() {
        return questionId;
    }
}