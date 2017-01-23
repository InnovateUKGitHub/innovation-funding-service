package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.resource.ProfileResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;

public class AssessorProfileResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        UserResource expectedUserResource = newUserResource().build();
        ProfileResource expectedProfileResource = newProfileResource().build();

        AssessorProfileResource assessorProfileResource = newAssessorProfileResource()
                .withUser(expectedUserResource)
                .withProfile(expectedProfileResource)
                .build();

        assertEquals(expectedUserResource, assessorProfileResource.getUser());
        assertEquals(expectedProfileResource, assessorProfileResource.getProfile());
    }

    @Test
    public void buildMany() throws Exception {
        UserResource[] expectedUserResources = newUserResource().buildArray(2, UserResource.class);
        ProfileResource[] expectedProfileResources = newProfileResource().buildArray(2, ProfileResource.class);

        List<AssessorProfileResource> assessorProfileResources = newAssessorProfileResource()
                .withUser(expectedUserResources)
                .withProfile(expectedProfileResources)
                .build(2);

        AssessorProfileResource first = assessorProfileResources.get(0);
        assertEquals(expectedUserResources[0], first.getUser());
        assertEquals(expectedProfileResources[0], first.getProfile());

        AssessorProfileResource second = assessorProfileResources.get(1);
        assertEquals(expectedUserResources[1], second.getUser());
        assertEquals(expectedProfileResources[1], second.getProfile());
    }
}
