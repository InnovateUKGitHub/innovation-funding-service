package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.organisation.resource.BusinessType;
import org.innovateuk.ifs.user.resource.ProfileSkillsEditResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.builder.ProfileSkillsEditResourceBuilder.newProfileSkillsEditResource;
import static org.innovateuk.ifs.organisation.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.organisation.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class ProfileSkillsEditResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUser = 1L;
        String expectedSkillsAreas = "skills areas";
        BusinessType expectedBusinessType = BUSINESS;

        ProfileSkillsEditResource profileSkillsEditResource = newProfileSkillsEditResource()
                .withUser(expectedUser)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessType)
                .build();

        assertEquals(expectedUser, profileSkillsEditResource.getUser());
        assertEquals(expectedSkillsAreas, profileSkillsEditResource.getSkillsAreas());
        assertEquals(expectedBusinessType, profileSkillsEditResource.getBusinessType());
    }

    @Test
    public void buildMany() {
        Long[] expectedUsers = {1L, 2L};
        String[] expectedSkillsAreas = {"skills areas 1", "skills areas 2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};

        List<ProfileSkillsEditResource> profileSkillsEditResources = newProfileSkillsEditResource()
                .withUser(expectedUsers)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessTypes)
                .build(2);

        ProfileSkillsEditResource first = profileSkillsEditResources.get(0);

        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedSkillsAreas[0], first.getSkillsAreas());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());

        ProfileSkillsEditResource second = profileSkillsEditResources.get(1);

        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedSkillsAreas[1], second.getSkillsAreas());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
    }

}