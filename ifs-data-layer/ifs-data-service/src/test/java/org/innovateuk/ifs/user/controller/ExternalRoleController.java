package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.user.transactional.ExternalRoleService;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.Matchers.is;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.AgreementResourceBuilder.newAgreementResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExternalRoleController extends BaseControllerMockMVCTest<ExternalRoleController> {

    @Override
    protected ExternalRoleController supplyControllerUnderTest() {
        return new ExternalRoleController();
    }

    @Mock
    private ExternalRoleService externalRoleService;

    @Test
    public void addUserRole() throws Exception {
        when(externalRoleService.addUserRole(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(put("/user/{userId}/add-external-role", 1l))
                .andExpect(status().isOk());
    }
}
