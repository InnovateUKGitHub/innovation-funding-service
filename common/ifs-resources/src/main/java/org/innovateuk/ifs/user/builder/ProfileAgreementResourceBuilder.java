package org.innovateuk.ifs.user.builder;


import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.AgreementResource;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Builder for {@link ProfileAgreementResource}s
 */
public class ProfileAgreementResourceBuilder extends BaseBuilder<ProfileAgreementResource, ProfileAgreementResourceBuilder> {

    private ProfileAgreementResourceBuilder(List<BiConsumer<Integer, ProfileAgreementResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileAgreementResourceBuilder newProfileAgreementResource() {
        return new ProfileAgreementResourceBuilder(emptyList());
    }

    @Override
    protected ProfileAgreementResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileAgreementResource>> actions) {
        return new ProfileAgreementResourceBuilder(actions);
    }

    @Override
    protected ProfileAgreementResource createInitial() {
        return createDefault(ProfileAgreementResource.class);
    }

    public ProfileAgreementResourceBuilder withUser(Long... users) {
        return withArray((user, profileAgreementResource) -> setField("user", user, profileAgreementResource), users);
    }

    public ProfileAgreementResourceBuilder withAgreement(AgreementResource... agreements) {
        return withArray((agreement, profileAgreementResource) -> setField("agreement", agreement, profileAgreementResource), agreements);
    }

    public ProfileAgreementResourceBuilder withCurrentAgreement(Boolean... currentAgreements) {
        return withArray((currentAgreement, profileAgreementResource) -> setField("currentAgreement", currentAgreement, profileAgreementResource), currentAgreements);
    }

    public ProfileAgreementResourceBuilder withAgreementSignedDate(ZonedDateTime... agreementSignedDates) {
        return withArray((agreementSignedDate, profileAgreementResource) -> setField("agreementSignedDate", agreementSignedDate, profileAgreementResource), agreementSignedDates);
    }
}
