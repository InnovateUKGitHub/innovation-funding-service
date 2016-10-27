package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.UserProfileStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link UserProfileStatusResource}s.
 */
public class UserProfileStatusResourceBuilder extends BaseBuilder<UserProfileStatusResource, UserProfileStatusResourceBuilder> {

    private UserProfileStatusResourceBuilder(List<BiConsumer<Integer, UserProfileStatusResource>> multiActions) {
        super(multiActions);
    }

    public static UserProfileStatusResourceBuilder newUserProfileStatusResource() {
        return new UserProfileStatusResourceBuilder(emptyList());
    }

    @Override
    protected UserProfileStatusResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UserProfileStatusResource>> actions) {
        return new UserProfileStatusResourceBuilder(actions);
    }

    @Override
    protected UserProfileStatusResource createInitial() {
        return createDefault(UserProfileStatusResource.class);
    }

    public UserProfileStatusResourceBuilder withSkillsComplete(Boolean... skillsCompleteList) {
        return withArray((skillsComplete, userProfileStatusResource) -> setField("skillsComplete", skillsComplete, userProfileStatusResource), skillsCompleteList);
    }

    public UserProfileStatusResourceBuilder withAffliliationsComplete(Boolean... affiliationsCompleteList) {
        return withArray((affiliationsComplete, userProfileStatusResource) -> setField("affiliationsComplete", affiliationsComplete, userProfileStatusResource), affiliationsCompleteList);
    }

    public UserProfileStatusResourceBuilder withContractComplete(Boolean... contractCompleteList) {
        return withArray((contractComplete, userProfileStatusResource) -> setField("contractComplete", contractComplete, userProfileStatusResource), contractCompleteList);
    }
}