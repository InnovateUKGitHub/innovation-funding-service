package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link AssessorProfileResource}.
 */
public class AssessorProfileResourceBuilder extends BaseBuilder<AssessorProfileResource, AssessorProfileResourceBuilder> {

    public static AssessorProfileResourceBuilder newAssessorProfileResource() {
        return new AssessorProfileResourceBuilder(emptyList());
    }

    public AssessorProfileResourceBuilder(List<BiConsumer<Integer, AssessorProfileResource>> newActions) {
        super(newActions);
    }

    public AssessorProfileResourceBuilder withUser(UserResource... users) {
        return withArraySetFieldByReflection("user", users);
    }

    public AssessorProfileResourceBuilder withProfile(ProfileResource... profiles) {
        return withArraySetFieldByReflection("profile", profiles);
    }

    @Override
    protected AssessorProfileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessorProfileResource>> actions) {
        return new AssessorProfileResourceBuilder(actions);
    }

    @Override
    protected AssessorProfileResource createInitial() {
        return new AssessorProfileResource();
    }
}
