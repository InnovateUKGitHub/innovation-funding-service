package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedIdentitiesEndpoint;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.innovateuk.ifs.shibboleth.api.models.NewIdentity;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IdentitiesEndpoint_Creating_Test extends MockedIdentitiesEndpoint {

    @Test
    public void createNewIdentitySuccessfully() throws Exception {

        final String uuid = "31a05805-c748-492d-a862-c047102516be";
        final String email = "email@email.com";
        final String password = "P@55word";

        setupSuccessfullyCreatedIdentity(uuid, password, email);

        mockMvc.perform(MockMvcRequestBuilders

            .post("/identities")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(convertToJson(new NewIdentity(email, password))))

            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.uuid", is(equalTo(uuid))))
            .andExpect(jsonPath("$.email", is(equalTo(email))))
            .andExpect(header().string(HttpHeaders.LOCATION, is(equalTo("/identities/" + uuid))));

        verify(createService).createIdentity(email, password);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }


    private void setupSuccessfullyCreatedIdentity(final String uuid, final String password, final String email)
        throws Exception {
        when(createService.createIdentity(email, password))
            .thenReturn(new Identity(UUID.fromString(uuid), email, password, false));
    }
}
