package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.Gender;
import com.worth.ifs.user.resource.UserStatus;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static java.util.Collections.emptyList;

/**
 * Builder for User entities.
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

    public UserBuilder withOrganisations(List<Organisation>... organisationsList) {
        return withArray((organisations, user) -> user.addUserOrganisation(organisations.toArray(new Organisation[organisations.size()])), organisationsList);
    }

    public UserBuilder withEmailAddress(final String... emailAddresses) {
        return withArray((email, user) -> user.setEmail(email), emailAddresses);
    }

    public UserBuilder withRoles(List<Role>... rolesList) {
        return withArray((roles, user) -> setField("roles", roles, user), rolesList);
    }

    public UserBuilder withFirstName(String... firstNames) {
        return withArray((firstName, user) -> setField("firstName", firstName, user), firstNames);
    }

    public UserBuilder withLastName(String... lastNames) {
        return withArray((lastName, user) -> setField("lastName", lastName, user), lastNames);
    }

    public UserBuilder withDisability(Disability... disabilities) {
        return withArray((disability, user) -> setField("disability", disability, user), disabilities);
    }

    public UserBuilder withEthnicity(Ethnicity... ethnicities) {
        return withArray((ethnicity, user) -> setField("ethnicity", ethnicity, user), ethnicities);
    }

    public UserBuilder withGender(Gender... genders) {
        return withArray((gender, user) -> setField("gender", gender, user), genders);
    }

    public UserBuilder withImageUrl(String... imageUrls) {
        return withArray((imageUrl, user) -> setField("imageUrl", imageUrl, user), imageUrls);
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

    public UserBuilder withInviteName(String... inviteNames) {
        return withArray((inviteName, object) -> setField("inviteName", inviteName, object), inviteNames);
    }

    public UserBuilder withStatus(UserStatus... statuses) {
        return withArray((status, user) -> setField("status", status, user), statuses);
    }

    public UserBuilder withProcessRoles(List<ProcessRole>... processRolesList) {
        return withArray((processRoles, object) -> setField("processRoles", processRoles, object), processRolesList);
    }

    public UserBuilder withUid(String... uids) {
        return withArray((uid, object) -> setField("uid", uid, object), uids);
    }

    public UserBuilder withProfile(Profile... profiles) {
        return withArray((profile, user) -> setField("profile", profile, user), profiles);
    }

    public UserBuilder withAffiliations(List<Affiliation>... affiliationsList) {
        return withArray((affiliations, user) -> setField("affiliations", affiliations, user), affiliationsList);
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
