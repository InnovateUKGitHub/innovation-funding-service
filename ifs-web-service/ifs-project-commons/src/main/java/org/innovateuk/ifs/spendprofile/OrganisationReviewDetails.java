package org.innovateuk.ifs.spendprofile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;

/**
 * Object holding organisation details
 */
public class OrganisationReviewDetails {

    private Long organisationId;
    private String organisationName;
    private boolean isMarkedComplete;
    private boolean isUserPartOfThisOrganisation;
    private boolean showEditLinkToUser;
    private UserResource reviewedBy;
    private ZonedDateTime reviewedOn;

    public OrganisationReviewDetails(Long organisationId, String organisationName, boolean isMarkedComplete, boolean isUserPartOfThisOrganisation,
                                     boolean showEditLinkToUser, UserResource reviewedBy, ZonedDateTime reviewedOn) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.isMarkedComplete = isMarkedComplete;
        this.isUserPartOfThisOrganisation = isUserPartOfThisOrganisation;
        this.showEditLinkToUser = showEditLinkToUser;
        this.reviewedBy = reviewedBy;
        this.reviewedOn = reviewedOn;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
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

    public UserResource getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UserResource reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public ZonedDateTime getReviewedOn() {
        return reviewedOn;
    }

    public void setReviewedOn(ZonedDateTime reviewedOn) {
        this.reviewedOn = reviewedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationReviewDetails that = (OrganisationReviewDetails) o;

        return new EqualsBuilder()
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .append(isMarkedComplete, that.isMarkedComplete)
                .append(isUserPartOfThisOrganisation, that.isUserPartOfThisOrganisation)
                .append(showEditLinkToUser, that.showEditLinkToUser)
                .append(reviewedBy, that.reviewedBy)
                .append(reviewedOn, that.reviewedOn)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationId)
                .append(organisationName)
                .append(isMarkedComplete)
                .append(isUserPartOfThisOrganisation)
                .append(showEditLinkToUser)
                .append(reviewedBy)
                .append(reviewedOn)
                .toHashCode();
    }
}
