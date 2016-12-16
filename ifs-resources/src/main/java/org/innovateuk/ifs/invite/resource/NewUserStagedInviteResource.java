package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

public class NewUserStagedInviteResource extends StagedInviteResource {

    @NotEmpty(message = "validation.newUserStagedInviteResource.name.required")
    private String name;

    private long innovationCategoryId;

    public NewUserStagedInviteResource() {
    }

    public NewUserStagedInviteResource(String email, long competitionId, String name, long innovationCategoryId) {
        super(email, competitionId);
        this.name = name;
        this.innovationCategoryId = innovationCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInnovationCategoryId() {
        return innovationCategoryId;
    }

    public void setInnovationCategoryId(long innovationCategoryId) {
        this.innovationCategoryId = innovationCategoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NewUserStagedInviteResource that = (NewUserStagedInviteResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(innovationCategoryId, that.innovationCategoryId)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(name)
                .append(innovationCategoryId)
                .toHashCode();
    }
}
