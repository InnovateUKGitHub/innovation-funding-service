package org.innovateuk.ifs.profile.builder;

import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.user.domain.Agreement;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.user.builder.AgreementBuilder.newAgreement;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class ProfileBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        Address expectedAddress = newAddress().build();
        String expectedSkillsAreas = "skills areas";
        BusinessType expectedBusinessType = BUSINESS;
        Agreement expectedAgreement = newAgreement().build();
        ZonedDateTime expectedAgreementSignedDate = ZonedDateTime.now();
        
        Profile profile = newProfile()
                .withId(expectedId)
                .withAddress(expectedAddress)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessType)
                .withAgreement(expectedAgreement)
                .withAgreementSignedDate(expectedAgreementSignedDate)
                .build();

        assertEquals(expectedId, profile.getId());
        assertEquals(expectedAddress, profile.getAddress());
        assertEquals(expectedSkillsAreas, profile.getSkillsAreas());
        assertEquals(expectedBusinessType, profile.getBusinessType());
        assertEquals(expectedAgreement, profile.getAgreement());
        assertEquals(expectedAgreementSignedDate, profile.getAgreementSignedDate());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Address[] expectedAddresses = newAddress().buildArray(2, Address.class);
        String[] expectedSkillsAreas = {"skills areas 1", "skills areas 2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};
        Agreement[] expectedContracts = newAgreement().buildArray(2, Agreement.class);
        ZonedDateTime[] expectedAgreementSignedDates = {ZonedDateTime.now(), ZonedDateTime.now().plusDays(1L)};

        List<Profile> profiles = newProfile()
                .withId(expectedIds)
                .withAddress(expectedAddresses)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessTypes)
                .withAgreement(expectedContracts)
                .withAgreementSignedDate(expectedAgreementSignedDates)
                .build(2);

        Profile first = profiles.get(0);

        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedAddresses[0], first.getAddress());
        assertEquals(expectedSkillsAreas[0], first.getSkillsAreas());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());
        assertEquals(expectedContracts[0], first.getAgreement());
        assertEquals(expectedAgreementSignedDates[0], first.getAgreementSignedDate());

        Profile second = profiles.get(1);

        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedAddresses[1], second.getAddress());
        assertEquals(expectedSkillsAreas[1], second.getSkillsAreas());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
        assertEquals(expectedContracts[1], second.getAgreement());
        assertEquals(expectedAgreementSignedDates[1], second.getAgreementSignedDate());
    }
}
