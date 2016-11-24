  package com.worth.ifs.project.bankdetails.controller;

  import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.project.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

  public class ProjectBankDetailsControllerTest extends BaseControllerMockMVCTest<ProjectBankDetailsController> {
      private RestDocumentationResultHandler document;

      @Before
      public void setUpDocumentation() throws Exception {
          this.document = document("project/bank-details/{method-name}",
                  preprocessResponse(prettyPrint()));
      }

      @Override
      protected ProjectBankDetailsController supplyControllerUnderTest() {
          return new ProjectBankDetailsController();
      }

      @Test
      public void submitBanksDetailsSuccessfully() throws Exception {
          Long projectId = 1L;
          Long organisationId = 1L;
          OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().build();
          BankDetailsResource bankDetailsResource = newBankDetailsResource()
                  .withProject(projectId).withSortCode("123456")
                  .withAccountNumber("12345678")
                  .withOrganisation(organisationId)
                  .withOrganiationAddress(organisationAddressResource)
                  .build();

          when(bankDetailsServiceMock.submitBankDetails(bankDetailsResource)).thenReturn(serviceSuccess());

          mockMvc.perform(put("/project/{projectId}/bank-details", projectId).contentType(APPLICATION_JSON).content(toJson(bankDetailsResource))).andExpect(status().isOk()).andReturn();
      }

      @Test
      public void submitBankDetailsWithInvalidAccountDetailsReturnsError() throws Exception {
          Long projectId = 1L;
          Long organisationId = 1L;
          OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().build();
          BankDetailsResource bankDetailsResource = newBankDetailsResource()
                  .withProject(projectId).withSortCode("123")
                  .withAccountNumber("1234567")
                  .withOrganisation(organisationId)
                  .withOrganiationAddress(organisationAddressResource)
                  .build();

          when(bankDetailsServiceMock.submitBankDetails(bankDetailsResource)).thenReturn(serviceSuccess());

          Error invalidSortCodeError = fieldError("sortCode", "123", "validation.standard.sortcode.format", "", "", "\\d{6}");
          Error sortCodeNotProvided = fieldError("sortCode", null, "validation.standard.sortcode.required", "");
          Error invalidAccountNumberError = fieldError("accountNumber", "1234567", "validation.standard.accountnumber.format", "", "", "\\d{8}");
          Error accountNumberNotProvided = fieldError("accountNumber", null, "validation.standard.accountnumber.required", "");
          Error organisationAddressNotProvided = fieldError("organisationAddress", null, "validation.bankdetailsresource.organisationaddress.required", "");
          Error organisationIdNotProvided = fieldError("organisation", null, "validation.bankdetailsresource.organisation.required", "");
          Error projectIdNotProvided = fieldError("project", null, "validation.bankdetailsresource.project.required", "");

          RestErrorResponse expectedErrors = new RestErrorResponse(asList(invalidSortCodeError, invalidAccountNumberError));

          mockMvc.perform(put("/project/{projectId}/bank-details", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(bankDetailsResource)))
                  .andExpect(status().isNotAcceptable())
                  .andExpect(content().json(toJson(expectedErrors)))
                  .andReturn();

          bankDetailsResource = newBankDetailsResource().build();

          expectedErrors = new RestErrorResponse(asList(sortCodeNotProvided, accountNumberNotProvided, organisationAddressNotProvided, organisationIdNotProvided, projectIdNotProvided));

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
          OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().build();
          BankDetailsResource bankDetailsResource = newBankDetailsResource()
                  .withProject(projectId).withSortCode("123456")
                  .withAccountNumber("12345678")
                  .withOrganisation(organisationId)
                  .withOrganiationAddress(organisationAddressResource)
                  .build();

          when(bankDetailsServiceMock.updateBankDetails(bankDetailsResource)).thenReturn(serviceSuccess());

          mockMvc.perform(post("/project/{projectId}/bank-details", projectId).contentType(APPLICATION_JSON).content(toJson(bankDetailsResource))).andExpect(status().isOk()).andReturn();
      }

      @Test
      public void updateBankDetailsWithInvalidAccountDetailsReturnsError() throws Exception {
          Long projectId = 1L;
          Long organisationId = 1L;
          OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().build();
          BankDetailsResource bankDetailsResource = newBankDetailsResource()
                  .withProject(projectId).withSortCode("123")
                  .withAccountNumber("1234567")
                  .withOrganisation(organisationId)
                  .withOrganiationAddress(organisationAddressResource)
                  .build();

          when(bankDetailsServiceMock.updateBankDetails(bankDetailsResource)).thenReturn(serviceSuccess());

          Error invalidSortCodeError = fieldError("sortCode", "123", "validation.standard.sortcode.format", "", "", "\\d{6}");
          Error sortCodeNotProvided = fieldError("sortCode", null, "validation.standard.sortcode.required", "");
          Error invalidAccountNumberError = fieldError("accountNumber", "1234567", "validation.standard.accountnumber.format", "", "", "\\d{8}");
          Error accountNumberNotProvided = fieldError("accountNumber", null, "validation.standard.accountnumber.required", "");
          Error organisationAddressNotProvided = fieldError("organisationAddress", null, "validation.bankdetailsresource.organisationaddress.required", "");
          Error organisationIdNotProvided = fieldError("organisation", null, "validation.bankdetailsresource.organisation.required", "");
          Error projectIdNotProvided = fieldError("project", null, "validation.bankdetailsresource.project.required", "");

          RestErrorResponse expectedErrors = new RestErrorResponse(asList(invalidSortCodeError, invalidAccountNumberError));

          mockMvc.perform(post("/project/{projectId}/bank-details", projectId)
                  .contentType(APPLICATION_JSON)
                  .content(toJson(bankDetailsResource)))
                  .andExpect(status().isNotAcceptable())
                  .andExpect(content().json(toJson(expectedErrors)))
                  .andReturn();

          bankDetailsResource = newBankDetailsResource().build();

          expectedErrors = new RestErrorResponse(asList(sortCodeNotProvided, accountNumberNotProvided, organisationAddressNotProvided, organisationIdNotProvided, projectIdNotProvided));

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
