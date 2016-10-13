package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.BusinessType;
import com.worth.ifs.user.resource.ProfileSkillsResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProfileSkillsResource}s.
 */
public class ProfileSkillsResourceBuilder extends BaseBuilder<ProfileSkillsResource, ProfileSkillsResourceBuilder> {

    private ProfileSkillsResourceBuilder(List<BiConsumer<Integer, ProfileSkillsResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileSkillsResourceBuilder newProfileSkillsResource() {
        return new ProfileSkillsResourceBuilder(emptyList());
    }

    @Override
    protected ProfileSkillsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileSkillsResource>> actions) {
        return new ProfileSkillsResourceBuilder(actions);
    }

    @Override
    protected ProfileSkillsResource createInitial() {
        return createDefault(ProfileSkillsResource.class);
    }

    public ProfileSkillsResourceBuilder withUser(Long... users) {
        return withArray((user, profileSkillsResource) -> setField("user", user, profileSkillsResource), users);
    }

    public ProfileSkillsResourceBuilder withSkillsAreas(String... skillsAreass) {
        return withArray((skillsAreas, profileSkillsResource) -> setField("skillsAreas", skillsAreas, profileSkillsResource), skillsAreass);
    }

    public ProfileSkillsResourceBuilder withBusinessType(BusinessType... businessTypes) {
        return withArray((businessType, profileSkillsResource) -> setField("businessType", businessType, profileSkillsResource), businessTypes);
    }
}