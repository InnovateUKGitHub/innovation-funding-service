package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.resource.ProfileAddressResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.createDefault;
import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProfileAddressResource}s.
 */
public class ProfileAddressResourceBuilder extends BaseBuilder<ProfileAddressResource, ProfileAddressResourceBuilder> {

    private ProfileAddressResourceBuilder(List<BiConsumer<Integer, ProfileAddressResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileAddressResourceBuilder newProfileAddressResource() {
        return new ProfileAddressResourceBuilder(emptyList());
    }

    @Override
    protected ProfileAddressResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileAddressResource>> actions) {
        return new ProfileAddressResourceBuilder(actions);
    }

    @Override
    protected ProfileAddressResource createInitial() {
        return createDefault(ProfileAddressResource.class);
    }

    public ProfileAddressResourceBuilder withUser(Long... users) {
        return withArray((user, ProfileAddressResource) -> setField("user", user, ProfileAddressResource), users);
    }

    public ProfileAddressResourceBuilder withAddress(AddressResource... addresses) {
        return withArray((address, profileSkillsResource) -> setField("address", address, profileSkillsResource), addresses);
    }
}
