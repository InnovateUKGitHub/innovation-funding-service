package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public final class ApplicationUserCompositeId implements Serializable {

    private long applicationId;

    private long userId;

    public static ApplicationUserCompositeId id(long applicationId, long userId){
        return new ApplicationUserCompositeId(applicationId, userId);
    }

    public ApplicationUserCompositeId() {}

    public ApplicationUserCompositeId(long applicationId, long userId) {

        this.applicationId = applicationId;
        this.userId = userId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationUserCompositeId that = (ApplicationUserCompositeId) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(userId, that.userId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(userId)
                .toHashCode();
    }
}
