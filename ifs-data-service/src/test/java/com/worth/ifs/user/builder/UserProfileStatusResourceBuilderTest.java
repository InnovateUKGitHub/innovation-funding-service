package com.worth.ifs.user.builder;

import com.worth.ifs.user.resource.UserProfileStatusResource;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.junit.Assert.assertEquals;

public class UserProfileStatusResourceBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedUser = 1L;
        boolean expectedSkillsComplete = true;
        boolean expectedAffiliationsComplete = false;
        boolean expectedContractComplete = true;

        UserProfileStatusResource userProfileStatusResource = newUserProfileStatusResource()
                .withUser(1L)
                .withSkillsComplete(expectedSkillsComplete)
                .withAffliliationsComplete(expectedAffiliationsComplete)
                .withContractComplete(expectedContractComplete)
                .build();

        assertEquals(expectedUser, userProfileStatusResource.getUser());
        assertEquals(expectedSkillsComplete, userProfileStatusResource.isSkillsComplete());
        assertEquals(expectedAffiliationsComplete, userProfileStatusResource.isAffiliationsComplete());
        assertEquals(expectedContractComplete, userProfileStatusResource.isContractComplete());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedUsers = {1L, 2L};
        Boolean[] expectedSkillsComplete = {true, false};
        Boolean[] expectedAffiliationsComplete = {false, true};
        Boolean[] expectedContractsComplete = {true, false};

        List<UserProfileStatusResource> userRegistrationResources = newUserProfileStatusResource()
                .withUser(1L, 2L)
                .withSkillsComplete(expectedSkillsComplete)
                .withAffliliationsComplete(expectedAffiliationsComplete)
                .withContractComplete(expectedContractsComplete)
                .build(2);

        UserProfileStatusResource first = userRegistrationResources.get(0);
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedSkillsComplete[0], first.isSkillsComplete());
        assertEquals(expectedAffiliationsComplete[0], first.isAffiliationsComplete());
        assertEquals(expectedContractsComplete[0], first.isContractComplete());

        UserProfileStatusResource second = userRegistrationResources.get(1);
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedSkillsComplete[1], second.isSkillsComplete());
        assertEquals(expectedAffiliationsComplete[1], second.isAffiliationsComplete());
        assertEquals(expectedContractsComplete[1], second.isContractComplete());
    }
}