package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.bankdetails.transactional.BankDetailsService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectBankDetailsControllerTest extends BaseControllerMockMVCTest<ProjectBankDetailsController> {

    @Mock
    private BankDetailsService bankDetailsServiceMock;

    @Override
    protected ProjectBankDetailsController supplyControllerUnderTest() {
        return new ProjectBankDetailsController();
    }

    @Test
    public void submitBanksDetailsSuccessfully() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        AddressResource addressResource = newAddressResource().build();
        BankDetailsResource bankDetailsResource = newBankDetailsResource()
                .withProject(projectId).withSortCode("123456")
                .withAccountNumber("12345678")
                .withOrganisation(organisationId)
                .withAddress(addressResource)
                .withCompanyName("Company name")
                .build();

        when(bankDetailsServiceMock.submitBankDetails(new ProjectOrganisationCompositeId(projectId, organisationId),
                bankDetailsResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/project/{projectId}/bank-details", projectId).contentType(APPLICATION_JSON).content(toJson(bankDetailsResource))).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void submitBankDetailsWithInvalidAccountDetailsReturnsError() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;

        AddressResource addressResource = newAddressResource().build();
        BankDetailsResource bankDetailsResource = newBankDetailsResource()
                .withProject(projectId).withSortCode("123")
                .withAccountNumber("1234567")
                .withOrganisation(organisationId)
                .withAddress(addressResource)
                .build();

        when(bankDetailsServiceMock.submitBankDetails(new ProjectOrganisationCompositeId(projectId, organisationId),
                bankDetailsResource)).thenReturn(serviceSuccess());

        Error invalidSortCodeError = fieldError("sortCode", "123", "validation.standard.sortcode.format", "", "", "\\d{6}");
        Error sortCodeNotProvided = fieldError("sortCode", null, "validation.standard.sortcode.required", "");
        Error invalidAccountNumberError = fieldError("accountNumber", "1234567", "validation.standard.accountnumber.format", "", "", "\\d{8}");
        Error accountNumberNotProvided = fieldError("accountNumber", null, "validation.standard.accountnumber.required", "");
        Error organisationAddressNotProvided = fieldError("address", null, "validation.bankdetailsresource.organisationaddress.required", "");
        Error organisationIdNotProvided = fieldError("organisation", null, "validation.bankdetailsresource.organisation.required", "");
        Error projectIdNotProvided = fieldError("project", null, "validation.bankdetailsresource.project.required", "");

        RestErrorResponse expectedErrors = new RestErrorResponse(asList(
                invalidSortCodeError,
                invalidAccountNumberError
        ) );

        mockMvc.perform(put("/project/{projectId}/bank-details", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(bankDetailsResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(expectedErrors)))
                .andReturn();

        bankDetailsResource = newBankDetailsResource().build();

        expectedErrors = new RestErrorResponse(asList(
                sortCodeNotProvided,
                accountNumberNotProvided,
                organisationAddressNotProvided,
                organisationIdNotProvided,
                projectIdNotProvided
        ));

        mockMvc.perform(put("/project/{projectId}/bank-details", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(bankDetailsResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(expectedErrors)));
    }

    @Test
    public void updateBanksDetailsSuccessfully() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;
        AddressResource addressResource = newAddressResource().build();
        BankDetailsResource bankDetailsResource = newBankDetailsResource()
                .withProject(projectId).withSortCode("123456")
                .withAccountNumber("12345678")
                .withOrganisation(organisationId)
                .withCompanyName("Company name")
                .withAddress(addressResource)
                .build();

        when(bankDetailsServiceMock.updateBankDetails(new ProjectOrganisationCompositeId(projectId, organisationId),
                bankDetailsResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/bank-details", projectId).contentType(APPLICATION_JSON).content(toJson(bankDetailsResource))).andExpect(status().isOk()).andReturn();
    }

    @Test
    public void updateBankDetailsWithInvalidAccountDetailsReturnsError() throws Exception {
        Long projectId = 1L;
        Long organisationId = 1L;

        AddressResource addressResource = newAddressResource().build();
        BankDetailsResource bankDetailsResource = newBankDetailsResource()
                .withProject(projectId).withSortCode("123")
                .withAccountNumber("1234567")
                .withOrganisation(organisationId)
                .withAddress(addressResource)
                .build();

        when(bankDetailsServiceMock.updateBankDetails(new ProjectOrganisationCompositeId(projectId, organisationId),
                bankDetailsResource)).thenReturn(serviceSuccess());

        Error invalidSortCodeError = fieldError("sortCode", "123", "validation.standard.sortcode.format", "", "", "\\d{6}");
        Error sortCodeNotProvided = fieldError("sortCode", null, "validation.standard.sortcode.required", "");
        Error invalidAccountNumberError = fieldError("accountNumber", "1234567", "validation.standard.accountnumber.format", "", "", "\\d{8}");
        Error accountNumberNotProvided = fieldError("accountNumber", null, "validation.standard.accountnumber.required", "");
        Error organisationAddressNotProvided = fieldError("address", null, "validation.bankdetailsresource.organisationaddress.required", "");
        Error organisationIdNotProvided = fieldError("organisation", null, "validation.bankdetailsresource.organisation.required", "");
        Error projectIdNotProvided = fieldError("project", null, "validation.bankdetailsresource.project.required", "");

        RestErrorResponse expectedErrors = new RestErrorResponse(asList(
                invalidSortCodeError,
                invalidAccountNumberError));

        mockMvc.perform(post("/project/{projectId}/bank-details", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(bankDetailsResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(expectedErrors)))
                .andReturn();

        bankDetailsResource = newBankDetailsResource().build();

        expectedErrors = new RestErrorResponse(asList(
                sortCodeNotProvided,
                accountNumberNotProvided,
                organisationAddressNotProvided,
                organisationIdNotProvided,
                projectIdNotProvided
        ));

        mockMvc.perform(post("/project/{projectId}/bank-details", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(bankDetailsResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(expectedErrors)));
    }


    @Test
    public void testGetBankDetailsStatusSummaryForProject() throws Exception {
        final Long projectId = 123L;

        ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary = newProjectBankDetailsStatusSummary().build();

        when(bankDetailsServiceMock.getProjectBankDetailsStatusSummary(projectId)).thenReturn(serviceSuccess(projectBankDetailsStatusSummary));

        mockMvc.perform(get("/project/{projectId}/bank-details/status-summary", projectId)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(projectBankDetailsStatusSummary)));
    }
}
