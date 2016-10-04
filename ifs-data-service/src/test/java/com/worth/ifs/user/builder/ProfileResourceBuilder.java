package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.resource.BusinessType;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.resource.ProfileResource;
import com.worth.ifs.user.resource.UserResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.*;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link com.worth.ifs.user.resource.ProfileResource}.
 */
public class ProfileResourceBuilder extends BaseBuilder<ProfileResource, ProfileResourceBuilder> {

    private ProfileResourceBuilder(List<BiConsumer<Integer, ProfileResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileResourceBuilder newProfileResource() {
        return new ProfileResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProfileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileResource>> actions) {
        return new ProfileResourceBuilder(actions);
    }

    public ProfileResourceBuilder withId(Long... ids) {
        return withArray((id, profile) -> setField("id", id, profile) , ids);
    }

    public ProfileResourceBuilder withUser(UserResource... users) {
        return withArray((user, profile) -> setField("user", user, profile), users);
    }

    public ProfileResourceBuilder withAddress(AddressResource... addresses) {
        return withArray((address, profile) -> setField("address", address, profile), addresses);
    }

    public ProfileResourceBuilder withSkillsAreas(String... skillsAreasList) {
        return withArray((skillsAreas, profile) -> setField("skillsAreas", skillsAreas, profile), skillsAreasList);
    }

    public ProfileResourceBuilder withBusinessType(BusinessType... businessTypes) {
        return withArray((businessType, profile) -> setField("businessType", businessType, profile), businessTypes);
    }

    public ProfileResourceBuilder withContract(ContractResource... contracts) {
        return withArray((contract, profile) -> setField("contract", contract, profile), contracts);
    }

    public ProfileResourceBuilder withContractSignedDate(LocalDateTime... contractSignedDates) {
        return withArray((contractSignedDate, profile) -> setField("contractSignedDate", contractSignedDate, profile), contractSignedDates);
    }

    @Override
    protected ProfileResource createInitial() {
        return createDefault(ProfileResource.class);
    }
}
