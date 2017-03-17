package org.innovateuk.ifs.registration.builder;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.junit.Assert.*;

public class UserRegistrationResourceBuilderTest {

    @Test
    public void testBuildOne() {
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedPhoneNumber = "01234 567890";
        String expectedPassword = "Passw0rd123";
        AddressResource expectedAddress = newAddressResource().build();
        String expectedEmail = "tom@poly.io";
        List<RoleResource> expectedRoles = asList(newRoleResource().buildArray(2, RoleResource.class));

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withPhoneNumber(expectedPhoneNumber)
                .withPassword(expectedPassword)
                .withAddress(expectedAddress)
                .withEmail(expectedEmail)
                .withRoles(expectedRoles)
                .build();

        assertEquals(expectedFirstName, userRegistrationResource.getFirstName());
        assertEquals(expectedLastName, userRegistrationResource.getLastName());
        assertEquals(expectedPhoneNumber, userRegistrationResource.getPhoneNumber());
        assertEquals(expectedPassword, userRegistrationResource.getPassword());
        assertEquals(expectedAddress, userRegistrationResource.getAddress());
        assertEquals(expectedEmail, userRegistrationResource.getEmail());
        assertEquals(expectedRoles, userRegistrationResource.getRoles());
    }

    @Test
    public void testBuildMany() {
        String[] expectedFirstNames = {"James", "Sarah"};
        String[] expectedLastNames = {"Smith", "Smythe"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        String[] expectedPasswords = {"Passw0rd123", "Passw0rd456"};
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);
        String[] expectedEmails = {"tom@poly.io", "geoff@poly.io"};
        List<RoleResource>[] expectedRoles = new List[]{
                asList(newRoleResource().buildArray(2, RoleResource.class)),
                asList(newRoleResource().buildArray(2, RoleResource.class))
        };

        List<UserRegistrationResource> userRegistrationResources = newUserRegistrationResource()
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withPassword(expectedPasswords)
                .withAddress(expectedAddresses)
                .withEmail(expectedEmails)
                .withRoles(expectedRoles)
                .build(2);

        UserRegistrationResource first = userRegistrationResources.get(0);
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedPasswords[0], first.getPassword());
        assertEquals(expectedAddresses[0], first.getAddress());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedRoles[0], first.getRoles());

        UserRegistrationResource second = userRegistrationResources.get(1);
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedPasswords[1], second.getPassword());
        assertEquals(expectedAddresses[1], second.getAddress());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedRoles[1], second.getRoles());
    }
}
