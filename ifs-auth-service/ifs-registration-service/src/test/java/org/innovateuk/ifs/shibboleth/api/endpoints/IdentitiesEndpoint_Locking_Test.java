package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedIdentitiesEndpoint;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IdentitiesEndpoint_Locking_Test extends MockedIdentitiesEndpoint {

    @Test
    public void unlockSuccesfully() throws Exception {

        final UUID uuid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders

            .delete("/identities/" + uuid + "/lock")
            .contentType(MediaType.APPLICATION_JSON_UTF8))

            .andExpect(status().isOk())
            .andExpect(content().string(isEmptyString()));

        verify(userAccountLockoutService).unlockAccount(uuid);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }


    @Test
    public void getLocked() throws Exception {

        final UUID uuid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders

            .get("/identities/" + uuid + "/lock")
            .contentType(MediaType.APPLICATION_JSON_UTF8))

            .andExpect(status().isOk())
            .andExpect(content().string(equalToIgnoringWhiteSpace("false")));

        verify(userAccountLockoutService).getAccountLockStatus(uuid);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }

}
