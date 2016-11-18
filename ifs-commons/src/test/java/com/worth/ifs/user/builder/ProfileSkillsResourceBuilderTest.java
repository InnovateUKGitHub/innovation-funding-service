package com.worth.ifs.user.builder;

import com.worth.ifs.user.resource.BusinessType;
import com.worth.ifs.user.resource.ProfileSkillsResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.ProfileSkillsResourceBuilder.newProfileSkillsResource;
import static com.worth.ifs.user.resource.BusinessType.ACADEMIC;
import static com.worth.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class ProfileSkillsResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedUser = 1L;
        String expectedSkillsAreas = "skills areas";
        BusinessType expectedBusinessType = BUSINESS;

        ProfileSkillsResource profileSkillsResource = newProfileSkillsResource()
                .withUser(expectedUser)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessType)
                .build();

        assertEquals(expectedUser, profileSkillsResource.getUser());
        assertEquals(expectedSkillsAreas, profileSkillsResource.getSkillsAreas());
        assertEquals(expectedBusinessType, profileSkillsResource.getBusinessType());
    }

    @Test
    public void buildMany() {
        Long[] expectedUsers = {1L, 2L};
        String[] expectedSkillsAreas = {"skills areas 1", "skills areas 2"};
        BusinessType[] expectedBusinessTypes = {BUSINESS, ACADEMIC};

        List<ProfileSkillsResource> profileSkillsResources = newProfileSkillsResource()
                .withUser(expectedUsers)
                .withSkillsAreas(expectedSkillsAreas)
                .withBusinessType(expectedBusinessTypes)
                .build(2);

        ProfileSkillsResource first = profileSkillsResources.get(0);

        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedSkillsAreas[0], first.getSkillsAreas());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());

        ProfileSkillsResource second = profileSkillsResources.get(1);

        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedSkillsAreas[1], second.getSkillsAreas());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
    }

}