package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseResource;
import org.innovateuk.ifs.knowledgebase.resourse.KnowledgeBaseType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.registration.form.*;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.KnowledgeBaseRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationKnowledgeBaseControllerTest extends BaseControllerMockMVCTest<OrganisationCreationKnowledgeBaseController> {

    static final String BASE_URL = "/organisation/create";

    @Mock
    private KnowledgeBaseRestService knowledgeBaseRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationTypeRestService organisationTypeRestService;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private OrganisationJourneyEnd organisationJourneyEnd;

    protected OrganisationCreationKnowledgeBaseController supplyControllerUnderTest() {
        return new OrganisationCreationKnowledgeBaseController();
    }

    @Test
    public void selectKnowledgeBase() throws Exception {
        InviteAndIdCookie projectInviteCookie = new InviteAndIdCookie(1L, "hash");
        when(registrationCookieService.getProjectInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(projectInviteCookie));
        when(knowledgeBaseRestService.getKnowledgeBases()).thenReturn(restSuccess(singletonList("KnowledgeBase1")));

        mockMvc.perform(get(BASE_URL + "/knowledge-base"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/knowledge-base"));
    }

    @Test
    public void selectedKnowledgeBase() throws Exception {

        String name = "KnowledgeBase 1";

        KnowledgeBaseResource knowledgeBaseResource = new KnowledgeBaseResource();
        knowledgeBaseResource.setName(name);
        knowledgeBaseResource.setRegistrationNumber("123456789");
        knowledgeBaseResource.setType(KnowledgeBaseType.CATAPULT);

        KnowledgeBaseForm knowledgeBaseForm = new KnowledgeBaseForm();
        knowledgeBaseForm.setKnowledgeBase(name);

        OrganisationCreationForm organisationCreationForm = new OrganisationCreationForm();
        organisationCreationForm.setOrganisationName(knowledgeBaseForm.getKnowledgeBase());
        organisationCreationForm.setOrganisationSearchName(knowledgeBaseForm.getKnowledgeBase());
        organisationCreationForm.setOrganisationTypeId(5L);

        when(registrationCookieService.getOrganisationCreationCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(organisationCreationForm));
        when(knowledgeBaseRestService.getKnowledgeBases()).thenReturn(restSuccess(singletonList(organisationCreationForm.getOrganisationName())));
        when(knowledgeBaseRestService.getKnowledgeBaseByName(name)).thenReturn(restSuccess(knowledgeBaseResource));

        mockMvc.perform(post(BASE_URL + "/knowledge-base")
                .param("knowledgeBase", name))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/knowledge-base/confirm"));
    }

    @Test
    public void knowledgeBaseDetails() throws Exception {
        List<OrganisationTypeResource> organisationTypes = newOrganisationTypeResource()
                .withId(4L, 5L)
                .withName("Catapult", "University")
                .withVisibleInSetup(Boolean.TRUE, Boolean.TRUE)
                .build(2);

        when(organisationTypeRestService.getAll()).thenReturn(restSuccess(organisationTypes));

        mockMvc.perform(get(BASE_URL + "/knowledge-base/details"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/knowledge-base-details"));
    }

    @Test
    public void saveKnowledgeBaseDetails() throws Exception {

        List<OrganisationTypeResource> organisationTypes = newOrganisationTypeResource()
                .withId(4L, 5L)
                .withName("Catapult", "University")
                .withVisibleInSetup(Boolean.TRUE, Boolean.TRUE)
                .build(2);

        when(organisationTypeRestService.getAll()).thenReturn(restSuccess(organisationTypes));

        mockMvc.perform(post(BASE_URL + "/knowledge-base/details")
                .param("name", "knowledgeBase")
                .param("type", "CATAPULT")
                .param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                        param("addressForm.manualAddress.addressLine1", "Montrose House 1").
                        param("addressForm.manualAddress.addressLine2", "Clayhill Park").
                        param("addressForm.manualAddress.addressLine3", "Cheshire West and Chester").
                        param("addressForm.manualAddress.town", "Neston").
                        param("addressForm.manualAddress.county", "Cheshire").
                        param("addressForm.manualAddress.postcode", "CH64 3RU"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/knowledge-base/confirm"));
    }

    @Test
    public void addressFormAction() throws Exception {

        List<OrganisationTypeResource> organisationTypes = newOrganisationTypeResource()
                .withId(4L, 5L)
                .withName("Catapult", "University")
                .withVisibleInSetup(Boolean.TRUE, Boolean.TRUE)
                .build(2);

        when(organisationTypeRestService.getAll()).thenReturn(restSuccess(organisationTypes));

        mockMvc.perform(post(BASE_URL + "/knowledge-base/details")
                .param("addressForm.action", "true")
                .param("name", "knowledgeBase")
                .param("organisationType", "5")
                .param("addressForm.addressType", AddressForm.AddressType.MANUAL_ENTRY.name()).
                        param("addressForm.manualAddress.addressLine1", "Montrose House 1").
                        param("addressForm.manualAddress.addressLine2", "Clayhill Park").
                        param("addressForm.manualAddress.addressLine3", "Cheshire West and Chester").
                        param("addressForm.manualAddress.town", "Neston").
                        param("addressForm.manualAddress.county", "Cheshire").
                        param("addressForm.manualAddress.postcode", "CH64 3RU"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/knowledge-base-details"));
    }

    @Test
    public void confirmKnowledgeBaseDetails() throws Exception {
        mockMvc.perform(get(BASE_URL + "/knowledge-base/confirm"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/knowledge-base-confirm-organisation"));
    }

    @Test
    public void saveOrganisation() throws Exception {

        String view = "view";

        OrganisationResource organisationResource = newOrganisationResource()
                .withId(1l)
                .build();

        AddressForm addressForm = new AddressForm();
        AddressResource address = new AddressResource(null, null, null, null, null, null);
        addressForm.setManualAddress(address);
        addressForm.setAddressType(AddressForm.AddressType.MANUAL_ENTRY);

        KnowledgeBaseCreateForm knowledgeBaseCreateForm = new KnowledgeBaseCreateForm();
        knowledgeBaseCreateForm.setCatapultNumber("123456789");
        knowledgeBaseCreateForm.setType(KnowledgeBaseType.CATAPULT);
        knowledgeBaseCreateForm.setName("knowledge");
        knowledgeBaseCreateForm.setAddressForm(addressForm);

        OrganisationTypeForm organisationTypeForm = new OrganisationTypeForm();
        organisationTypeForm.setOrganisationType(6l);

        when(organisationRestService.createOrMatch(any())).thenReturn(restSuccess(organisationResource));
        when(organisationJourneyEnd.completeProcess(any(), any(), eq(loggedInUser), eq(organisationResource.getId()))).thenReturn(view);
        when(registrationCookieService.getKnowledgeBaseDetailsValue(any(HttpServletRequest.class))).thenReturn(Optional.of(knowledgeBaseCreateForm));
        when(registrationCookieService.getOrganisationTypeCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(organisationTypeForm));

        mockMvc.perform(post(BASE_URL + "/knowledge-base/save-organisation")
                .param("addressForm.action", "true")
                .param("name", "knowledgeBase")
                .param("type", "CATAPULT"))
                .andExpect(status().isOk())
                .andExpect(view().name(view));
    }
}