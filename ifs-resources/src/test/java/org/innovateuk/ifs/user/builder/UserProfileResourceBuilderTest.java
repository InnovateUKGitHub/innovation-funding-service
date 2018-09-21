package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserProfileResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.user.builder.UserProfileResourceBuilder.newUserProfileResource;
import static org.innovateuk.ifs.user.resource.Title.Miss;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.junit.Assert.assertEquals;

public class UserProfileResourceBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedUser = 1L;
        Title expectedTitle = Mr;
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedPhoneNumber = "01234 567890";
        AddressResource expectedAddress = newAddressResource().build();
        String expectedEmail = "tom@poly.io";

        UserProfileResource userRegistrationResource = newUserProfileResource()
                .withUser(expectedUser)
                .withTitle(expectedTitle)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withPhoneNumber(expectedPhoneNumber)
                .withAddress(expectedAddress)
                .withEmail(expectedEmail)
                .build();

        assertEquals(expectedUser, userRegistrationResource.getUser());
        assertEquals(expectedTitle, userRegistrationResource.getTitle());
        assertEquals(expectedFirstName, userRegistrationResource.getFirstName());
        assertEquals(expectedLastName, userRegistrationResource.getLastName());
        assertEquals(expectedPhoneNumber, userRegistrationResource.getPhoneNumber());
        assertEquals(expectedAddress, userRegistrationResource.getAddress());
        assertEquals(expectedEmail, userRegistrationResource.getEmail());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedUsers = {1L, 2L};
        Title[] expectedTitles = {Mr, Miss};
        String[] expectedFirstNames = {"James", "Sarah"};
        String[] expectedLastNames = {"Smith", "Smythe"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);
        String[] expectedEmails = {"tom@poly.io", "geoff@poly.io"};

        List<UserProfileResource> userProfileResources = newUserProfileResource()
                .withUser(1L, 2L)
                .withTitle(expectedTitles)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withAddress(expectedAddresses)
                .withEmail(expectedEmails)
                .build(2);

        UserProfileResource first = userProfileResources.get(0);
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedTitles[0], first.getTitle());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedAddresses[0], first.getAddress());
        assertEquals(expectedEmails[0], first.getEmail());

        UserProfileResource second = userProfileResources.get(1);
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedTitles[1], second.getTitle());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedAddresses[1], second.getAddress());
        assertEquals(expectedEmails[1], second.getEmail());
    }
}
