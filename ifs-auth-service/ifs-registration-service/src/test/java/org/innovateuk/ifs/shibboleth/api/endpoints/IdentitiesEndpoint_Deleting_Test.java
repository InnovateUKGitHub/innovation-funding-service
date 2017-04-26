package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedIdentitiesEndpoint;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IdentitiesEndpoint_Deleting_Test extends MockedIdentitiesEndpoint {

    @Test
    public void deleteIdentitySuccessfully() throws Exception {

        final UUID uuid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders

            .delete("/identities/" + uuid))

            .andExpect(status().isOk())
            .andExpect(content().string(isEmptyString()));

        verify(deleteService).deleteIdentity(uuid);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }

}
