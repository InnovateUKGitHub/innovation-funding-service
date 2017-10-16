package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedIdentitiesEndpoint;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IdentitiesEndpoint_Finding_Test extends MockedIdentitiesEndpoint {

    @Test
    public void findAnIdentitySuccessfully() throws Exception {

        final UUID uuid = UUID.randomUUID();
        final String email = "email@email.com";
        final String password = "password";

        setupAnIdentityToFind(uuid, password, email);

        mockMvc.perform(MockMvcRequestBuilders

            .get("/identities/" + uuid))

            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid", is(equalTo(uuid.toString()))))
            .andExpect(jsonPath("$.email", is(equalTo(email))));

        verify(findService).getIdentity(uuid);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }


    private void setupAnIdentityToFind(final UUID uuid, final String password, final String email) throws Exception {
        when(findService.getIdentity(uuid)).thenReturn(new Identity(uuid, email, password, false));
    }
}
