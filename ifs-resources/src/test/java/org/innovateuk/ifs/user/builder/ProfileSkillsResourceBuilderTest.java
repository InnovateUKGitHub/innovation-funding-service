package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileSkillsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class ProfileSkillsResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUser = 1L;
        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource()
                .withName("Data", "Cyber Security")
                .build(2);
        String expectedSkillsAreas = "skills areas";
        BusinessType expectedBusinessType = BUSINESS;

        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource()
                .withUser(expectedUser)
                .withInnovationAreas(expectedInnovationAreas)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessType)
                .build();

        assertEquals(expectedUser, profileSkillsResource.getUser());
        assertEquals(expectedInnovationAreas, profileSkillsResource.getInnovationAreas());
        assertEquals(expectedSkillsAreas, profileSkillsResource.getSkillsAreas());
        assertEquals(expectedBusinessType, profileSkillsResource.getBusinessType());
    }

    @Test
    public void buildMany() {
        Long[] expectedUsers = {1L, 2L};
        String[] expectedSkillsAreas = {"skills areas 1", "skills areas 2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};
        List<InnovationAreaResource>[] expectedInnovationAreas = new List[]{newInnovationAreaResource()
                .withName("Data", "Cyber Security")
                .build(2),
                newInnovationAreaResource()
                        .withName("User Experience", "Creative Economy")
                        .build(2)};

        List<ProfileSkillsResource> profileSkillsResource = newProfileSkillsResource()
                .withUser(expectedUsers)
                .withInnovationAreas(expectedInnovationAreas)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessTypes)
                .build(2);

        ProfileSkillsResource first = profileSkillsResource.get(0);

        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedInnovationAreas[0], first.getInnovationAreas());
        assertEquals(expectedSkillsAreas[0], first.getSkillsAreas());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());

        ProfileSkillsResource second = profileSkillsResource.get(1);

        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedInnovationAreas[1], second.getInnovationAreas());
        assertEquals(expectedSkillsAreas[1], second.getSkillsAreas());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
    }

}
