package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.UserProfileBaseResource;

import java.util.List;
import java.util.function.BiConsumer;

public abstract class UserProfileBaseResourceBuilder<T extends UserProfileBaseResource, S extends UserProfileBaseResourceBuilder> extends BaseBuilder<T, S> {

    protected UserProfileBaseResourceBuilder(List<BiConsumer<Integer, T>> newActions) {
        super(newActions);
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

    public S withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }
}
