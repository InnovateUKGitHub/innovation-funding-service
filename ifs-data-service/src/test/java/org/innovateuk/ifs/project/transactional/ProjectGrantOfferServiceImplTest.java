package org.innovateuk.ifs.project.transactional;


import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.CostCategoryResourceBuilder.newCostCategoryResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ProjectGrantOfferServiceImplTest extends BaseServiceUnitTest<ProjectGrantOfferService> {

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;

    private Application application;
    private Organisation organisation;
    private Role leadApplicantRole;
    private Role projectManagerRole;
    private User user;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private FileEntryResource fileEntryResource;
    private PartnerOrganisation partnerOrganisation;
    private Project project;
    private OrganisationResource organisationResource;
    private SpendProfileTableResource table;

    @Before
    public void setUp() {

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().withGrantClaimPercentage(30).withApplication(456L).withOrganisation(3L)
                .build();
        table = new SpendProfileTableResource();

        table.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.44"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50.10"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("10.31"))));

        table.setCostCategoryGroupMap(asMap(
                LABOUR,  asList(asMap(
                        1L, asList(new BigDecimal("30.44"), new BigDecimal("30"), new BigDecimal("40")),
                        2L, asList(new BigDecimal("70"), new BigDecimal("50.10"), new BigDecimal("60")),
                        3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("10.31")))
                )));

        table.setEligibleCostPerCategoryMap(asMap(
                1L, asList(new BigDecimal("30.44"), new BigDecimal("30"), new BigDecimal("40")),
                2L, asList(new BigDecimal("70"), new BigDecimal("50.10"), new BigDecimal("60")),
                3L, asList(new BigDecimal("50"), new BigDecimal("5"), new BigDecimal("10.31"))
        ));
        table.setCostCategoryResourceMap(asMap(
                1L, newCostCategoryResource(),
                2L, newCostCategoryResource()
        ));
        table.setMonths(asList(
                new LocalDateResource(1, 3, 2019),new LocalDateResource(1, 4, 2019)));

        organisation = newOrganisation().build();
        organisationResource = newOrganisationResource().build();

        Competition competition = newCompetition().build();

        Address address = newAddress().withAddressLine1("test1")
                .withAddressLine2("test2")
                .withPostcode("PST")
                .withTown("town").build();

        leadApplicantRole = newRole(UserRoleType.LEADAPPLICANT).build();
        projectManagerRole = newRole(UserRoleType.PROJECT_MANAGER).build();

        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisation(organisation).
                withRole(leadApplicantRole).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisation).
                withRole(PROJECT_PARTNER).
                withUser(user).
                build();

        application = newApplication().
                withId(applicationId).
                withCompetition(competition).
                withProcessRoles(leadApplicantProcessRole).
                withName("My Application").
                withDurationInMonths(5L).
                withStartDate(LocalDate.of(2017, 3, 2)).
                build();

        partnerOrganisation = newPartnerOrganisation().withOrganisation(organisation).build();

        List<PartnerOrganisation> partnerOrganisations = new ArrayList<>();
        partnerOrganisations.add(partnerOrganisation);

        project = newProject().
                withId(projectId).
                withPartnerOrganisations(partnerOrganisations).
                withAddress(address).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();
        fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();


        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationMapperMock.mapToResource(organisation)).thenReturn(organisationResource);
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), organisation.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(projectFinanceServiceMock.getSpendProfileTable(any(ProjectOrganisationCompositeId.class)))
                .thenReturn(ServiceResult.serviceSuccess(table));
    }

    @Test
    public void testCreateSignedGrantOfferLetterFileEntry() {
        assertCreateFile(
                project::getSignedGrantOfferLetter,
                (fileToCreate, inputStreamSupplier) ->
                        service.createSignedGrantOfferLetterFileEntry(123L, fileToCreate, inputStreamSupplier));
    }

    @Test
    public void testCreateGrantOfferLetterFileEntry() {
        assertCreateFile(
                project::getGrantOfferLetter,
                (fileToCreate, inputStreamSupplier) ->
                        service.createGrantOfferLetterFileEntry(123L, fileToCreate, inputStreamSupplier));
    }


    @Test
    public void testCreateAdditionalContractFileEntry() {
        assertCreateFile(
                project::getAdditionalContractFile,
                (fileToCreate, inputStreamSupplier) ->
                        service.createAdditionalContractFileEntry(123L, fileToCreate, inputStreamSupplier));
    }


    @Test
    public void testGetAdditionalContractFileEntryDetails() {
        assertGetFileDetails(
                project::setAdditionalContractFile,
                () -> service.getAdditionalContractFileEntryDetails(123L));
    }

    @Test
    public void testGetGrantOfferLetterFileEntryDetails() {
        assertGetFileDetails(
                project::setGrantOfferLetter,
                () -> service.getGrantOfferLetterFileEntryDetails(123L));
    }

    @Test
    public void testGetSignedGrantOfferLetterFileEntryDetails() {
        assertGetFileDetails(
                project::setSignedGrantOfferLetter,
                () -> service.getSignedGrantOfferLetterFileEntryDetails(123L));
    }

    @Test
    public void testGetAdditionalContractFileContents() {
        assertGetFileContents(
                project::setAdditionalContractFile,
                () -> service.getAdditionalContractFileAndContents(123L));
    }

    @Test
    public void testGetGrantOfferLetterFileContents() {
        assertGetFileContents(
                project::setGrantOfferLetter,
                () -> service.getGrantOfferLetterFileAndContents(123L));
    }

    @Test
    public void testGetSignedGrantOfferLetterFileContents() {
        assertGetFileContents(
                project::setSignedGrantOfferLetter,
                () -> service.getSignedGrantOfferLetterFileAndContents(123L));
    }

    @Test
    public void testUpdateSignedGrantOfferLetterFileEntry() {
        assertUpdateFile(
                project::getSignedGrantOfferLetter,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testSubmitGrantOfferLetterFailure() {

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        Assert.assertFalse(result.isSuccess());
        Assert.assertThat(project.getOfferSubmittedDate(), nullValue());
    }

    @Test
    public void testSubmitGrantOfferLetterSuccess() {
        project.setSignedGrantOfferLetter(mock(FileEntry.class));
        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.isSuccess());
        Assert.assertThat(project.getOfferSubmittedDate(), notNullValue());
    }

    @Test
    public void testGenerateGrantOfferLetter() {
        assertGenerateFile(
                fileEntryResource ->
                        service.generateGrantOfferLetter(123L, fileEntryResource));
    }

    @Test
    public void testRemoveGrantOfferLetterFileEntry() {

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(fileServiceMock.deleteFile(existingGOLFile.getId())).thenReturn(serviceSuccess(existingGOLFile));

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isSuccess());
        assertNull(project.getGrantOfferLetter());
        verify(fileServiceMock).deleteFile(existingGOLFile.getId());
    }

    @Override
    protected ProjectGrantOfferService supplyServiceUnderTest() {
        return new ProjectGrantOfferServiceImpl();
    }
}
