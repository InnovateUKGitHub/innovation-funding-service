package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.category.resource.CategoryResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.resource.Gender;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.user.resource.Disability.NO;
import static org.innovateuk.ifs.user.resource.Disability.YES;
import static org.innovateuk.ifs.user.resource.Gender.*;
import static org.junit.Assert.assertEquals;

public class AssessorProfileResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        String expectedTitle = "Title";
        String expectedFirstName = "First";
        String expectedLastName = "Last";
        String expectedPhoneNumber = "01234 567890";
        Gender expectedGender = NOT_STATED;
        Disability expectedDisability = NO;
        EthnicityResource expectedEthnicity = newEthnicityResource().build();
        AddressResource expectedAddress = newAddressResource().build();
        String expectedEmail = "test@test.com";

        String expectedSkillsAreas = "Skills Area";
        BusinessType expectedBusinessType = BUSINESS;
        List<AffiliationResource> expectedAffiliations = newAffiliationResource().build(2);
        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource().build(2);

        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withAffiliations(expectedAffiliations)
                .withInnovationAreas(expectedInnovationAreas)
                .withBusinessType(expectedBusinessType)
                .withSkillsAreas(expectedSkillsAreas)
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

        assertEquals(expectedBusinessType, assessorProfileResource.getBusinessType());
        assertEquals(expectedSkillsAreas, assessorProfileResource.getSkillsAreas());
        assertEquals(expectedInnovationAreas, assessorProfileResource.getInnovationAreas());
        assertEquals(expectedAffiliations, assessorProfileResource.getAffiliations());
        assertEquals(expectedTitle, assessorProfileResource.getTitle());
        assertEquals(expectedFirstName, assessorProfileResource.getFirstName());
        assertEquals(expectedLastName, assessorProfileResource.getLastName());
        assertEquals(expectedPhoneNumber, assessorProfileResource.getPhoneNumber());
        assertEquals(expectedGender, assessorProfileResource.getGender());
        assertEquals(expectedDisability, assessorProfileResource.getDisability());
        assertEquals(expectedEthnicity, assessorProfileResource.getEthnicity());
        assertEquals(expectedAddress, assessorProfileResource.getAddress());
        assertEquals(expectedEmail, assessorProfileResource.getEmail());
    }

    @Test
    public void buildMany() throws Exception {
        Long[] expectedUsers = {1L, 2L};
        String[] expectedTitles = {"Mr", "Miss"};
        String[] expectedFirstNames = {"James", "Sarah"};
        String[] expectedLastNames = {"Smith", "Smythe"};
        String[] expectedPhoneNumbers = {"01234 567890", "02345 678901"};
        Gender[] expectedGenders = {MALE, FEMALE};
        Disability[] expectedDisabilities = {YES, NO};
        EthnicityResource[] expectedEthnicities = newEthnicityResource().buildArray(2, EthnicityResource.class);
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);
        String[] expectedEmails = {"test1@test.com", "test2@test.com"};

        String[] expectedSkillsAreas = {"Skills Area 1", "Skills Area 2"};
        BusinessType[] expectedBusinessType = {BUSINESS, ACADEMIC};
        List<AffiliationResource> expectedAffiliations = newAffiliationResource().build(2);
        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource().build(2);

        List<AssessorProfileResource> assessorProfileResources = newAssessorProfileResource()
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessType)
                .withAffiliations(expectedAffiliations)
                .withInnovationAreas(expectedInnovationAreas)
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

        AssessorProfileResource first = assessorProfileResources.get(0);
        assertEquals(expectedSkillsAreas[0], first.getSkillsAreas());
        assertEquals(expectedBusinessType[0], first.getBusinessType());
        assertEquals(expectedAffiliations, first.getAffiliations());
        assertEquals(expectedInnovationAreas, first.getInnovationAreas());
        assertEquals(expectedTitles[0], first.getTitle());
        assertEquals(expectedFirstNames[0], first.getFirstName());
        assertEquals(expectedLastNames[0], first.getLastName());
        assertEquals(expectedPhoneNumbers[0], first.getPhoneNumber());
        assertEquals(expectedGenders[0], first.getGender());
        assertEquals(expectedDisabilities[0], first.getDisability());
        assertEquals(expectedEthnicities[0], first.getEthnicity());
        assertEquals(expectedAddresses[0], first.getAddress());
        assertEquals(expectedEmails[0], first.getEmail());

        AssessorProfileResource second = assessorProfileResources.get(1);
        assertEquals(expectedSkillsAreas[1], second.getSkillsAreas());
        assertEquals(expectedBusinessType[1], second.getBusinessType());
        assertEquals(expectedAffiliations, second.getAffiliations());
        assertEquals(expectedInnovationAreas, second.getInnovationAreas());
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
