package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.AffiliationResourceBuilder.newAffiliationResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class ProfileResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        AddressResource expectedAddress = newAddressResource().build();
        String expectedSkillsAreas = "Skills Area";
        BusinessType expectedBusinessType = BUSINESS;
        List<AffiliationResource> expectedAffiliations = newAffiliationResource().build(2);
        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource().build(2);

        ProfileResource profileResource = newProfileResource()
                .withAffiliations(expectedAffiliations)
                .withBusinessType(expectedBusinessType)
                .withSkillsAreas(expectedSkillsAreas)
                .withInnovationAreas(expectedInnovationAreas)
                .withAddress(expectedAddress)
                .build();

        assertEquals(expectedBusinessType, profileResource.getBusinessType());
        assertEquals(expectedSkillsAreas, profileResource.getSkillsAreas());
        assertEquals(expectedInnovationAreas, profileResource.getInnovationAreas());
        assertEquals(expectedAffiliations, profileResource.getAffiliations());
        assertEquals(expectedAddress, profileResource.getAddress());
    }

    @Test
    public void buildMany() throws Exception {
        AddressResource[] expectedAddresses = newAddressResource().buildArray(2, AddressResource.class);
        String[] expectedSkillsAreas = {"Skills Area 1", "Skills Area 2"};
        BusinessType[] expectedBusinessType = {BUSINESS, ACADEMIC};
        List<AffiliationResource>[] expectedAffiliations = new List[]{newAffiliationResource().build(2), newAffiliationResource().build(2)};
        List<InnovationAreaResource>[] expectedInnovationAreas = new List[]{ newInnovationAreaResource().build(2),  newInnovationAreaResource().build(2)};

        List<ProfileResource> profileResources = newProfileResource()
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessType)
                .withAffiliations(expectedAffiliations)
                .withInnovationAreas(expectedInnovationAreas)
                .withAddress(expectedAddresses)
                .build(2);

        ProfileResource first = profileResources.get(0);
        assertEquals(expectedSkillsAreas[0], first.getSkillsAreas());
        assertEquals(expectedBusinessType[0], first.getBusinessType());
        assertEquals(expectedAffiliations[0], first.getAffiliations());
        assertEquals(expectedInnovationAreas[0], first.getInnovationAreas());
        assertEquals(expectedAddresses[0], first.getAddress());

        ProfileResource second = profileResources.get(1);
        assertEquals(expectedSkillsAreas[1], second.getSkillsAreas());
        assertEquals(expectedBusinessType[1], second.getBusinessType());
        assertEquals(expectedAffiliations[1], second.getAffiliations());
        assertEquals(expectedInnovationAreas[1], second.getInnovationAreas());
        assertEquals(expectedAddresses[1], second.getAddress());
    }
}
