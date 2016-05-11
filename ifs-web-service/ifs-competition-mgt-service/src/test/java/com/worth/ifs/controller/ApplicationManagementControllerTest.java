package com.worth.ifs.controller;

import com.worth.ifs.controller.ApplicationManagementController;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static com.worth.ifs.application.service.Futures.settable;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationManagementControllerTest extends BaseControllerMockMVCTest<ApplicationManagementController>{

    @Test
    public void testDisplayApplicationForCompetitionAdministrator() throws Exception {
        Map<Long, FormInputResponseResource> mappedFormInputResponsesToFormInput = new HashMap<>();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()) )
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("applicationReadyForSubmit", false))
                .andExpect(model().attribute("isCompManagementDownload", true))
                .andExpect(model().attribute("responses", mappedFormInputResponsesToFormInput));
    }

    @Override
    protected ApplicationManagementController supplyControllerUnderTest() {
        return new ApplicationManagementController();
    }
}
