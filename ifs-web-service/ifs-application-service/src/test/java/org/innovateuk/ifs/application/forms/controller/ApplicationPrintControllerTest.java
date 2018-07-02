package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationPrintPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ui.Model;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationPrintControllerTest extends AbstractApplicationMockMVCTest<ApplicationPrintController> {
    @Mock
    private ApplicationPrintPopulator applicationPrintPopulator;

    @Override
    protected ApplicationPrintController supplyControllerUnderTest() {
        return new ApplicationPrintController();
    }

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void testApplicationPrint() throws Exception {
        ApplicationResource app = applications.get(0);

        when(applicationPrintPopulator.print(eq(1L), any(Model.class), any(UserResource.class))).thenReturn("uri");

        mockMvc.perform(get("/application/" + app.getId() + "/print"))
                .andExpect(status().isOk())
                .andExpect(view().name("uri"));

        verify(applicationPrintPopulator).print(eq(1L), any(Model.class), any(UserResource.class));
    }
}
