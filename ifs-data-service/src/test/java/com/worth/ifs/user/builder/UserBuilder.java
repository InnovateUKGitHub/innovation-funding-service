package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserStatus;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.worth.ifs.BuilderAmendFunctions.setField;
import static com.worth.ifs.BuilderAmendFunctions.uniqueIds;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Builder for Organisation entities.
 */
public class UserBuilder extends BaseBuilder<User, UserBuilder> {

    private UserBuilder(List<BiConsumer<Integer, User>> multiActions) {
        super(multiActions);
    }

    public static UserBuilder newUser() {
        return new UserBuilder(emptyList()).
                with(uniqueIds()).
                withFirstName("User").
                with(idBasedLastNames()).
                with(idBasedEmails());
    }

    @Override
    protected UserBuilder createNewBuilderWithActions(List<BiConsumer<Integer, User>> actions) {
        return new UserBuilder(actions);
    }

    public UserBuilder withRolesGlobal(Role... globalRoles) {
        return with(user -> user.setRoles(asList(globalRoles)));
    }

    public UserBuilder withOrganisations(final Organisation... organisations) {
        return with(user -> user.addUserOrganisation(organisations));
    }

    public UserBuilder withEmailAddress(final String... emailAddresses) {
        return withArray((email, user) -> user.setEmail(email), emailAddresses);
    }

    public UserBuilder withFirstName(String... firstNames) {
        return withArray((firstName, user) -> setField("firstName", firstName, user), firstNames);
    }

    public UserBuilder withLastName(String... lastNames) {
        return withArray((lastName, user) -> setField("lastName", lastName, user), lastNames);
    }

    public UserBuilder withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, user) -> setField("phoneNumber", phoneNumber, user), phoneNumbers);
    }

    public UserBuilder withTitle(String... titles) {
        return withArray((title, user) -> setField("title", title, user), titles);
    }

    public UserBuilder withId(Long... ids) {
        return withArray((id, object) -> setField("id", id, object), ids);
    }

    public UserBuilder withinviteName(String... inviteNames) {
        return withArray((inviteName, object) -> setField("inviteName", inviteName, object), inviteNames);
    }

    public UserBuilder withUserStatus(UserStatus... userStatuss) {
        return withArray((userStatus, object) -> setField("status", userStatus, object), userStatuss);
    }

    public UserBuilder withProcessRole(ProcessRole... processRoles) {
        return withArray((processRole, object) -> setField("processRoles", singletonList(processRole), object), processRoles);
    }

    public UserBuilder withUid(String... uids) {
        return withArray((uid, object) -> setField("uid", uid, object), uids);
    }

    @Override
    protected User createInitial() {
        return new User();
    }

    private static Consumer<User> idBasedLastNames() {
        return user -> user.setLastName(user.getId() + "");
    }

    private static Consumer<User> idBasedEmails() {
        return user -> user.setEmail("user" + user.getId() + "@example.com");
    }
}
