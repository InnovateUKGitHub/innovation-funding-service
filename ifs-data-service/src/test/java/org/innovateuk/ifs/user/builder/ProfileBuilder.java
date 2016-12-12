package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.domain.Contract;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link Profile}s.
 */
public class ProfileBuilder extends BaseBuilder<Profile, ProfileBuilder> {

    private ProfileBuilder(List<BiConsumer<Integer, Profile>> multiActions) {
        super(multiActions);
    }

    public static ProfileBuilder newProfile() {
        return new ProfileBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ProfileBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Profile>> actions) {
        return new ProfileBuilder(actions);
    }

    public ProfileBuilder withId(Long... ids) {
        return withArray((id, profile) -> setField("id", id, profile) , ids);
    }

    public ProfileBuilder withAddress(Address... addresses) {
        return withArray((address, profile) -> setField("address", address, profile), addresses);
    }

    public ProfileBuilder withSkillsAreas(String... skillsAreasList) {
        return withArray((skillsAreas, profile) -> setField("skillsAreas", skillsAreas, profile), skillsAreasList);
    }

    public ProfileBuilder withBusinessType(BusinessType... businessTypes) {
        return withArray((businessType, profile) -> setField("businessType", businessType, profile), businessTypes);
    }

    public ProfileBuilder withContract(Contract... contracts) {
        return withArray((contract, profile) -> setField("contract", contract, profile), contracts);
    }

    public ProfileBuilder withContractSignedDate(LocalDateTime... contractSignedDates) {
        return withArray((contractSignedDate, profile) -> setField("contractSignedDate", contractSignedDate, profile), contractSignedDates);
    }

    @Override
    protected Profile createInitial() {
        return createDefault(Profile.class);
    }
}
