package com.worth.ifs.registration.builder;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import com.worth.ifs.user.resource.RoleResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Disability.YES;
import static com.worth.ifs.user.resource.Gender.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class UserRegistrationResourceBuilderTest {

    @Test
    public void testBuildOne() {
        String expectedTitle = "Title";
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedPhoneNumber = "01234 567890";
        Gender expectedGender = NOT_STATED;
        Disability expectedDisability = NO;
        EthnicityResource expectedEthnicity = newEthnicityResource().build();
        String expectedPassword = "Passw0rd123";
        AddressResource expectedAddress = newAddressResource().build();
        String expectedEmail = "tom@poly.io";
        List<RoleResource> expectedRoles = asList(newRoleResource().buildArray(2, RoleResource.class));

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(expectedTitle)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withPhoneNumber(expectedPhoneNumber)
                .withGender(expectedGender)
                .withDisability(expectedDisability)
                .withEthnicity(expectedEthnicity)
                .withPassword(expectedPassword)
                .withAddress(expectedAddress)
                .withEmail(expectedEmail)
                .withRoles(expectedRoles)
                .build();

        assertEquals(expectedTitle, userRegistrationResource.getTitle());
        assertEquals(expectedFirstName, userRegistrationResource.getFirstName());
        assertEquals(expectedLastName, userRegistrationResource.getLastName());
        assertEquals(expectedPhoneNumber, userRegistrationResource.getPhoneNumber());
        assertEquals(expectedGender, userRegistrationResource.getGender());
        assertEquals(expectedDisability, userRegistrationResource.getDisability());
        assertEquals(expectedEthnicity, userRegistrationResource.getEthnicity());
        assertEquals(expectedPassword, userRegistrationResource.getPassword());
        assertEquals(expectedAddress, userRegistrationResource.getAddress());
        assertEquals(expectedEmail, userRegistrationResource.getEmail());
        assertEquals(expectedRoles, userRegistrationResource.getRoles());
    }

    @Test
    public void testBuildMany() {
        String[] expectedTitles = {"Mr", "Miss"};
        String[] expectedFirstNames = {"James", "Sarah"};
        String[] expectedLastNames = {"Smith", "Smythe"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        Gender[] expectedGenders = {MALE, FEMALE};
        Disability[] expectedDisabilities = {YES, NO};
        EthnicityResource[] expectedEthnicities = newEthnicityResource().buildArray(2, EthnicityResource.class);
        String[] expectedPasswords = {"Passw0rd123", "Passw0rd456"};
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);
        String[] expectedEmails = {"tom@poly.io", "geoff@poly.io"};
        List<RoleResource>[] expectedRoles = new List[]{
                asList(newRoleResource().buildArray(2, RoleResource.class)),
                asList(newRoleResource().buildArray(2, RoleResource.class))
        };

        List<UserRegistrationResource> userRegistrationResources = newUserRegistrationResource()
                .withTitle(expectedTitles)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withGender(expectedGenders)
                .withDisability(expectedDisabilities)
                .withEthnicity(expectedEthnicities)
                .withPassword(expectedPasswords)
                .withAddress(expectedAddresses)
                .withEmail(expectedEmails)
                .withRoles(expectedRoles)
                .build(2);

        UserRegistrationResource first = userRegistrationResources.get(0);
        assertEquals(expectedTitles[0], first.getTitle());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedGenders[0], first.getGender());
        assertEquals(expectedDisabilities[0], first.getDisability());
        assertEquals(expectedEthnicities[0], first.getEthnicity());
        assertEquals(expectedPasswords[0], first.getPassword());
        assertEquals(expectedAddresses[0], first.getAddress());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedRoles[0], first.getRoles());

        UserRegistrationResource second = userRegistrationResources.get(1);
        assertEquals(expectedTitles[1], second.getTitle());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedGenders[1], second.getGender());
        assertEquals(expectedDisabilities[1], second.getDisability());
        assertEquals(expectedEthnicities[1], second.getEthnicity());
        assertEquals(expectedPasswords[1], second.getPassword());
        assertEquals(expectedAddresses[1], second.getAddress());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedRoles[1], second.getRoles());
    }
}