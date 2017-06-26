package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class UserProfileBaseResourceBuilder<T extends UserProfileBaseResource, S extends UserProfileBaseResourceBuilder> extends BaseBuilder<T, S> {

    protected UserProfileBaseResourceBuilder(List<BiConsumer<Integer, T>> newActions) {
        super(newActions);
    }

    public S withTitle(Title... titles) {
        return withArray((title, userProfile) -> userProfile.setTitle(title), titles);
    }

    public S withFirstName(String... firstNames) {
        return withArraySetFieldByReflection("firstName", firstNames);
    }

    public S withLastName(String... lastNames) {
        return withArraySetFieldByReflection("lastName", lastNames);
    }

    public S withPhoneNumber(String... phoneNumbers) {
        return withArraySetFieldByReflection("phoneNumber", phoneNumbers);
    }

    public S withGender(Gender... genders) {
        return withArraySetFieldByReflection("gender", genders);
    }

    public S withDisability(Disability... disabilities) {
        return withArraySetFieldByReflection("disability", disabilities);
    }

    public S withEthnicity(EthnicityResource... ethnicities) {
        return withArraySetFieldByReflection("ethnicity", ethnicities);
    }

    public S withAddress(AddressResource... addresses) {
        return withArraySetFieldByReflection("address", addresses);
    }

    public S withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public S withCreatedBy(String... createdBy) {
        return withArraySetFieldByReflection("createdBy", createdBy);
    }

    public S withCreatedOn(ZonedDateTime... createdOn) {
        return withArraySetFieldByReflection("createdOn", createdOn);
    }
}
