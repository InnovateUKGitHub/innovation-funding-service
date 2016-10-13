package com.worth.ifs.user.builder;

import com.worth.ifs.BaseBuilder;
import com.worth.ifs.user.resource.*;

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

    public UserResourceBuilder withUID(String... uids) {
        return withArray((uid, user) -> setField("uid", uid, user), uids);
    }

    @SafeVarargs
    public final UserResourceBuilder withRolesGlobal(List<RoleResource>... rolesList) {
        return withArray((roles, user) -> user.setRoles(roles), rolesList);
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

    public UserResourceBuilder withDisability(Disability... disabilities) {
        return withArray((disability, user) -> setField("disability", disability, user), disabilities);
    }

    public UserResourceBuilder withEthnicity(Long... ethnicities) {
        return withArray((ethnicity, user) -> setField("ethnicity", ethnicity, user), ethnicities);
    }

    public UserResourceBuilder withGender(Gender... genders) {
        return withArray((gender, user) -> setField("gender", gender, user), genders);
    }

    public UserResourceBuilder withPhoneNumber(String... phoneNumbers) {
        return withArray((phoneNumber, user) -> setField("phoneNumber", phoneNumber, user), phoneNumbers);
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

    public UserResourceBuilder withProfile(Long... profiles) {
        return withArray((profile, user) -> setField("profile", profile, user), profiles);
    }

    @SafeVarargs
    public final UserResourceBuilder withProcessRoles(List<Long>... processRoles) {
        return withArray((processRoleList, user) -> user.setProcessRoles(processRoleList), processRoles);
    }

    @SafeVarargs
    public final UserResourceBuilder withOrganisations(List<Long>... organisationIds) {
        return withArray((organisationIdList, user) -> user.setOrganisations(organisationIdList), organisationIds);
    }
}
