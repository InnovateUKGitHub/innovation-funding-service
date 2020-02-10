package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.user.resource.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

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

    public UserResourceBuilder withUID(String... uids) {
        return withArray((uid, user) -> setField("uid", uid, user), uids);
    }

    @SafeVarargs
    public final UserResourceBuilder withRolesGlobal(List<Role>... rolesList) {
        return withArray((roles, user) -> user.setRoles(roles), rolesList);
    }

    public final UserResourceBuilder withRoleGlobal(Role role) {
        return withRolesGlobal(singletonList(role));
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

    public UserResourceBuilder withInviteName(String... inviteNames) {
        return withArray((inviteName, user) -> setField("inviteName", inviteName, user), inviteNames);
    }

    public UserResourceBuilder withImageUrl(String... imageUrls) {
        return withArray((imageUrl, user) -> setField("imageUrl", imageUrl, user), imageUrls);
    }

    public UserResourceBuilder withStatus(UserStatus... statuses) {
        return withArray((status, user) -> setField("status", status, user), statuses);
    }

    public UserResourceBuilder withUid(String... uids) {
        return withArray((uid, user) -> setField("uid", uid, user), uids);
    }

    public UserResourceBuilder withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, user) -> setField("phoneNumber", phoneNumber, user), phoneNumbers);
    }

    public UserResourceBuilder withEmail(String... emails) {
        return withArray((email, user) -> setField("email", email, user), emails);
    }

    public UserResourceBuilder withTitle(Title... titles) {
        return withArray((title, user) -> user.setTitle(title), titles);
    }

    public UserResourceBuilder withPassword(String... passwords) {
        return withArray((password, user) -> setField("password", password, user), passwords);
    }

    public UserResourceBuilder withProfile(Long... profiles) {
        return withArray((profile, user) -> setField("profileId", profile, user), profiles);
    }

    public UserResourceBuilder withAllowMarketingEmails(Boolean... allowMarketingEmails) {
        return withArray((allowMarketingEmail, user) ->
                setField("allowMarketingEmails", allowMarketingEmail, user), allowMarketingEmails);
    }

    public UserResourceBuilder withTermsAndConditionsIds(Set<Long>... termsAndConditionsIdList) {
        return withArray((termsAndConditionsIds, user) -> setField("termsAndConditionsIds", termsAndConditionsIds,
                user), termsAndConditionsIdList);
    }

    public UserResourceBuilder withCreatedOn(ZonedDateTime... createdOns) {
        return withArray((createdOn, user) -> setField("createdOn", createdOn, user), createdOns);
    }

    public UserResourceBuilder withCreatedBy(String... createdBys) {
        return withArray((createdBy, user) -> setField("createdBy", createdBy, user), createdBys);
    }

    public UserResourceBuilder withModifiedOn(ZonedDateTime... modifiedOns) {
        return withArray((modifiedOn, user) -> setField("modifiedOn", modifiedOn, user), modifiedOns);
    }

    public UserResourceBuilder withModifiedBy(String... modifiedBys) {
        return withArray((modifiedBy, user) -> setField("modifiedBy", modifiedBy, user), modifiedBys);
    }
}