package org.innovateuk.ifs.project.core.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.BuilderAmendFunctions;
import org.innovateuk.ifs.invite.domain.ProjectInvite;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipant;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.user.domain.User;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Superclass for {@link org.innovateuk.ifs.project.core.domain.ProjectParticipant} builders.
 */
public abstract class ProjectParticipantBuilder<T extends ProjectParticipant, B extends ProjectParticipantBuilder<T, B>>
        extends BaseBuilder<T, B> {

    private final EnumSet<ProjectParticipantRole> allowedRoles;

    protected ProjectParticipantBuilder(List<BiConsumer<Integer, T>> multiActions, EnumSet<ProjectParticipantRole> allowedRoles) {
        super(multiActions);
        this.allowedRoles = allowedRoles;
    }

    public final B withId(Long... ids) {
        return withArraySetFieldByReflection("id", ids);
    }

    protected B withRole(ProjectParticipantRole... roles) {
        if (Arrays.stream(roles).anyMatch(EnumSet.complementOf(allowedRoles)::contains)) {
            throw new IllegalArgumentException("roles can only contain " + allowedRoles);
        }

        return withArraySetFieldByReflection("role", roles);
    }

    public final B withProject(Project... projects) {
        return withArraySetFieldByReflection("project", projects);
    }

    protected B withOrganisation(Organisation... organisations) {
        return withArraySetFieldByReflection("organisation", organisations);
    }

    public final <I extends ProjectInvite<I>> ProjectParticipantBuilder withInvite(I... projectInvites) {
        return withArray((projectInvite, projectUser) -> setField("invite", projectInvite, projectUser), projectInvites);
    }

    public final B withUser(User... users) {
        return withArray(BuilderAmendFunctions::setUser, users);
    }
}