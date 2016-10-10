package com.worth.ifs.user.builder;

import com.worth.ifs.user.resource.*;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.ProfileResourceBuilder.newProfileResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.Disability.NOT_STATED;
import static com.worth.ifs.user.resource.Disability.YES;
import static com.worth.ifs.user.resource.Gender.FEMALE;
import static com.worth.ifs.user.resource.Gender.MALE;
import static com.worth.ifs.user.resource.UserStatus.ACTIVE;
import static com.worth.ifs.user.resource.UserStatus.INACTIVE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class UserResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedTitle = "Title";
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedInviteName = "Invite Name";
        String expectedPhoneNumber = "01234 567890";
        String expectedImageUrl = "http://url";
        UserStatus expectedStatus = ACTIVE;
        String expectedUid = "Uid";
        String expectedEmail = "test@test.com";
        List<Long> expectedProcessRoles = asList(100L, 101L);
        List<Long> expectedOrganisations = asList(200L, 201L);
        List<RoleResource> expectedRoles = newRoleResource().build(2);
        Gender expectedGender = FEMALE;
        Disability expectedDisability = NOT_STATED;
        Long expectedEthnicity = 1L;
        ProfileResource expectedProfile = newProfileResource().build();

        UserResource user = newUserResource()
                .withId(expectedId)
                .withTitle(expectedTitle)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withInviteName(expectedInviteName)
                .withPhoneNumber(expectedPhoneNumber)
                .withImageUrl(expectedImageUrl)
                .withStatus(expectedStatus)
                .withUid(expectedUid)
                .withEmail(expectedEmail)
                .withProcessRoles(expectedProcessRoles)
                .withOrganisations(expectedOrganisations)
                .withRolesGlobal(expectedRoles)
                .withGender(expectedGender)
                .withDisability(expectedDisability)
                .withEthnicity(expectedEthnicity)
                .withProfile(expectedProfile)
                .build();

        assertEquals(expectedId, user.getId());
        assertEquals(expectedTitle, user.getTitle());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedInviteName, user.getInviteName());
        assertEquals(expectedPhoneNumber, user.getPhoneNumber());
        assertEquals(expectedImageUrl, user.getImageUrl());
        assertEquals(expectedStatus, user.getStatus());
        assertEquals(expectedUid, user.getUid());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedProcessRoles, user.getProcessRoles());
        assertEquals(expectedOrganisations, user.getOrganisations());
        assertEquals(expectedRoles, user.getRoles());
        assertEquals(expectedGender, user.getGender());
        assertEquals(expectedDisability, user.getDisability());
        assertEquals(expectedEthnicity, user.getEthnicity());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedTitles = {"Miss", "Mr"};
        String[] expectedFirstNames = {"First 1", "First 2"};
        String[] expectedLastNames = {"Last 1", "Last 2"};
        String[] expectedInviteNames = {"Invite Name 1", "Invite Name 2"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        String[] expectedImageUrls = {"http://url1", "http://url2"};
        UserStatus[] expectedStatuss = {ACTIVE, INACTIVE};
        String[] expectedUids = {"Uid1", "Uid2"};
        String[] expectedEmails = {"email1@test.com", "email2@test.com"};
        List<List<Long>> expectedProcessRoles = asList(asList(100L, 101L), asList(102L, 103L));
        List<List<Long>> expectedOrganisations = asList(asList(200L, 201L), asList(202L, 203L));
        List<List<RoleResource>> expectedRoles = asList(newRoleResource().build(2), newRoleResource().build(2));
        Gender[] expectedGenders = {FEMALE, MALE};
        Disability[] expectedDisabilities = {NOT_STATED, YES};
        Long[] expectedEthnicities = {1L, 2L};
        ProfileResource[] expectedProfiles = newProfileResource().buildArray(2, ProfileResource.class);

        List<UserResource> users = newUserResource()
                .withId(expectedIds)
                .withTitle(expectedTitles)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withInviteName(expectedInviteNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withImageUrl(expectedImageUrls)
                .withStatus(expectedStatuss)
                .withUid(expectedUids)
                .withEmail(expectedEmails)
                .withProcessRoles(expectedProcessRoles.get(0), expectedProcessRoles.get(1))
                .withOrganisations(expectedOrganisations.get(0), expectedOrganisations.get(1))
                .withRolesGlobal(expectedRoles.get(0), expectedRoles.get(1))
                .withGender(expectedGenders)
                .withDisability(expectedDisabilities)
                .withEthnicity(expectedEthnicities)
                .withProfile(expectedProfiles)
                .build(2);


        UserResource first = users.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedTitles[0], first.getTitle());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedInviteNames[0], first.getInviteName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedImageUrls[0], first.getImageUrl());
        assertEquals(expectedStatuss[0], first.getStatus());
        assertEquals(expectedUids[0], first.getUid());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedProcessRoles.get(0), first.getProcessRoles());
        assertEquals(expectedOrganisations.get(0), first.getOrganisations());
        assertEquals(expectedRoles.get(0), first.getRoles());
        assertEquals(expectedGenders[0], first.getGender());
        assertEquals(expectedDisabilities[0], first.getDisability());
        assertEquals(expectedEthnicities[0], first.getEthnicity());
        assertEquals(expectedProfiles[0], first.getProfile());

        UserResource second = users.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedTitles[1], second.getTitle());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedInviteNames[1], second.getInviteName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedImageUrls[1], second.getImageUrl());
        assertEquals(expectedStatuss[1], second.getStatus());
        assertEquals(expectedUids[1], second.getUid());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedProcessRoles.get(1), second.getProcessRoles());
        assertEquals(expectedOrganisations.get(1), second.getOrganisations());
        assertEquals(expectedRoles.get(1), second.getRoles());
        assertEquals(expectedGenders[1], second.getGender());
        assertEquals(expectedDisabilities[1], second.getDisability());
        assertEquals(expectedEthnicities[1], second.getEthnicity());
        assertEquals(expectedProfiles[1], second.getProfile());
    }

}