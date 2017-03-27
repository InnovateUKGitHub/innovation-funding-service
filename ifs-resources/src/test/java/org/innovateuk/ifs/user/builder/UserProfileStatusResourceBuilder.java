package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;
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

    public UserProfileStatusResourceBuilder withUser(Long... users) {
        return withArraySetFieldByReflection("user", users);
    }

    public UserProfileStatusResourceBuilder withSkillsComplete(Boolean... skillsCompleteList) {
        return withArraySetFieldByReflection("skillsComplete", skillsCompleteList);
    }

    public UserProfileStatusResourceBuilder withAffliliationsComplete(Boolean... affiliationsCompleteList) {
        return withArraySetFieldByReflection("affiliationsComplete", affiliationsCompleteList);
    }

    public UserProfileStatusResourceBuilder withAgreementComplete(Boolean... agreementCompleteList) {
        return withArraySetFieldByReflection("agreementComplete", agreementCompleteList);
    }
}
