package com.worth.ifs.project.bankdetails.documentation;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.project.bankdetails.controller.CompetitionBankDetailsController;
import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static com.worth.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionBankDetailsControllerDocumentation extends BaseControllerMockMVCTest<CompetitionBankDetailsController> {

    @Override
    protected CompetitionBankDetailsController supplyControllerUnderTest() {
        return new CompetitionBankDetailsController();
    }

    @Test
    public void exportBankDetails() throws Exception {

        Application application = newApplication().build();

        Project project = newProject().withApplication(application).build();

        Organisation organisation = newOrganisation().build();

        Address address = newAddress().build();

        OrganisationAddress organisationAddress = newOrganisationAddress().withAddress(address).build();

        Long competitionId = 123L;

        List<BankDetails> bankDetailsList = newBankDetails().withAccountNumber("12345678").withSortCode("123456").withOrganiationAddress(organisationAddress).withOrganisation(organisation).withProject(project).build(2);

        when(bankDetailsRepositoryMock.findByProjectApplicationCompetitionId(competitionId)).thenReturn(bankDetailsList);

        mockMvc.perform(get("/competition/{competitionId}/bank-details/export", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Company name\",\"Application Number\",\"Address Line 1\",\"Address Line 2\",\"Address Line 3\",\"Town/City\",\"County\",\"Postcode\",\"Account name\",\"Account number\",\"Sort code\"\n" +
                        "\"Organisation 3\",\"00000001\",,,,,,,\"Organisation 3\",\"12345678\",\"123456\"\n" +
                        "\"Organisation 3\",\"00000001\",,,,,,,\"Organisation 3\",\"12345678\",\"123456\"\n"))
                .andDo(document("competition/bank-details/{method-name}",
                        responseHeaders(
                                headerWithName("Content-Type").description("Type of content in response body (plain text)"))));

        verify(bankDetailsRepositoryMock).findByProjectApplicationCompetitionId(competitionId);
    }
}
