package org.innovateuk.ifs.project.bankdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionBankDetailsControllerTest extends BaseControllerMockMVCTest<CompetitionBankDetailsController> {

    @Override
    protected CompetitionBankDetailsController supplyControllerUnderTest() {
        return new CompetitionBankDetailsController();
    }

    @Test
    public void exportBankDetailsWhenSomeExist() throws Exception {

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
                .andExpect(content().string("\"Company name\",\"Application Number\",\"Company address\",\"Company address 2\",\"Company address 3\",\"Company address 4\",\"Company town/city\",\"Company county\",\"Company postcode\",\"Bank account name\",\"Bank account number\",\"Bank sort code\"\n"+
                        "\"Hive IT\",\"1\",\"The Electric Works Concourse Way\",\"Sheaf St\",\"\",\"\",\"Sheffield\",\"South Yorkshire\",\"S1 2BJ\",\"Hive IT\",\"12345678\",\"123456\"\n" +
                        "\"Worth Systems\",\"2\",\"4-5\",\"Bonhill Street\",\"\",\"\",\"London\",\"\",\"EC2A 4BX\",\"Worth Systems\",\"87654321\",\"654321\"\n"));

        verify(bankDetailsRepositoryMock).findByProjectApplicationCompetitionId(competitionId);
    }

    @Test
    public void exportBankDetailsWhenNoneExist() throws Exception {

        Long competitionId = 123L;

        when(bankDetailsRepositoryMock.findByProjectApplicationCompetitionId(competitionId)).thenReturn(emptyList());

        mockMvc.perform(get("/competition/{competitionId}/bank-details/export", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string("\"Company name\",\"Application Number\",\"Company address\",\"Company address 2\",\"Company address 3\",\"Company address 4\",\"Company town/city\",\"Company county\",\"Company postcode\",\"Bank account name\",\"Bank account number\",\"Bank sort code\"\n"));

        verify(bankDetailsRepositoryMock).findByProjectApplicationCompetitionId(competitionId);
    }
}
