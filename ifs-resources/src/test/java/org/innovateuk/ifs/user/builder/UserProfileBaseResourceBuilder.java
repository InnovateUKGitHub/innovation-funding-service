package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.EthnicityResource;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.UserProfileBaseResource;

import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

public abstract class UserProfileBaseResourceBuilder<T extends UserProfileBaseResource, S extends UserProfileBaseResourceBuilder> extends BaseBuilder<T, S> {

    protected UserProfileBaseResourceBuilder(List<BiConsumer<Integer, T>> newActions) {
        super(newActions);
    }

    public S withTitle(String... titles) {
        return withArray((title, userProfileResource) -> setField("title", title, userProfileResource), titles);
    }

    public S withFirstName(String... firstNames) {
        return withArray((firstName, userProfileResource) -> setField("firstName", firstName, userProfileResource), firstNames);
    }

    public S withLastName(String... lastNames) {
        return withArray((lastName, userProfileResource) -> setField("lastName", lastName, userProfileResource), lastNames);
    }

    public S withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, userProfileResource) -> setField("phoneNumber", phoneNumber, userProfileResource), phoneNumbers);
    }

    public S withGender(Gender... genders) {
        return withArray((gender, userProfileResource) -> setField("gender", gender, userProfileResource), genders);
    }

    public S withDisability(Disability... disabilities) {
        return withArray((disability, userProfileResource) -> setField("disability", disability, userProfileResource), disabilities);
    }

    public S withEthnicity(EthnicityResource... ethnicities) {
        return withArray((ethnicity, userProfileResource) -> setField("ethnicity", ethnicity, userProfileResource), ethnicities);
    }

    public S withAddress(AddressResource... addresses) {
        return withArray((address, userProfileResource) -> setField("address", address, userProfileResource), addresses);
    }

    public S withEmail(String... emails) {
        return withArray((email, userProfileResource) -> setField("email", email, userProfileResource), emails);
    }
}
