package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.user.domain.Affiliation;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.user.builder.AffiliationBuilder.newAffiliation;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.junit.Assert.*;

/**
 * Test the initial setup of the builder
 */
public class UserBuilderTest {

    @Test
    public void testUsersBuiltWithInitialSetOfValuesDefined() {

        List<User> users = newUser().build(2);
        Long initialId = users.get(0).getId();

        assertEquals(Long.valueOf(initialId), users.get(0).getId());
        assertEquals(Long.valueOf(initialId + 1), users.get(1).getId());

        assertEquals("User " + initialId, users.get(0).getName());
        assertEquals("User " + (initialId + 1), users.get(1).getName());

        assertEquals("user" + initialId + "@example.com", users.get(0).getEmail());
        assertEquals("user" + (initialId + 1) + "@example.com", users.get(1).getEmail());
    }

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedInviteName = "Invite Name";
        String expectedPhoneNumber = "01234 567890";
        String expectedImageUrl = "http://url";
        UserStatus expectedStatus = ACTIVE;
        String expectedUid = "Uid";
        String expectedEmail = "test@test.com";
        Set<Role> expectedRoles = newRole().buildSet(2);
        Long expectedProfile = 1L;
        List<Affiliation> expectedAffiliations = newAffiliation().withAffiliationType(EMPLOYER, FAMILY_FINANCIAL).build(2);

        User user = newUser()
                .withId(expectedId)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withInviteName(expectedInviteName)
                .withPhoneNumber(expectedPhoneNumber)
                .withImageUrl(expectedImageUrl)
                .withStatus(expectedStatus)
                .withUid(expectedUid)
                .withEmailAddress(expectedEmail)
                .withRoles(expectedRoles)
                .withProfileId(expectedProfile)
                .withAffiliations(expectedAffiliations)
                .build();

        assertEquals(expectedId, user.getId());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedInviteName, user.getInviteName());
        assertEquals(expectedPhoneNumber, user.getPhoneNumber());
        assertEquals(expectedImageUrl, user.getImageUrl());
        assertEquals(expectedStatus, user.getStatus());
        assertEquals(expectedUid, user.getUid());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedRoles, user.getRoles());
        assertEquals(expectedProfile, user.getProfileId());
        assertEquals(expectedAffiliations, user.getAffiliations());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        String[] expectedFirstNames = {"First 1", "First 2"};
        String[] expectedLastNames = {"Last 1", "Last 2"};
        String[] expectedInviteNames = {"Invite Name 1", "Invite Name 2"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        String[] expectedImageUrls = {"http://url1", "http://url2"};
        UserStatus[] expectedStatuss = {ACTIVE, INACTIVE};
        String[] expectedUids = {"Uid1", "Uid2"};
        String[] expectedEmails = {"email1@test.com", "email2@test.com"};
        List<Set<Role>> expectedRoles = asList(newRole().buildSet(2), newRole().buildSet(2));
        Long[] expectedProfiles = {1L, 2L};
        List<Affiliation>[] expectedAffiliations = new List[]{
                newAffiliation().withAffiliationType(EMPLOYER, FAMILY_FINANCIAL).build(2),
                newAffiliation().withAffiliationType(PERSONAL_FINANCIAL, FAMILY_FINANCIAL).build(2)
        };

        List<User> users = newUser()
                .withId(expectedIds)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withInviteName(expectedInviteNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withImageUrl(expectedImageUrls)
                .withStatus(expectedStatuss)
                .withUid(expectedUids)
                .withEmailAddress(expectedEmails)
                .withRoles(expectedRoles.get(0), expectedRoles.get(1))
                .withProfileId(expectedProfiles)
                .withAffiliations(expectedAffiliations)
                .build(2);


        User first = users.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedInviteNames[0], first.getInviteName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedImageUrls[0], first.getImageUrl());
        assertEquals(expectedStatuss[0], first.getStatus());
        assertEquals(expectedUids[0], first.getUid());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedRoles.get(0), first.getRoles());
        assertEquals(expectedProfiles[0], first.getProfileId());
        assertEquals(expectedAffiliations[0], first.getAffiliations());

        User second = users.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedInviteNames[1], second.getInviteName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedImageUrls[1], second.getImageUrl());
        assertEquals(expectedStatuss[1], second.getStatus());
        assertEquals(expectedUids[1], second.getUid());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedRoles.get(1), second.getRoles());
        assertEquals(expectedProfiles[1], second.getProfileId());
        assertEquals(expectedAffiliations[1], second.getAffiliations());
    }
}
