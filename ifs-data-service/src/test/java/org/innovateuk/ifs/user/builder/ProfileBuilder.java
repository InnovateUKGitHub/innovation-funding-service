package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.*;

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

    public static ProfileBuilder newProfileWithoutId() {
        return new ProfileBuilder(emptyList());
    }

    @Override
    protected ProfileBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Profile>> actions) {
        return new ProfileBuilder(actions);
    }

    public ProfileBuilder withId(Long... ids) {
        return withArray((id, profile) -> setField("id", id, profile), ids);
    }

    public ProfileBuilder withAddress(Address... addresses) {
        return withArray((address, profile) -> setField("address", address, profile), addresses);
    }

    public ProfileBuilder withSkillsAreas(String... skillsAreasList) {
        return withArray((skillsAreas, profile) -> setField("skillsAreas", skillsAreas, profile), skillsAreasList);
    }

    public ProfileBuilder withInnovationArea(InnovationArea... innovationAreas) {
        return withArray((innovationArea, profile) -> {
            profile.addInnovationArea(innovationArea);
        }, innovationAreas);
    }

    public ProfileBuilder withInnovationAreas(List<InnovationArea>... innovationAreaLists) {
        return withArray((innovationAreaList, profile) -> innovationAreaList.forEach(profile::addInnovationArea), innovationAreaLists);
    }

    public ProfileBuilder withBusinessType(BusinessType... businessTypes) {
        return withArray((businessType, profile) -> setField("businessType", businessType, profile), businessTypes);
    }

    public ProfileBuilder withAgreement(Agreement... agreements) {
        return withArray((agreement, profile) -> setField("agreement", agreement, profile), agreements);
    }

    public ProfileBuilder withAgreementSignedDate(ZonedDateTime... agreementSignedDates) {
        return withArray((agreementSignedDate, profile) -> setField("agreementSignedDate", agreementSignedDate, profile), agreementSignedDates);
    }

    @Override
    protected Profile createInitial() {
        return createDefault(Profile.class);
    }
}
