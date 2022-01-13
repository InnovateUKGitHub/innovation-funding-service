package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.user.resource.AgreementResource;
import org.innovateuk.ifs.user.resource.ProfileAgreementResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.innovateuk.ifs.user.builder.ProfileAgreementResourceBuilder.newProfileAgreementResource;
import static org.junit.Assert.assertEquals;

public class ProfileAgreementResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUser = 1L;
        AgreementResource agreementResource = newAgreementResource().build();
        boolean expectedCurrentAgreement = true;
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.now();

        ProfileAgreementResource profileAgreementResource = newProfileAgreementResource()
                .withUser(expectedUser)
                .withAgreement(agreementResource)
                .withCurrentAgreement(expectedCurrentAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build();

        assertEquals(expectedUser, profileAgreementResource.getUser());
        assertEquals(agreementResource, profileAgreementResource.getAgreement());
        assertEquals(expectedCurrentAgreement, profileAgreementResource.isCurrentAgreement());
        assertEquals(expectedAgreementSignedDate, profileAgreementResource.getAgreementSignedDate());
    }

    @Test
    public void buildMany() {
        Long[] expectedUsers = {1L, 2L};
        AgreementResource[] expectedContracts = newAgreementResource().buildArray(2, AgreementResource.class);
        Boolean[] expectedCurrentAgreements = {true, false};
        ZonedDateTime[] expectedAgreementSignedDates = {ZonedDateTime.now(), ZonedDateTime.now()};

        List<ProfileAgreementResource> profileAgreementResources = newProfileAgreementResource()
                .withUser(expectedUsers)
                .withAgreement(expectedContracts)
                .withCurrentAgreement(expectedCurrentAgreements)
                .withAgreementSignedDate(expectedAgreementSignedDates)
                .build(2);

        ProfileAgreementResource first = profileAgreementResources.get(0);

        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedContracts[0], first.getAgreement());
        assertEquals(expectedCurrentAgreements[0], first.isCurrentAgreement());
        assertEquals(expectedAgreementSignedDates[0], first.getAgreementSignedDate());

        ProfileAgreementResource second = profileAgreementResources.get(1);

        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedContracts[1], second.getAgreement());
        assertEquals(expectedCurrentAgreements[1], second.isCurrentAgreement());
        assertEquals(expectedAgreementSignedDates[1], second.getAgreementSignedDate());
    }

}
