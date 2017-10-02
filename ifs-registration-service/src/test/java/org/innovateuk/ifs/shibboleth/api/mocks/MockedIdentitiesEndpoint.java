package org.innovateuk.ifs.shibboleth.api.mocks;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.shibboleth.api.endpoints.IdentitiesEndpoint;
import org.innovateuk.ifs.shibboleth.api.services.*;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

public abstract class MockedIdentitiesEndpoint extends Mocked {

    @InjectMocks
    protected IdentitiesEndpoint identitiesEndpoint;

    @Mock
    protected CreateIdentityService createService;

    @Mock
    protected DeleteIdentityService deleteService;

    @Mock
    protected FindIdentityService findService;

    @Mock
    protected UpdateIdentityService updateService;

    @Mock
    protected ActivateUserService activateUserService;

    @Mock
    protected UserAccountLockoutService userAccountLockoutService;

    protected MockMvc mockMvc;


    @Before
    public void before() {
        Mockito.reset(createService, deleteService, findService, updateService, activateUserService, userAccountLockoutService);

        mockMvc = MockMvcBuilders.standaloneSetup(identitiesEndpoint).build();
    }


    protected byte[] convertToJson(final Object object) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

}
