package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedIdentitiesEndpoint;
import org.innovateuk.ifs.shibboleth.api.models.ChangeEmail;
import org.innovateuk.ifs.shibboleth.api.models.ChangePassword;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.isEmptyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IdentitiesEndpoint_Updating_Test extends MockedIdentitiesEndpoint {

    @Test
    public void updateIdentityEmailSuccessfully() throws Exception {

        final UUID uuid = UUID.randomUUID();
        final String email = "new@email.com";

        mockMvc.perform(MockMvcRequestBuilders

            .put("/identities/" + uuid + "/email")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(convertToJson(new ChangeEmail(email))))

            .andExpect(status().isOk())
            .andExpect(content().string(isEmptyString()));

        verify(updateService).changeEmail(uuid, email);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }


    @Test
    public void updateIdentityPasswordSuccessfully() throws Exception {

        final UUID uuid = UUID.randomUUID();
        final String password = "new-Pa55word";

        mockMvc.perform(MockMvcRequestBuilders

            .put("/identities/" + uuid + "/password")
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(convertToJson(new ChangePassword(password))))

            .andExpect(status().isOk())
            .andExpect(content().string(isEmptyString()));

        verify(updateService).changePassword(uuid, password);

        verifyNoMoreInteractions(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);
    }

}
