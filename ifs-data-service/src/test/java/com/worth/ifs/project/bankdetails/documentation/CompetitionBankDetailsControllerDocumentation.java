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

        List<Application> applications = newApplication().build(2);

        List<Project> projects = newProject().withApplication(applications.get(0), applications.get(1)).build(2);

        List<Organisation> organisations = newOrganisation().withName("Hive IT", "Worth Systems").build(2);

        List<Address> addresses = newAddress().
                withAddressLine1("The Electric Works Concourse Way", "4-5").
                withAddressLine2("Sheaf St", "Bonhill Street").
                withAddressLine3("", "").
                withTown("Sheffield", "London").
                withCounty("South Yorkshire", "").
                withPostcode("S1 2BJ", "EC2A 4BX").
                build(2);

        List<OrganisationAddress> organisationAddresses = newOrganisationAddress().withAddress(addresses.get(0), addresses.get(1)).build(2);

        Long competitionId = 123L;

        List<BankDetails> bankDetailsList = newBankDetails().withAccountNumber("12345678", "87654321").withSortCode("123456", "654321").withOrganiationAddress(organisationAddresses.get(0), organisationAddresses.get(1)).withOrganisation(organisations.get(0), organisations.get(1)).withProject(projects.get(0), projects.get(1)).build(2);

        when(bankDetailsRepositoryMock.findByProjectApplicationCompetitionId(competitionId)).thenReturn(bankDetailsList);

        mockMvc.perform(get("/competition/{competitionId}/bank-details/export", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Company name\",\"com.worth.ifs.Application Number\",\"Company address\",\"Company address 2\",\"Company address 3\",\"Company address 4\",\"Company town/city\",\"Company county\",\"Company postcode\",\"Bank account name\",\"Bank account number\",\"Bank sort code\"\n"+
                        "\"Hive IT\",\"00000001\",\"The Electric Works Concourse Way\",\"Sheaf St\",\"\",\"\",\"Sheffield\",\"South Yorkshire\",\"S1 2BJ\",\"Hive IT\",\"12345678\",\"123456\"\n" +
                        "\"Worth Systems\",\"00000002\",\"4-5\",\"Bonhill Street\",\"\",\"\",\"London\",\"\",\"EC2A 4BX\",\"Worth Systems\",\"87654321\",\"654321\"\n"))
                .andDo(document("competition/bank-details/{method-name}",
                        responseHeaders(
                                headerWithName("Content-Type").description("Type of content in response body (plain text)"))));

        verify(bankDetailsRepositoryMock).findByProjectApplicationCompetitionId(competitionId);
    }
}
