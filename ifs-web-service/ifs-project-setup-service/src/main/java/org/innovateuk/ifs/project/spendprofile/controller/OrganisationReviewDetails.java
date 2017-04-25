package org.innovateuk.ifs.project.spendprofile.controller;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Object holding organisation details
 */
public class OrganisationReviewDetails {

    private String organisationName;
    private boolean isMarkedComplete;
    private boolean isUserPartOfThisOrganisation;
    private boolean showEditLinkToUser;

    public OrganisationReviewDetails(String organisationName, boolean isMarkedComplete, boolean isUserPartOfThisOrganisation, boolean showEditLinkToUser) {
        this.organisationName = organisationName;
        this.isMarkedComplete = isMarkedComplete;
        this.isUserPartOfThisOrganisation = isUserPartOfThisOrganisation;
        this.showEditLinkToUser = showEditLinkToUser;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isMarkedComplete() {
        return isMarkedComplete;
    }

    public void setMarkedComplete(boolean markedComplete) {
        isMarkedComplete = markedComplete;
    }

    public boolean isUserPartOfThisOrganisation() {
        return isUserPartOfThisOrganisation;
    }

    public void setUserPartOfThisOrganisation(boolean userPartOfThisOrganisation) {
        isUserPartOfThisOrganisation = userPartOfThisOrganisation;
    }

    public boolean isShowEditLinkToUser() {
        return showEditLinkToUser;
    }

    public void setShowEditLinkToUser(boolean showEditLinkToUser) {
        this.showEditLinkToUser = showEditLinkToUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationReviewDetails that = (OrganisationReviewDetails) o;

        return new EqualsBuilder()
                .append(organisationName, that.organisationName)
                .append(isMarkedComplete, that.isMarkedComplete)
                .append(isUserPartOfThisOrganisation, that.isUserPartOfThisOrganisation)
                .append(showEditLinkToUser, that.showEditLinkToUser)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationName)
                .append(isMarkedComplete)
                .append(isUserPartOfThisOrganisation)
                .append(showEditLinkToUser)
                .toHashCode();
    }
}
