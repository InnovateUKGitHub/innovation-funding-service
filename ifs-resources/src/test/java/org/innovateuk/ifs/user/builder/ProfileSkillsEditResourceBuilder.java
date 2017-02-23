package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;

/**
 * Builder for {@link ProfileSkillsEditResource}s.
 */
public class ProfileSkillsEditResourceBuilder extends ProfileSkillsBaseResourceBuilder<ProfileSkillsEditResource, ProfileSkillsEditResourceBuilder> {

    private ProfileSkillsEditResourceBuilder(List<BiConsumer<Integer, ProfileSkillsEditResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileSkillsEditResourceBuilder newProfileSkillsEditResource() {
        return new ProfileSkillsEditResourceBuilder(emptyList());
    }

    @Override
    protected ProfileSkillsEditResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileSkillsEditResource>> actions) {
        return new ProfileSkillsEditResourceBuilder(actions);
    }

    @Override
    protected ProfileSkillsEditResource createInitial() {
        return createDefault(ProfileSkillsEditResource.class);
    }
}