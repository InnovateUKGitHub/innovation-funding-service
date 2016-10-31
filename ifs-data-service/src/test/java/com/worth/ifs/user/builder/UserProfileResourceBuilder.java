package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.user.resource.*;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BaseBuilderAmendFunctions.createDefault;
import static com.worth.ifs.BaseBuilderAmendFunctions.setField;
import static java.util.Collections.emptyList;

public class UserProfileResourceBuilder extends BaseBuilder<UserProfileResource, UserProfileResourceBuilder> {

    private UserProfileResourceBuilder(List<BiConsumer<Integer, UserProfileResource>> multiActions) {
        super(multiActions);
    }

    public static UserProfileResourceBuilder newUserProfileResource() {
        return new UserProfileResourceBuilder(emptyList());
    }

    @Override
    protected UserProfileResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UserProfileResource>> actions) {
        return new UserProfileResourceBuilder(actions);
    }

    @Override
    protected UserProfileResource createInitial() {
        return createDefault(UserProfileResource.class);
    }

    public UserProfileResourceBuilder withUser(Long... users) {
        return withArray((user, userProfileResource) -> setField("user", user, userProfileResource), users);
    }

    public UserProfileResourceBuilder withTitle(String... titles) {
        return withArray((title, userProfileResource) -> setField("title", title, userProfileResource), titles);
    }

    public UserProfileResourceBuilder withFirstName(String... firstNames) {
        return withArray((firstName, userProfileResource) -> setField("firstName", firstName, userProfileResource), firstNames);
    }

    public UserProfileResourceBuilder withLastName(String... lastNames) {
        return withArray((lastName, userProfileResource) -> setField("lastName", lastName, userProfileResource), lastNames);
    }

    public UserProfileResourceBuilder withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, userProfileResource) -> setField("phoneNumber", phoneNumber, userProfileResource), phoneNumbers);
    }

    public UserProfileResourceBuilder withGender(Gender... genders) {
        return withArray((gender, userProfileResource) -> setField("gender", gender, userProfileResource), genders);
    }

    public UserProfileResourceBuilder withDisability(Disability... disabilities) {
        return withArray((disability, userProfileResource) -> setField("disability", disability, userProfileResource), disabilities);
    }

    public UserProfileResourceBuilder withEthnicity(EthnicityResource... ethnicities) {
        return withArray((ethnicity, userProfileResource) -> setField("ethnicity", ethnicity, userProfileResource), ethnicities);
    }

    public UserProfileResourceBuilder withAddress(AddressResource... addresses) {
        return withArray((address, userProfileResource) -> setField("address", address, userProfileResource), addresses);
    }

    public UserProfileResourceBuilder withEmail(String... emails) {
        return withArray((email, userProfileResource) -> setField("email", email, userProfileResource), emails);
    }
}
