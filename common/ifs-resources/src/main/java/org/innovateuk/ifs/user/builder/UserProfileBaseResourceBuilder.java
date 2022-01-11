package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

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

    public S withAddress(AddressResource... addresses) {
        return withArraySetFieldByReflection("address", addresses);
    }

    public S withSimpleOrganisation(String... simpleOrganisations) {
        return withArraySetFieldByReflection("simpleOrganisation", simpleOrganisations);
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

    public S withModifiedBy(String... users) {
        return withArray((user, profile) -> setField("modifiedBy", user, profile), users);
    }

    public S withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArray((modifiedOn, profile) -> setField("modifiedOn", modifiedOn, profile), modifiedOns);
    }
}
