package com.worth.ifs.bankdetails;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.BankDetailsController;
import com.worth.ifs.project.form.BankDetailsForm;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.AddressLookupBaseController.FORM_ATTR_NAME;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BankDetailsControllerTest extends BaseControllerMockMVCTest<BankDetailsController> {
    @Override
    protected BankDetailsController supplyControllerUnderTest() {
        return new BankDetailsController();
    }

    @Test
    public void testEmptyFormWhenNoBankDetailsExist() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competitionResource.getId()).build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();
        OrganisationResource organisationResource = newOrganisationResource().build();
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectService.getOrganisationByProjectAndUser(projectResource.getId(), loggedInUser.getId())).thenReturn(organisationResource);
        when(bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectResource.getId(), organisationResource.getId())).thenReturn(restFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)));
        BankDetailsForm form = new BankDetailsForm();
        mockMvc.perform(get("/project/{id}/bank-details", projectResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("project", projectResource))
                .andExpect(model().attribute("currentUser", loggedInUser))
                .andExpect(model().attribute("organisation", organisationResource))
                .andExpect(model().attribute(FORM_ATTR_NAME, form))
                .andExpect(view().name("project/bank-details"));
    }
}
