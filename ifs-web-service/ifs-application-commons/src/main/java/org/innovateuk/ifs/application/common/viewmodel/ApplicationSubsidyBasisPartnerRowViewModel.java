package org.innovateuk.ifs.application.common.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public Boolean isNorthernIslandDeclaration() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationSubsidyBasisPartnerRowViewModel that = (ApplicationSubsidyBasisPartnerRowViewModel) o;

        return new EqualsBuilder()
                .append(lead, that.lead)
                .append(questionnaireMarkedAsComplete, that.questionnaireMarkedAsComplete)
                .append(applicationId, that.applicationId)
                .append(organisationId, that.organisationId)
                .append(questionId, that.questionId)
                .append(name, that.name)
                .append(northernIslandDeclaration, that.northernIslandDeclaration)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(lead)
                .append(northernIslandDeclaration)
                .append(questionnaireMarkedAsComplete)
                .append(applicationId)
                .append(organisationId)
                .append(questionId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "ApplicationSubsidyBasisPartnerRowViewModel{" +
                "name='" + name + '\'' +
                ", lead=" + lead +
                ", northernIslandDeclaration=" + northernIslandDeclaration +
                ", questionnaireMarkedAsComplete=" + questionnaireMarkedAsComplete +
                ", applicationId=" + applicationId +
                ", organisationId=" + organisationId +
                ", questionId=" + questionId +
                '}';
    }
}