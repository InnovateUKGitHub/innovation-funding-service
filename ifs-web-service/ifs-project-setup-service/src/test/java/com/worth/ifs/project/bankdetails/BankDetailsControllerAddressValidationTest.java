package com.worth.ifs.project.bankdetails;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.builder.AddressResourceBuilder;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.BankDetailsController;
import com.worth.ifs.project.bankdetails.form.BankDetailsForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collection;

import static com.worth.ifs.address.resource.OrganisationAddressType.ADD_NEW;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
