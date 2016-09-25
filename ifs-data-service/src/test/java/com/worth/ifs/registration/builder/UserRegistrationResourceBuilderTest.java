package com.worth.ifs.registration.builder;

import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.EthnicityResource;
import com.worth.ifs.user.resource.Gender;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Disability.YES;
import static com.worth.ifs.user.resource.Gender.*;
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

        UserRegistrationResource userRegistrationResource = newUserRegistrationResource()
                .withTitle(expectedTitle)
                .withFirstName(expectedFirstName)
                .withLastName(expectedLastName)
                .withPhoneNumber(expectedPhoneNumber)
                .withGender(expectedGender)
                .withDisability(expectedDisability)
                .withEthnicity(expectedEthnicity)
                .withPassword(expectedPassword)
                .build();

        assertEquals(expectedTitle, userRegistrationResource.getTitle());
        assertEquals(expectedFirstName, userRegistrationResource.getFirstName());
        assertEquals(expectedLastName, userRegistrationResource.getLastName());
        assertEquals(expectedPhoneNumber, userRegistrationResource.getPhoneNumber());
        assertEquals(expectedGender, userRegistrationResource.getGender());
        assertEquals(expectedDisability, userRegistrationResource.getDisability());
        assertEquals(expectedEthnicity, userRegistrationResource.getEthnicity());
        assertEquals(expectedPassword, userRegistrationResource.getPassword());
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

        List<UserRegistrationResource> userRegistrationResources = newUserRegistrationResource()
                .withTitle(expectedTitles)
                .withFirstName(expectedFirstNames)
                .withLastName(expectedLastNames)
                .withPhoneNumber(expectedPhoneNumbers)
                .withGender(expectedGenders)
                .withDisability(expectedDisabilities)
                .withEthnicity(expectedEthnicities)
                .withPassword(expectedPasswords)
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

        UserRegistrationResource second = userRegistrationResources.get(1);
        assertEquals(expectedTitles[1], second.getTitle());
        assertEquals(expectedFirstNames[1], second.getFirstName());
        assertEquals(expectedLastNames[1], second.getLastName());
        assertEquals(expectedPhoneNumbers[1], second.getPhoneNumber());
        assertEquals(expectedGenders[1], second.getGender());
        assertEquals(expectedDisabilities[1], second.getDisability());
        assertEquals(expectedEthnicities[1], second.getEthnicity());
        assertEquals(expectedPasswords[1], second.getPassword());
    }

}