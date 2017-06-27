package org.innovateuk.ifs.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class EditUserViewModel {

    private String createdByUser;

    private ZonedDateTime createdOn;

    private UserResource user;

    public EditUserViewModel(String createdByUser, ZonedDateTime createdOn, UserResource user) {
        this.createdByUser = createdByUser;
        this.createdOn = createdOn;
        this.user = user;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditUserViewModel that = (EditUserViewModel) o;

        return new EqualsBuilder()
                .append(createdByUser, that.createdByUser)
                .append(createdOn, that.createdOn)
                .append(user, that.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(createdByUser)
                .append(createdOn)
                .append(user)
                .toHashCode();
    }
}
