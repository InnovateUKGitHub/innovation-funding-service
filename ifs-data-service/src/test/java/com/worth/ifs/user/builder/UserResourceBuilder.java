package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.function.BiConsumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

public class UserResourceBuilder extends BaseBuilder<UserResource, UserResourceBuilder> {

    private UserResourceBuilder(List<BiConsumer<Integer, UserResource>> multiActions) {
        super(multiActions);
    }

    public static UserResourceBuilder newUserResource() {
        return new UserResourceBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected UserResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, UserResource>> actions) {
        return new UserResourceBuilder(actions);
    }

    @Override
    protected UserResource createInitial() {
        return new UserResource();
    }

    public UserResourceBuilder withId(Long... ids) {
        return withArray((id, user) -> setField("id", id, user), ids);
    }

    public UserResourceBuilder withFirstName(String... firstNames) {
        return withArray((firstName, user) -> setField("firstName", firstName, user), firstNames);
    }

    public UserResourceBuilder withLastName(String... lastNames) {
        return withArray((lastName, user) -> setField("lastName", lastName, user), lastNames);
    }

    public UserResourceBuilder withName(String... names) {
        return withArray((name, user) -> setField("name", name, user), names);
    }

    public UserResourceBuilder withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, user) -> setField("phoneNumber", phoneNumber, user), phoneNumbers);
    }

    public UserResourceBuilder withToken(String... tokens) {
        return withArray((token, user) -> setField("token", token, user), tokens);
    }

    public UserResourceBuilder withEmail(String... emails) {
        return withArray((email, user) -> setField("email", email, user), emails);
    }

    public UserResourceBuilder withTitle(String... titles) {
        return withArray((title, user) -> setField("title", title, user), titles);
    }

    public UserResourceBuilder withPassword(String... passwords) {
        return withArray((password, user) -> setField("password", password, user), passwords);
    }


}
