package org.innovateuk.ifs.management.externalrole;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.management.externalrole.controller.ExternalRoleController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ExternalRoleControllerTest extends BaseControllerMockMVCTest<ExternalRoleController> {

    @Override
    protected ExternalRoleController supplyControllerUnderTest() {

        ExternalRoleController controller = new ExternalRoleController();
        ReflectionTestUtils.setField(controller, "externalRoleLinkEnabled", true);
        return controller;
    }

    @InjectMocks
    private ExternalRoleController externalRoleController;

    @Mock
    private UserRestService userRestService;

    @Test
    public void viewUser() throws Exception {

        long userId = 1l;

        when(userRestService.retrieveUserById(userId)).thenReturn(restSuccess(new UserResource()));

        mockMvc.perform(get("/admin/user/{userId}/external-role", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("externalrole/external-role"));

    }
}
