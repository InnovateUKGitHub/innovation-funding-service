package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.application.forms.questions.team.form.ApplicationTeamAddressForm;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddressViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationOrganisationAddressRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.address.resource.Countries.COUNTRIES;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationTeamAddressControllerTest extends BaseControllerMockMVCTest<ApplicationTeamAddressController> {

    @Override
    protected ApplicationTeamAddressController supplyControllerUnderTest() {
        return new ApplicationTeamAddressController();
    }

    @Mock
    private ApplicationOrganisationAddressRestService applicationOrganisationAddressRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Test
    public void getAddress() throws Exception {
        AddressResource address = newAddressResource()
                .withAddressLine1("1 street lane")
                .withAddressLine2("somewhere")
                .withTown("some town")
                .withPostcode("zip")
                .withCountry("Swindonland")
                .build();

        ApplicationResource application = newApplicationResource().build();
        OrganisationResource organisation = newOrganisationResource().build();
        long questionId = 40L;

        when(applicationOrganisationAddressRestService.getAddress(application.getId(), organisation.getId(), OrganisationAddressType.INTERNATIONAL)).thenReturn(restSuccess(address));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/team/organisation/{organisationId}/address", application.getId(), questionId, organisation.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team-address"))
                .andReturn();

        ApplicationTeamAddressViewModel actual = (ApplicationTeamAddressViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(application.getId(), actual.getApplicationId());
        assertEquals(organisation.getName(), actual.getOrganisationName());
        assertEquals(questionId, actual.getQuestionId());
        assertEquals(COUNTRIES, actual.getCountries());

        ApplicationTeamAddressForm form = (ApplicationTeamAddressForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAddressLine1(), address.getAddressLine1());
        assertEquals(form.getAddressLine2(), address.getAddressLine2());
        assertEquals(form.getTown(), address.getTown());
        assertEquals(form.getZipCode(), address.getPostcode());
        assertEquals(form.getCountry(), address.getCountry());
    }

    @Test
    public void updateAddress_validation() throws Exception {
        AddressResource address = newAddressResource()
                .withAddressLine1("1 street lane")
                .withAddressLine2("somewhere")
                .withTown("some town")
                .withPostcode("zip")
                .withCountry("Swindonland")
                .build();

        ApplicationResource application = newApplicationResource().build();
        OrganisationResource organisation = newOrganisationResource().build();
        long questionId = 40L;

        when(applicationOrganisationAddressRestService.getAddress(application.getId(), organisation.getId(), OrganisationAddressType.INTERNATIONAL)).thenReturn(restSuccess(address));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team/organisation/{organisationId}/address", application.getId(), questionId, organisation.getId()))
                .andExpect(view().name("application/questions/application-team-address"))
                .andExpect(model().attributeHasFieldErrorCode("form", "addressLine1", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("form", "town", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("form", "country", "NotBlank"));

        verify(applicationOrganisationAddressRestService, never()).updateAddress(anyLong(), anyLong(), any(), any());
    }

    @Test
    public void updateAddress() throws Exception {
        AddressResource address = newAddressResource()
                .withAddressLine1("1 street lane")
                .withAddressLine2("somewhere")
                .withTown("some town")
                .withPostcode("zip")
                .withCountry("Swindonland")
                .build();

        long applicationId = 1L;
        long organisationId = 2L;
        long questionId = 40L;

        when(applicationOrganisationAddressRestService.updateAddress(anyLong(), anyLong(), any(), any())).thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team/organisation/{organisationId}/address", applicationId, questionId, organisationId)
                .param("addressLine1", address.getAddressLine1())
                .param("addressLine2", address.getAddressLine2())
                .param("town", address.getTown())
                .param("zipCode", address.getPostcode())
                .param("country", address.getCountry()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)));

        verify(applicationOrganisationAddressRestService, never()).updateAddress(anyLong(), anyLong(), any(), eq(address));
    }
}
