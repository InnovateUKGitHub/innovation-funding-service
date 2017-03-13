package org.innovateuk.ifs.user.builder;

import org.innovateuk.ifs.user.resource.UserProfileStatusResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.junit.Assert.assertEquals;

public class UserProfileStatusResourceBuilderTest {

    @Test
    public void testBuildOne() {
        Long expectedUser = 1L;
        boolean expectedSkillsComplete = true;
        boolean expectedAffiliationsComplete = false;
        boolean expectedAgreementComplete = true;

        UserProfileStatusResource userProfileStatusResource = newUserProfileStatusResource()
                .withUser(1L)
                .withSkillsComplete(expectedSkillsComplete)
                .withAffliliationsComplete(expectedAffiliationsComplete)
                .withAgreementComplete(expectedAgreementComplete)
                .build();

        assertEquals(expectedUser, userProfileStatusResource.getUser());
        assertEquals(expectedSkillsComplete, userProfileStatusResource.isSkillsComplete());
        assertEquals(expectedAffiliationsComplete, userProfileStatusResource.isAffiliationsComplete());
        assertEquals(expectedAgreementComplete, userProfileStatusResource.isAgreementComplete());
    }

    @Test
    public void testBuildMany() {
        Long[] expectedUsers = {1L, 2L};
        Boolean[] expectedSkillsComplete = {true, false};
        Boolean[] expectedAffiliationsComplete = {false, true};
        Boolean[] expectedAgreementComplete = {true, false};

        List<UserProfileStatusResource> userRegistrationResources = newUserProfileStatusResource()
                .withUser(1L, 2L)
                .withSkillsComplete(expectedSkillsComplete)
                .withAffliliationsComplete(expectedAffiliationsComplete)
                .withAgreementComplete(expectedAgreementComplete)
                .build(2);

        UserProfileStatusResource first = userRegistrationResources.get(0);
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedSkillsComplete[0], first.isSkillsComplete());
        assertEquals(expectedAffiliationsComplete[0], first.isAffiliationsComplete());
        assertEquals(expectedAgreementComplete[0], first.isAgreementComplete());

        UserProfileStatusResource second = userRegistrationResources.get(1);
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedSkillsComplete[1], second.isSkillsComplete());
        assertEquals(expectedAffiliationsComplete[1], second.isAffiliationsComplete());
        assertEquals(expectedAgreementComplete[1], second.isAgreementComplete());
    }
}
