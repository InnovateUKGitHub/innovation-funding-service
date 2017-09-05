package org.innovateuk.ifs.project.bankdetails;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.builder.AddressResourceBuilder;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.bankdetails.controller.BankDetailsController;
import org.innovateuk.ifs.project.bankdetails.form.BankDetailsForm;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.ADD_NEW;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(Parameterized.class)
public class BankDetailsControllerAddressValidationTest extends BaseControllerMockMVCTest<BankDetailsController> {

    private BankDetailsForm form;

    private String expectedView;

    public BankDetailsControllerAddressValidationTest(BankDetailsForm form, String expectedView) {
        this.form = form;
        this.expectedView = expectedView;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        AddressResource addressResourceWithNullAddressLine1 = AddressResourceBuilder.newAddressResource().build();
        BankDetailsForm formWithSelectedPostcodeWithNullAddressLine1 = new BankDetailsForm();
        formWithSelectedPostcodeWithNullAddressLine1.getAddressForm().setSelectedPostcode(addressResourceWithNullAddressLine1);

        AddressResource addressResourceWithNullTown = AddressResourceBuilder.newAddressResource().
                withAddressLine1("add Line1").build();
        BankDetailsForm formWithSelectedPostcodeWithNullTown = new BankDetailsForm();
        formWithSelectedPostcodeWithNullTown.getAddressForm().setSelectedPostcode(addressResourceWithNullTown);

        AddressResource addressResourceWithNullPostcode = AddressResourceBuilder.newAddressResource().
                withAddressLine1("add Line1").withTown("Town1").build();
        BankDetailsForm formWithSelectedPostcodeWithNullPostcode = new BankDetailsForm();
        formWithSelectedPostcodeWithNullPostcode.getAddressForm().setSelectedPostcode(addressResourceWithNullPostcode);

        return Arrays.asList(new Object[][]{
                {formWithSelectedPostcodeWithNullAddressLine1, "project/bank-details"},
                {formWithSelectedPostcodeWithNullTown, "project/bank-details"},
                {formWithSelectedPostcodeWithNullPostcode, "project/bank-details"},
        });
    }


    @Override
    protected BankDetailsController supplyControllerUnderTest() {
        return new BankDetailsController();
    }

    @Test
    public void testUpdateBankDetailsWithVariousErrorScenarios() throws Exception {

        ProjectResource projectResource = setUpMockingForUpdateBankDetails();

        mockMvc.perform(post("/project/{id}/bank-details", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("sortCode", "123456").
                param("accountNumber", "12345678").
                param("addressType", ADD_NEW.name()).
                param("addressForm.selectedPostcode.addressLine1", form.getAddressForm().getSelectedPostcode().getAddressLine1()).
                param("addressForm.selectedPostcode.town", form.getAddressForm().getSelectedPostcode().getTown()).
                param("addressForm.selectedPostcode.postcode", form.getAddressForm().getSelectedPostcode().getPostcode())).
                andExpect(view().name(expectedView));

        verify(bankDetailsRestService, never()).updateBankDetails(any(), any());

    }

    private ProjectResource setUpMockingForUpdateBankDetails() {

        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)));

        return projectResource;
    }
}
