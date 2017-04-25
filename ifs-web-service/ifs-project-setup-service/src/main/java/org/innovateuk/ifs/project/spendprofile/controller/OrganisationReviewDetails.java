package org.innovateuk.ifs.project.spendprofile.controller;

/**
 * Object holding organisation details
 */
public class OrganisationReviewDetails {

    private String organisationName;
    private boolean isMarkedComplete;
    private boolean isUserPartofThisOrganisation;
    private boolean showEditLinkToUser;

    public OrganisationReviewDetails(String organisationName, boolean isMarkedComplete, boolean isUserPartofThisOrganisation, boolean showEditLinkToUser) {
        this.organisationName = organisationName;
        this.isMarkedComplete = isMarkedComplete;
        this.isUserPartofThisOrganisation = isUserPartofThisOrganisation;
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

    public boolean isUserPartofThisOrganisation() {
        return isUserPartofThisOrganisation;
    }

    public void setUserPartofThisOrganisation(boolean userPartofThisOrganisation) {
        isUserPartofThisOrganisation = userPartofThisOrganisation;
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

        if (isMarkedComplete != that.isMarkedComplete) return false;
        if (isUserPartofThisOrganisation != that.isUserPartofThisOrganisation) return false;
        if (showEditLinkToUser != that.showEditLinkToUser) return false;
        return organisationName.equals(that.organisationName);

    }

    @Override
    public int hashCode() {
        int result = organisationName.hashCode();
        result = 31 * result + (isMarkedComplete ? 1 : 0);
        result = 31 * result + (isUserPartofThisOrganisation ? 1 : 0);
        result = 31 * result + (showEditLinkToUser ? 1 : 0);
        return result;
    }
}
