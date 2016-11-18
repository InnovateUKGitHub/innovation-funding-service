package com.worth.ifs.user.builder;


import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.resource.ProfileContractResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.createDefault;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

/**
 * Builder for {@link ProfileContractResource}s
 */
public class ProfileContractResourceBuilder extends BaseBuilder<ProfileContractResource, ProfileContractResourceBuilder> {

    private ProfileContractResourceBuilder(List<BiConsumer<Integer, ProfileContractResource>> multiActions) {
        super(multiActions);
    }

    public static ProfileContractResourceBuilder newProfileContractResource() {
        return new ProfileContractResourceBuilder(emptyList());
    }

    @Override
    protected ProfileContractResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ProfileContractResource>> actions) {
        return new ProfileContractResourceBuilder(actions);
    }

    @Override
    protected ProfileContractResource createInitial() {
        return createDefault(ProfileContractResource.class);
    }

    public ProfileContractResourceBuilder withUser(Long... users) {
        return withArray((user, profileContractResource) -> setField("user", user, profileContractResource), users);
    }

    public ProfileContractResourceBuilder withContract(ContractResource... contracts) {
        return withArray((contract, profileContractResource) -> setField("contract", contract, profileContractResource), contracts);
    }

    public ProfileContractResourceBuilder withCurrentAgreement(Boolean... currentAgreements) {
        return withArray((currentAgreement, profileContractResource) -> setField("currentAgreement", currentAgreement, profileContractResource), currentAgreements);
    }

    public ProfileContractResourceBuilder withContractSignedDate(LocalDateTime... contractSignedDates) {
        return withArray((contractSignedDate, profileContractResource) -> setField("contractSignedDate", contractSignedDate, profileContractResource), contractSignedDates);
    }
}