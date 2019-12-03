package org.innovateuk.ifs.user;

import org.apache.commons.lang3.SerializationUtils;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

    @Test
    public void serialization() {
        UserResource user =  newUserResource().withId(1L)
                .withFirstName("James")
                .withLastName("Watts")
                .withEmail("james.watts@email.co.uk")
                .withRolesGlobal(singletonList(Role.APPLICANT))
                .withUID("2aerg234-aegaeb-23aer").build();
        ServiceResult<UserResource> result = serviceSuccess(user);

        ServiceResult<UserResource> copy = SerializationUtils.clone(result);
        assertEquals(result, copy);
    }
}
