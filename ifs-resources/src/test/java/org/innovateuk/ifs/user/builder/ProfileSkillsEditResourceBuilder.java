package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Builder for {@link ProfileSkillsEditResource}s.
 */
public class ProfileSkillsEditResourceBuilder extends BaseBuilder<ProfileSkillsEditResource, ProfileSkillsEditResourceBuilder> {

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

    public ProfileSkillsEditResourceBuilder withUser(Long... users) {
        return withArray((user, profileSkillsEditResource) -> setField("user", user, profileSkillsEditResource), users);
    }

    public ProfileSkillsEditResourceBuilder withSkillsAreas(String... skillsAreas) {
        return withArray((skillsArea, profileSkillsEditResource) -> setField("skillsAreas", skillsArea, profileSkillsEditResource), skillsAreas);
    }

    public ProfileSkillsEditResourceBuilder withBusinessType(BusinessType... businessTypes) {
        return withArray((businessType, profileSkillsEditResource) -> setField("businessType", businessType, profileSkillsEditResource), businessTypes);
    }
}