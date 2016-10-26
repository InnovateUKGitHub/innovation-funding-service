package com.worth.ifs.user.builder;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.*;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Disability.YES;
import static com.worth.ifs.user.resource.Gender.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class UserProfileResourceBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedUser = 1L;
        String expectedTitle = "Title";
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedPhoneNumber = "01234 567890";
        Gender expectedGender = NOT_STATED;
        Disability expectedDisability = NO;
        EthnicityResource expectedEthnicity = newEthnicityResource().build();
        AddressResource expectedAddress = newAddressResource().build();
        String expectedEmail = "tom@poly.io";

        UserProfileResource userRegistrationResource = newUserProfileResource()
                .withUser(1L)
                .withTitle(expectedTitle)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withPhoneNumber(expectedPhoneNumber)
                .withGender(expectedGender)
                .withDisability(expectedDisability)
                .withEthnicity(expectedEthnicity)
                .withAddress(expectedAddress)
                .withEmail(expectedEmail)
                .build();

        assertEquals(expectedUser, userRegistrationResource.getUser());
        assertEquals(expectedTitle, userRegistrationResource.getTitle());
        assertEquals(expectedFirstName, userRegistrationResource.getFirstName());
        assertEquals(expectedLastName, userRegistrationResource.getLastName());
        assertEquals(expectedPhoneNumber, userRegistrationResource.getPhoneNumber());
        assertEquals(expectedGender, userRegistrationResource.getGender());
        assertEquals(expectedDisability, userRegistrationResource.getDisability());
        assertEquals(expectedEthnicity, userRegistrationResource.getEthnicity());
        assertEquals(expectedAddress, userRegistrationResource.getAddress());
        assertEquals(expectedEmail, userRegistrationResource.getEmail());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedUsers = {1L, 2L};
        String[] expectedTitles = {"Mr", "Miss"};
        String[] expectedFirstNames = {"James", "Sarah"};
        String[] expectedLastNames = {"Smith", "Smythe"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        Gender[] expectedGenders = {MALE, FEMALE};
        Disability[] expectedDisabilities = {YES, NO};
        EthnicityResource[] expectedEthnicities = newEthnicityResource().buildArray(2, EthnicityResource.class);
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);
        String[] expectedEmails = {"tom@poly.io", "geoff@poly.io"};

        List<UserProfileResource> userRegistrationResources = newUserProfileResource()
                .withUser(1L, 2L)
                .withTitle(expectedTitles)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withGender(expectedGenders)
                .withDisability(expectedDisabilities)
                .withEthnicity(expectedEthnicities)
                .withAddress(expectedAddresses)
                .withEmail(expectedEmails)
                .build(2);

        UserProfileResource first = userRegistrationResources.get(0);
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedTitles[0], first.getTitle());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedGenders[0], first.getGender());
        assertEquals(expectedDisabilities[0], first.getDisability());
        assertEquals(expectedEthnicities[0], first.getEthnicity());
        assertEquals(expectedAddresses[0], first.getAddress());
        assertEquals(expectedEmails[0], first.getEmail());

        UserProfileResource second = userRegistrationResources.get(1);
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedTitles[1], second.getTitle());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedGenders[1], second.getGender());
        assertEquals(expectedDisabilities[1], second.getDisability());
        assertEquals(expectedEthnicities[1], second.getEthnicity());
        assertEquals(expectedAddresses[1], second.getAddress());
        assertEquals(expectedEmails[1], second.getEmail());
    }
}