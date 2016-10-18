package com.worth.ifs.registration.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.*;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class UserRegistrationResourceBuilder extends BaseBuilder<UserRegistrationResource, UserRegistrationResourceBuilder> {

    private UserRegistrationResourceBuilder(final List<BiConsumer<Integer, UserRegistrationResource>> newActions) {
        super(newActions);
    }

    @Override
    protected UserRegistrationResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UserRegistrationResource>> actions) {
        return new UserRegistrationResourceBuilder(actions);
    }

    public static UserRegistrationResourceBuilder newUserRegistrationResource() {
        return new UserRegistrationResourceBuilder(emptyList());
    }

    @Override
    protected UserRegistrationResource createInitial() {
        return new UserRegistrationResource();
    }

    public UserRegistrationResourceBuilder withTitle(String... titles) {
        return withArray((title, userRegistrationResource) -> setField("title", title, userRegistrationResource), titles);
    }

    public UserRegistrationResourceBuilder withFirstName(String... firstNames) {
        return withArray((firstName, userRegistrationResource) -> setField("firstName", firstName, userRegistrationResource), firstNames);
    }

    public UserRegistrationResourceBuilder withLastName(String... lastNames) {
        return withArray((lastName, userRegistrationResource) -> setField("lastName", lastName, userRegistrationResource), lastNames);
    }

    public UserRegistrationResourceBuilder withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, userRegistrationResource) -> setField("phoneNumber", phoneNumber, userRegistrationResource), phoneNumbers);
    }

    public UserRegistrationResourceBuilder withGender(Gender... genders) {
        return withArray((gender, userRegistrationResource) -> setField("gender", gender, userRegistrationResource), genders);
    }

    public UserRegistrationResourceBuilder withDisability(Disability... disabilities) {
        return withArray((disability, userRegistrationResource) -> setField("disability", disability, userRegistrationResource), disabilities);
    }

    public UserRegistrationResourceBuilder withEthnicity(EthnicityResource... ethnicities) {
        return withArray((ethnicity, userRegistrationResource) -> setField("ethnicity", ethnicity, userRegistrationResource), ethnicities);
    }

    public UserRegistrationResourceBuilder withPassword(String... passwords) {
        return withArray((password, userRegistrationResource) -> setField("password", password, userRegistrationResource), passwords);
    }

    public UserRegistrationResourceBuilder withAddress(AddressResource... addresses) {
        return withArray((address, userRegistrationResource) -> setField("address", address, userRegistrationResource), addresses);
    }

    public UserRegistrationResourceBuilder withEmail(String... emails) {
        return withArray((email, userRegistrationResource) -> setField("email", email, userRegistrationResource), emails);
    }

    public UserRegistrationResourceBuilder withRoles(List<RoleResource>... roles) {
        return withArray((roleList, userRegistrationResource) -> setField("roles", roleList, userRegistrationResource), roles);
    }
}