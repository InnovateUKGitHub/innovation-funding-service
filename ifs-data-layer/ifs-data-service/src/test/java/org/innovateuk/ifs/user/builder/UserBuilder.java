package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.resource.Disability;
import org.innovateuk.ifs.user.resource.Gender;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
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

    public UserBuilder withEmailAddress(final String... emailAddresses) {
        return withArray((email, user) -> user.setEmail(email), emailAddresses);
    }

    public UserBuilder withRoles(Set<Role>... rolesList) {
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

    public UserBuilder withTitle(Title... titles) {
        return withArray((title, user) -> user.setTitle(title), titles);
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

    public UserBuilder withUid(String... uids) {
        return withArray((uid, object) -> setField("uid", uid, object), uids);
    }

    public UserBuilder withProfileId(Long... profileIds) {
        return withArraySetFieldByReflection("profileId", profileIds);
    }

    public UserBuilder withAffiliations(List<Affiliation>... affiliationsList) {
        return withArray((affiliations, user) -> setField("affiliations", affiliations, user), affiliationsList);
    }

    public UserBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArray((createdOn, user) -> setField("createdOn", createdOn, user), createdOns);
    }

    public UserBuilder withCreatedBy(User... createdBys) {
        return withArray((createdBy, user) -> setField("createdBy", createdBy, user), createdBys);
    }

    public UserBuilder withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArray((modifiedOn, user) -> setField("modifiedOn", modifiedOn, user), modifiedOns);
    }

    public UserBuilder withModifiedBy(User... modifiedBys) {
        return withArray((modifiedBy, user) -> setField("modifiedBy", modifiedBy, user), modifiedBys);
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
