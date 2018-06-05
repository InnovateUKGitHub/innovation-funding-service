package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;

public class NewUserStagedInviteResource extends StagedInviteResource {

    @NotBlank(message = "validation.newUserStagedInviteResource.name.required")
    private String name;

    private long innovationAreaId;

    public NewUserStagedInviteResource() {
    }

    public NewUserStagedInviteResource(String email, long competitionId, String name, long innovationCategoryId) {
        super(email, competitionId);
        this.name = name;
        this.innovationAreaId = innovationCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInnovationAreaId() {
        return innovationAreaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NewUserStagedInviteResource that = (NewUserStagedInviteResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(innovationAreaId, that.innovationAreaId)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(name)
                .append(innovationAreaId)
                .toHashCode();
    }
}
