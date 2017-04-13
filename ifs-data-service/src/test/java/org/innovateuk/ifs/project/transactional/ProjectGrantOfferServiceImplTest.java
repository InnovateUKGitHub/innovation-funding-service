package org.innovateuk.ifs.project.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.LocalDateResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder;
import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.gol.YearlyGOLProfileTable;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_ALREADY_COMPLETE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.LABOUR;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static org.innovateuk.ifs.project.builder.CostCategoryResourceBuilder.newCostCategoryResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckSummaryResourceBuilder.newFinanceCheckSummaryResource;
import static org.innovateuk.ifs.project.transactional.ProjectGrantOfferServiceImpl.GRANT_OFFER_LETTER_DATE_FORMAT;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.RESEARCH;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class ProjectGrantOfferServiceImplTest extends BaseServiceUnitTest<ProjectGrantOfferService> {

    private Long projectId = 123L;
    private Long applicationId = 456L;
    private Long userId = 7L;

    private Application application;
    private List<Organisation> organisations;
    private Organisation nonAcademicUnfunded;
    private Role leadApplicantRole;
    private User user;
    private ProcessRole leadApplicantProcessRole;
    private ProjectUser leadPartnerProjectUser;
    private Project project;
    private List<OrganisationResource> organisationResources;
    private SpendProfileTableResource table;
    private SpendProfileTableResource tableZero;

    private List<BigDecimal> monthlyTotalsZero = asList(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO) ;

    private ApplicationFinanceResource applicationFinanceResourceZero = newApplicationFinanceResource().withGrantClaimPercentage(0)
            .withApplication(456L).withOrganisation(3L)
            .build();

    private List<BigDecimal> yearlyCosts = asList(
            BigDecimal.valueOf(500),
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(200));

    private Address address;

    private FileEntryResource fileEntryResource;

    private FileEntry createdFile;

    private String htmlFile;

    private Pair<File, FileEntry> fileEntryPair;

    @Captor
    ArgumentCaptor<Map<String, Object> > templateArgsCaptor;

    @Captor
    ArgumentCaptor<String> templateCaptor;

    @Captor
    ArgumentCaptor<FileEntryResource> fileEntryResCaptor;

    @Captor
    ArgumentCaptor<Supplier<InputStream>> supplierCaptor;

    @Before
    public void setUp() {

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().withGrantClaimPercentage(30)
                .withApplication(456L).withOrganisation(3L)
                .build();
        table = new SpendProfileTableResource();
        table.setMarkedAsComplete(true);
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
                1L, newCostCategoryResource().withName("Labour").build(),
                2L, newCostCategoryResource().withName("Materials").build(),
                3L, newCostCategoryResource().withName("Other").build()
        ));
        table.setMonths(asList(
                new LocalDateResource(1, 3, 2019),new LocalDateResource(1, 4, 2019)));

        tableZero = new SpendProfileTableResource();
        tableZero.setMarkedAsComplete(true);
        tableZero.setMonthlyCostsPerCategoryMap(asMap(
                1L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                2L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                3L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)));

        tableZero.setCostCategoryGroupMap(asMap(
                LABOUR,  asList(asMap(
                        1L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        2L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                        3L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
                ))));

        tableZero.setEligibleCostPerCategoryMap(asMap(
                1L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                2L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                3L, asList(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
        ));
        tableZero.setCostCategoryResourceMap(asMap(
                1L, newCostCategoryResource().withName("Labour").build(),
                2L, newCostCategoryResource().withName("Materials").build(),
                3L, newCostCategoryResource().withName("Other").build()
        ));
        tableZero.setMonths(asList(
                new LocalDateResource(1, 3, 2019),new LocalDateResource(1, 4, 2019)));

        organisations = newOrganisation().withOrganisationType(RESEARCH).withName("Org1&", "Org2\"", "Org3<").build(3);
        nonAcademicUnfunded = newOrganisation().withOrganisationType(BUSINESS).withName("Org4").build();
        organisationResources = newOrganisationResource().build(4);

        Competition competition = newCompetition().build();

        address = newAddress().withAddressLine1("test1")
                .withAddressLine2("test2")
                .withPostcode("PST")
                .withTown("town").build();

        leadApplicantRole = newRole(UserRoleType.LEADAPPLICANT).build();

        user = newUser().
                withId(userId).
                build();

        leadApplicantProcessRole = newProcessRole().
                withOrganisationId(organisations.get(0).getId()).
                withRole(leadApplicantRole).
                withUser(user).
                build();

        leadPartnerProjectUser = newProjectUser().
                withOrganisation(organisations.get(0)).
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

        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisations.get(0)).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisations.get(1)).build();
        PartnerOrganisation partnerOrganisation3 = newPartnerOrganisation().withOrganisation(organisations.get(2)).build();

        List<PartnerOrganisation> partnerOrganisations = new ArrayList<>();
        partnerOrganisations.add(partnerOrganisation);
        partnerOrganisations.add(partnerOrganisation2);
        partnerOrganisations.add(partnerOrganisation3);

        project = newProject().
                withId(projectId).
                withPartnerOrganisations(partnerOrganisations).
                withAddress(address).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();

        FinanceCheckSummaryResource financeCheckSummaryResource = newFinanceCheckSummaryResource()
                .withTotalPercentageGrant(BigDecimal.valueOf(25))
                .build();

        List<BigDecimal> monthlyTotals = asList(
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(20));

        List<BigDecimal> yearlyCosts = asList(
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200));

        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(organisationRepositoryMock.findOne(organisations.get(0).getId())).thenReturn(organisations.get(0));
        when(organisationRepositoryMock.findOne(organisations.get(1).getId())).thenReturn(organisations.get(1));
        when(organisationRepositoryMock.findOne(organisations.get(2).getId())).thenReturn(organisations.get(2));
        when(organisationMapperMock.mapToResource(organisations.get(0))).thenReturn(organisationResources.get(0));
        when(organisationMapperMock.mapToResource(organisations.get(1))).thenReturn(organisationResources.get(1));
        when(organisationMapperMock.mapToResource(organisations.get(2))).thenReturn(organisationResources.get(2));
        when(financeUtilMock.isUsingJesFinances(RESEARCH.getOrganisationTypeId())).thenReturn(true);
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), organisations.get(0).getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), organisations.get(1).getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), organisations.get(2).getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(spendProfileServiceMock.getSpendProfileTable(any(ProjectOrganisationCompositeId.class)))
                .thenReturn(ServiceResult.serviceSuccess(table));
        when(spendProfileTableCalculatorMock.calculateMonthlyTotals(table.getMonthlyCostsPerCategoryMap(),
                table.getMonths().size())).thenReturn(monthlyTotals);
        when(spendProfileTableCalculatorMock.calculateEligibleCostPerYear(any(ProjectResource.class), any(List.class), any(List.class))).thenReturn(yearlyCosts);
        when(financeCheckServiceMock.getFinanceCheckSummary(project.getId())).thenReturn(ServiceResult.serviceSuccess(financeCheckSummaryResource));
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
        when(golWorkflowHandlerMock.isSent(any())).thenReturn(Boolean.TRUE);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        assertUpdateFile(
                project::getSignedGrantOfferLetter,
                (fileToUpdate, inputStreamSupplier) ->
                        service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier));
    }

    @Test
    public void testUpdateSignedGrantOfferLetterFileEntryProjectLive() {

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(projectWorkflowHandlerMock.getState(any())).thenReturn(ProjectState.LIVE);
        when(golWorkflowHandlerMock.isSent(any())).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));

    }

    @Test
    public void testUpdateSignedGrantOfferLetterFileEntryGolNotSent() {

        FileEntryResource fileToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(projectWorkflowHandlerMock.getState(any())).thenReturn(ProjectState.SETUP);
        when(golWorkflowHandlerMock.isSent(any())).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.updateSignedGrantOfferLetterFile(123L, fileToUpdate, inputStreamSupplier);
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0).getErrorKey(), CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY.toString());

    }


    @Test
    public void testSubmitGrantOfferLetterFailureNoSignedGolFile() {

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.getFailure().is(CommonFailureKeys.SIGNED_GRANT_OFFER_LETTER_MUST_BE_UPLOADED_BEFORE_SUBMIT));
        Assert.assertThat(project.getOfferSubmittedDate(), nullValue());
    }

    @Test
    public void testSubmitGrantOfferLetterFailureCannotReachSignedState() {
        project.setSignedGrantOfferLetter(mock(FileEntry.class));

        when(golWorkflowHandlerMock.sign(any())).thenReturn(Boolean.FALSE);

        ServiceResult<Void> result = service.submitGrantOfferLetter(projectId);

        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_CANNOT_SET_SIGNED_STATE));
        Assert.assertThat(project.getOfferSubmittedDate(), nullValue());
    }

    @Test
    public void testSubmitGrantOfferLetterSuccess() {
        project.setSignedGrantOfferLetter(mock(FileEntry.class));

        when(golWorkflowHandlerMock.sign(any())).thenReturn(Boolean.TRUE);

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

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(userRepositoryMock.findOne(internalUserResource.getId())).thenReturn(internalUser);
        when(golWorkflowHandlerMock.removeGrantOfferLetter(project, internalUser)).thenReturn(true);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(fileServiceMock.deleteFile(existingGOLFile.getId())).thenReturn(serviceSuccess(existingGOLFile));

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isSuccess());
        assertNull(project.getGrantOfferLetter());

        verify(golWorkflowHandlerMock).removeGrantOfferLetter(project, internalUser);
        verify(fileServiceMock).deleteFile(existingGOLFile.getId());
    }

    @Test
    public void testRemoveGrantOfferLetterFileEntryProjectLive() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(userRepositoryMock.findOne(internalUserResource.getId())).thenReturn(internalUser);
        when(golWorkflowHandlerMock.removeGrantOfferLetter(project, internalUser)).thenReturn(true);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);
        when(fileServiceMock.deleteFile(existingGOLFile.getId())).thenReturn(serviceSuccess(existingGOLFile));

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }


    @Test
    public void testRemoveGrantOfferLetterFileEntryButWorkflowRejected() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingGOLFile = newFileEntry().build();
        project.setGrantOfferLetter(existingGOLFile);

        when(userRepositoryMock.findOne(internalUserResource.getId())).thenReturn(internalUser);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(golWorkflowHandlerMock.removeGrantOfferLetter(project, internalUser)).thenReturn(false);

        ServiceResult<Void> result = service.removeGrantOfferLetterFileEntry(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GRANT_OFFER_LETTER_CANNOT_BE_REMOVED));
        assertEquals(existingGOLFile, project.getGrantOfferLetter());

        verify(golWorkflowHandlerMock).removeGrantOfferLetter(project, internalUser);
        verify(fileServiceMock, never()).deleteFile(existingGOLFile.getId());
    }

    @Test
    public void testRemoveSignedGrantOfferLetterFileEntry() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingSignedGOLFile = newFileEntry().build();
        project.setSignedGrantOfferLetter(existingSignedGOLFile);

        when(userRepositoryMock.findOne(internalUserResource.getId())).thenReturn(internalUser);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.SETUP);
        when(fileServiceMock.deleteFile(existingSignedGOLFile.getId())).thenReturn(serviceSuccess(existingSignedGOLFile));

        ServiceResult<Void> result = service.removeSignedGrantOfferLetterFileEntry(123L);

        assertTrue(result.isSuccess());
        assertNull(project.getSignedGrantOfferLetter());

        verify(fileServiceMock).deleteFile(existingSignedGOLFile.getId());
    }

    @Test
    public void testRemoveSignedGrantOfferLetterFileEntryProjectLive() {

        UserResource internalUserResource = newUserResource().build();
        User internalUser = newUser().withId(internalUserResource.getId()).build();
        setLoggedInUser(internalUserResource);

        FileEntry existingSignedGOLFile = newFileEntry().build();
        project.setSignedGrantOfferLetter(existingSignedGOLFile);

        when(userRepositoryMock.findOne(internalUserResource.getId())).thenReturn(internalUser);
        when(projectWorkflowHandlerMock.getState(project)).thenReturn(ProjectState.LIVE);
        when(fileServiceMock.deleteFile(existingSignedGOLFile.getId())).thenReturn(serviceSuccess(existingSignedGOLFile));

        ServiceResult<Void> result = service.removeSignedGrantOfferLetterFileEntry(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(PROJECT_SETUP_ALREADY_COMPLETE));
    }

    private final Organisation organisation(OrganisationTypeEnum type, String name) {
        return newOrganisation()
                .withOrganisationType(type)
                .withName(name)
                .build();
    }

    private void setupGolTemplate() {
        fileEntryResource = newFileEntryResource().
                withFilesizeBytes(1024).
                withMediaType("application/pdf").
                withName("grant_offer_letter").
                build();

        createdFile = newFileEntry().build();
        fileEntryPair = Pair.of(new File("blah"), createdFile);

        StringBuilder stringBuilder = new StringBuilder();
        htmlFile = stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<html dir=\"ltr\" lang=\"en\">\n")
                .append("<head>\n")
                .append("<meta charset=\"UTF-8\"></meta>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("<p>\n")
                .append("${LeadContact}<br/>\n")
                .append("</p>\n")
                .append("</body>\n")
                .append("</html>\n").toString();

        when(rendererMock.renderTemplate(eq("common/grantoffer/grant_offer_letter.html"), any(Map.class))).thenReturn(ServiceResult.serviceSuccess(htmlFile));
        when(fileServiceMock.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(ServiceResult.serviceSuccess(fileEntryPair));
        when(fileEntryMapperMock.mapToResource(createdFile)).thenReturn(fileEntryResource);
    }

    private boolean checkGolTemplate() {
        boolean result = true;
        result &= fileEntryResource.getMediaType().equals(fileEntryResCaptor.getAllValues().get(0).getMediaType());
        result &= (fileEntryResource.getName() + ".pdf").equals(fileEntryResCaptor.getAllValues().get(0).getName());

        String startOfGeneratedFileString = null;
        try {
            int n = supplierCaptor.getAllValues().get(0).get().available();
            byte [] startOfGeneratedFile = new byte[n];
            supplierCaptor.getAllValues().get(0).get().read(startOfGeneratedFile, 0, n <9 ? n : 9);
            startOfGeneratedFileString = new String(startOfGeneratedFile, StandardCharsets.UTF_8);
        }
        catch(IOException e) {

        }
        String pdfHeader = "%PDF-1.4\n";
        result &= pdfHeader.equals(startOfGeneratedFileString.substring(0, pdfHeader.length()));
        return result;
    }
    @Test
    public void testGenerateGrantOfferLetterIfReadySuccess() {


        setupGolTemplate();

        Competition comp = newCompetition()
                .withName("Test Comp<")
                .build();
        Organisation o1 = organisation(BUSINESS, "OrgLeader&");
        Organisation o2 = organisation(BUSINESS, "Org2\"");
        Organisation o3 = organisation(BUSINESS, "Org3<");
        
        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT)
                .build();
        User u = newUser()
                .withFirstName("ab")
                .withLastName("cd")
                .build();
        ProcessRole leadAppProcessRole = newProcessRole()
                .withOrganisationId(o1.getId())
                .withUser(u)
                .withRole(leadAppRole)
                .build();
        Application app = newApplication()
                .withCompetition(comp)
                .withProcessRoles(leadAppProcessRole)
                .withId(3L)
                .build();
        ProjectUser pm = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withOrganisation(o1)
                .build();

        PartnerOrganisation po = newPartnerOrganisation()
                .withOrganisation(o1)
                .withLeadOrganisation(true)
                .build();

        PartnerOrganisation po2 = newPartnerOrganisation()
                .withOrganisation(o2)
                .withLeadOrganisation(false)
                .build();

        PartnerOrganisation po3 = newPartnerOrganisation()
                .withOrganisation(o3)
                .withLeadOrganisation(false)
                .build();

        Address address = newAddress()
                .withAddressLine1("InnovateUK>")
                .withAddressLine2("Northstar House\"")
                .withTown("Swindon&")
                .withPostcode("SN1 1AA'")
                .build();
        Project project = newProject()
                .withOtherDocumentsApproved(ApprovalType.APPROVED)
                .withName("project 1")
                .withApplication(app)
                .withPartnerOrganisations(asList(po3, po, po2))
                .withProjectUsers(asList(pm))
                .withDuration(10L)
                .withAddress(address)
                .withTargetStartDate(LocalDate.now())
                .build();

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withGrantClaimPercentage(30)
                .withApplication(456L)
                .withOrganisation(3L)
                .build();
        FinanceCheckSummaryResource financeCheckSummaryResource = newFinanceCheckSummaryResource()
                .withTotalPercentageGrant(BigDecimal.valueOf(25))
                .build();

        Map<String, Integer> organisationAndGrantPercentageMap = new HashMap<>();
        organisationAndGrantPercentageMap.put("OrgLeader&amp;", new Integer("30"));
        organisationAndGrantPercentageMap.put("Org2&quot;", new Integer("30"));
        organisationAndGrantPercentageMap.put("Org3&lt;", new Integer("30"));

        Map<String, List<String>> organisationYearsMap  = new HashMap<>();
        organisationYearsMap.put("OrgLeader&amp;", new LinkedList<>());
        organisationYearsMap.put("Org2&quot;", new LinkedList<>());
        organisationYearsMap.put("Org3&lt;", new LinkedList<>());

        Map<String, List<BigDecimal>> organisationEligibleCostTotal  = new HashMap<>();
        organisationEligibleCostTotal.put("OrgLeader&amp;", asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        organisationEligibleCostTotal.put("Org2&quot;", asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        organisationEligibleCostTotal.put("Org3&lt;", asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));

        Map<String, List<BigDecimal>> organisationGrantAllocationTotal  = new HashMap<>();
        organisationGrantAllocationTotal.put("OrgLeader&amp;", new LinkedList<>());
        organisationGrantAllocationTotal.put("Org2&quot;", new LinkedList<>());
        organisationGrantAllocationTotal.put("Org3&lt;", new LinkedList<>());

        Map<String, BigDecimal> yearEligibleCostTotal  = new HashMap<>();
        yearEligibleCostTotal.put("OrgLeader&amp;", new BigDecimal("1"));
        yearEligibleCostTotal.put("Org2&quot;", new BigDecimal("1"));
        yearEligibleCostTotal.put("Org3&lt;", new BigDecimal("1"));

        Map<String, BigDecimal> yearGrantAllocationTotal  = new HashMap<>();
        YearlyGOLProfileTable expectedYearlyGOLProfileTable = new YearlyGOLProfileTable(organisationAndGrantPercentageMap, organisationYearsMap, organisationEligibleCostTotal, organisationGrantAllocationTotal, yearEligibleCostTotal, yearGrantAllocationTotal);

        Map<String, Object> templateArgs = setupTemplateArguments(expectedYearlyGOLProfileTable);

        when(organisationRepositoryMock.findOne(o1.getId())).thenReturn(o1);
        when(organisationRepositoryMock.findOne(o2.getId())).thenReturn(o2);
        when(organisationRepositoryMock.findOne(o3.getId())).thenReturn(o3);
        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(spendProfileServiceMock.getSpendProfileTable(any(ProjectOrganisationCompositeId.class))).thenReturn(serviceSuccess(table));
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);

        when(financeCheckServiceMock.getFinanceCheckSummary(project.getId())).thenReturn(ServiceResult.serviceSuccess(financeCheckSummaryResource));

        when(financeUtilMock.isUsingJesFinances(BUSINESS.getOrganisationTypeId())).thenReturn(false);
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), o1.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), o2.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), o3.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));

        Map<String, BigDecimal> eligibleCostTotal = new HashMap<>();
        eligibleCostTotal.put(o1.getName(), new BigDecimal("1"));
        eligibleCostTotal.put(o2.getName(), new BigDecimal("1"));
        eligibleCostTotal.put(o3.getName(), new BigDecimal("1"));
        when(spendProfileTableCalculatorMock.createYearlyEligibleCostTotal(any(ProjectResource.class), any(Map.class), any(List.class))).thenReturn(eligibleCostTotal);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);

        verify(rendererMock).renderTemplate(templateCaptor.capture(), templateArgsCaptor.capture());
        verify(fileServiceMock).createFile(fileEntryResCaptor.capture(), supplierCaptor.capture());

        assertEquals(fileEntryResource.getMediaType(), fileEntryResCaptor.getAllValues().get(0).getMediaType());
        assertEquals(fileEntryResource.getName() + ".pdf", fileEntryResCaptor.getAllValues().get(0).getName());

        String startOfGeneratedFileString = null;
        try {
            int n = supplierCaptor.getAllValues().get(0).get().available();
            byte [] startOfGeneratedFile = new byte[n];
            supplierCaptor.getAllValues().get(0).get().read(startOfGeneratedFile, 0, n <9 ? n : 9);
            startOfGeneratedFileString = new String(startOfGeneratedFile, StandardCharsets.UTF_8);
        }
        catch(IOException e) {

        }
        String pdfHeader = "%PDF-1.4\n";
        assertEquals(pdfHeader, startOfGeneratedFileString.substring(0, pdfHeader.length()));
        assertTrue(result.isSuccess());
        assertTrue(compareTemplate(templateArgs, templateArgsCaptor.getAllValues().get(0)));
    }

    @Test
    public void testGenerateGrantOfferLetterFailureSpendProfilesNotApproved() {
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.REJECTED));

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGenerateGrantOfferLetterOtherDocsNotApproved() {

        Competition comp = newCompetition().withName("Test Comp").build();
        Organisation o1 = newOrganisation().withName("OrgLeader").build();
        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT).build();
        User u = newUser().withFirstName("ab").withLastName("cd").build();
        ProcessRole leadAppProcessRole = newProcessRole().withOrganisationId(o1.getId()).withUser(u).withRole(leadAppRole).build();
        Application app = newApplication().withCompetition(comp).withProcessRoles(leadAppProcessRole).withId(3L).build();
        ProjectUser pm = newProjectUser().withRole(PROJECT_MANAGER).withOrganisation(o1).build();
        PartnerOrganisation po = PartnerOrganisationBuilder.newPartnerOrganisation().withOrganisation(o1).withLeadOrganisation(true).build();
        Project project = newProject().withOtherDocumentsApproved(ApprovalType.REJECTED).withApplication(app).withPartnerOrganisations(asList(po)).withProjectUsers(asList(pm)).withDuration(10L).build();

        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGenerateGrantOfferLetterNoProject() {

        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(projectRepositoryMock.findOne(123L)).thenReturn(null);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUnfundedNonAcademicPartnerNotIncludedInGrantOfferLetter() {
        PartnerOrganisation partnerOrganisation = newPartnerOrganisation().withOrganisation(organisations.get(0)).build();
        PartnerOrganisation partnerOrganisation2 = newPartnerOrganisation().withOrganisation(organisations.get(1)).build();
        PartnerOrganisation partnerOrganisation3 = newPartnerOrganisation().withOrganisation(organisations.get(2)).build();
        PartnerOrganisation partnerOrganisation4 = newPartnerOrganisation().withOrganisation(nonAcademicUnfunded).build();

        List<PartnerOrganisation> partnerOrganisations = new ArrayList<>();
        partnerOrganisations.add(partnerOrganisation);
        partnerOrganisations.add(partnerOrganisation2);
        partnerOrganisations.add(partnerOrganisation3);
        partnerOrganisations.add(partnerOrganisation4);

        project = newProject().
                withId(projectId).
                withPartnerOrganisations(partnerOrganisations).
                withAddress(address).
                withApplication(application).
                withProjectUsers(singletonList(leadPartnerProjectUser)).
                build();

        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(financeUtilMock.isUsingJesFinances(BUSINESS.getOrganisationTypeId())).thenReturn(false);
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), nonAcademicUnfunded.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResourceZero));
        when(spendProfileServiceMock.getSpendProfileTable(new ProjectOrganisationCompositeId(projectId, nonAcademicUnfunded.getId())))
                .thenReturn(ServiceResult.serviceSuccess(tableZero));
        when(spendProfileTableCalculatorMock.calculateMonthlyTotals(tableZero.getMonthlyCostsPerCategoryMap(),
                tableZero.getMonths().size())).thenReturn(monthlyTotalsZero);

        Map<String, BigDecimal> eligibleCostTotal = new HashMap<>();
        eligibleCostTotal.put(organisations.get(0).getName(), new BigDecimal("1"));
        eligibleCostTotal.put(organisations.get(1).getName(), new BigDecimal("2"));
        eligibleCostTotal.put(organisations.get(2).getName(), new BigDecimal("3"));
        eligibleCostTotal.put(nonAcademicUnfunded.getName(), BigDecimal.ZERO);
        when(spendProfileTableCalculatorMock.createYearlyEligibleCostTotal(any(ProjectResource.class), any(Map.class), any(List.class))).thenReturn(eligibleCostTotal);

        when(spendProfileTableCalculatorMock.calculateEligibleCostPerYear(any(ProjectResource.class), any(List.class), any(List.class))).thenReturn(yearlyCosts);

        setupGolTemplate();

        Map<String, Integer> organisationAndGrantPercentageMap = new HashMap<>();
        organisationAndGrantPercentageMap.put("Org1&amp;", new Integer("100"));
        organisationAndGrantPercentageMap.put("Org2&quot;", new Integer("100"));
        organisationAndGrantPercentageMap.put("Org3&lt;", new Integer("100"));
        Map<String, List<String>> organisationYearsMap  = new HashMap<>();
        organisationYearsMap.put("Org1&amp;", new LinkedList<>());
        organisationYearsMap.put("Org2&quot;", new LinkedList<>());
        organisationYearsMap.put("Org3&lt;", new LinkedList<>());
        Map<String, List<BigDecimal>> organisationEligibleCostTotal  = new HashMap<>();
        organisationEligibleCostTotal.put("Org1&amp;", asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        organisationEligibleCostTotal.put("Org2&quot;", asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        organisationEligibleCostTotal.put("Org3&lt;", asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        Map<String, List<BigDecimal>> organisationGrantAllocationTotal  = new HashMap<>();
        organisationGrantAllocationTotal.put("Org1&amp;", new LinkedList<>());
        organisationGrantAllocationTotal.put("Org2&quot;", new LinkedList<>());
        organisationGrantAllocationTotal.put("Org3&lt;", new LinkedList<>());
        Map<String, BigDecimal> yearEligibleCostTotal  = new HashMap<>();
        yearEligibleCostTotal.put("Org1&amp;", new BigDecimal("1"));
        yearEligibleCostTotal.put("Org2&quot;", new BigDecimal("2"));
        yearEligibleCostTotal.put("Org3&lt;", new BigDecimal("3"));
        Map<String, BigDecimal> yearGrantAllocationTotal  = new HashMap<>();

        YearlyGOLProfileTable expectedYearlyGOLProfileTable = new YearlyGOLProfileTable(organisationAndGrantPercentageMap, organisationYearsMap, organisationEligibleCostTotal, organisationGrantAllocationTotal, yearEligibleCostTotal, yearGrantAllocationTotal);

        service.generateGrantOfferLetter(123L, fileEntryResource);

        verify(rendererMock).renderTemplate(templateCaptor.capture(), templateArgsCaptor.capture());

        verify(projectRepositoryMock).findOne(123L);

        assertTrue(compareYearlyGolProfileTable(expectedYearlyGOLProfileTable, (YearlyGOLProfileTable) templateArgsCaptor.getAllValues().get(0).get("TableData")));
    }

    @Test
    public void testGenerateGrantOfferLetterLeadPartnerNonAcademicWithNoFunding() {
        setupGolTemplate();

        Competition comp = newCompetition()
                .withName("Test Comp<")
                .build();
        Organisation o1 = organisation(BUSINESS, "OrgLeader&");
        Organisation o2 = organisation(BUSINESS, "Org2\"");
        Organisation o3 = organisation(BUSINESS, "Org3<");

        Role leadAppRole = newRole(UserRoleType.LEADAPPLICANT)
                .build();
        User u = newUser()
                .withFirstName("ab")
                .withLastName("cd")
                .build();
        ProcessRole leadAppProcessRole = newProcessRole()
                .withOrganisationId(o1.getId())
                .withUser(u)
                .withRole(leadAppRole)
                .build();
        Application app = newApplication()
                .withCompetition(comp)
                .withProcessRoles(leadAppProcessRole)
                .withId(3L)
                .build();
        ProjectUser pm = newProjectUser()
                .withRole(PROJECT_MANAGER)
                .withOrganisation(o1)
                .build();

        PartnerOrganisation po = newPartnerOrganisation()
                .withOrganisation(o1)
                .withLeadOrganisation(true)
                .build();

        PartnerOrganisation po2 = newPartnerOrganisation()
                .withOrganisation(o2)
                .withLeadOrganisation(false)
                .build();

        PartnerOrganisation po3 = newPartnerOrganisation()
                .withOrganisation(o3)
                .withLeadOrganisation(false)
                .build();

        Address address = newAddress()
                .withAddressLine1("InnovateUK>")
                .withAddressLine2("Northstar House\"")
                .withTown("Swindon&")
                .withPostcode("SN1 1AA'")
                .build();
        Project project = newProject()
                .withOtherDocumentsApproved(ApprovalType.APPROVED)
                .withName("project 1")
                .withApplication(app)
                .withPartnerOrganisations(asList(po3, po, po2))
                .withProjectUsers(asList(pm))
                .withDuration(10L)
                .withAddress(address)
                .withTargetStartDate(LocalDate.now())
                .build();

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withGrantClaimPercentage(30)
                .withApplication(456L)
                .withOrganisation(3L)
                .build();

        ApplicationFinanceResource applicationFinanceResourceZeroGrantClaim = newApplicationFinanceResource()
                .withGrantClaimPercentage(0)
                .withApplication(456L)
                .withOrganisation(3L)
                .build();
        FinanceCheckSummaryResource financeCheckSummaryResource = newFinanceCheckSummaryResource()
                .withTotalPercentageGrant(BigDecimal.valueOf(25))
                .build();

        Map<String, Integer> organisationAndGrantPercentageMap = new HashMap<>();
        organisationAndGrantPercentageMap.put(o1.getName(), new Integer("0"));
        organisationAndGrantPercentageMap.put(o2.getName(), new Integer("30"));
        organisationAndGrantPercentageMap.put(o3.getName(), new Integer("30"));

        Map<String, List<String>> organisationYearsMap  = new HashMap<>();
        organisationYearsMap.put(o1.getName(), new LinkedList<>());
        organisationYearsMap.put(o2.getName(), new LinkedList<>());
        organisationYearsMap.put(o3.getName(), new LinkedList<>());

        Map<String, List<BigDecimal>> organisationEligibleCostTotal  = new HashMap<>();
        organisationEligibleCostTotal.put(o1.getName(), asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        organisationEligibleCostTotal.put(o2.getName(), asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));
        organisationEligibleCostTotal.put(o3.getName(), asList(new BigDecimal("500"), new BigDecimal("100"), new BigDecimal("200")));

        Map<String, List<BigDecimal>> organisationGrantAllocationTotal  = new HashMap<>();
        organisationGrantAllocationTotal.put(o1.getName(), new LinkedList<>());
        organisationGrantAllocationTotal.put(o2.getName(), new LinkedList<>());
        organisationGrantAllocationTotal.put(o3.getName(), new LinkedList<>());

        Map<String, BigDecimal> yearEligibleCostTotal  = new HashMap<>();
        yearEligibleCostTotal.put(o1.getName(), new BigDecimal("1"));
        yearEligibleCostTotal.put(o2.getName(), new BigDecimal("1"));
        yearEligibleCostTotal.put(o3.getName(), new BigDecimal("1"));

        Map<String, BigDecimal> yearGrantAllocationTotal  = new HashMap<>();
        YearlyGOLProfileTable expectedYearlyGOLProfileTable = new YearlyGOLProfileTable(organisationAndGrantPercentageMap, organisationYearsMap, organisationEligibleCostTotal, organisationGrantAllocationTotal, yearEligibleCostTotal, yearGrantAllocationTotal);

        Map<String, Object> templateArgs = setupTemplateArguments(expectedYearlyGOLProfileTable);

        when(organisationRepositoryMock.findOne(o1.getId())).thenReturn(o1);
        when(organisationRepositoryMock.findOne(o2.getId())).thenReturn(o2);
        when(organisationRepositoryMock.findOne(o3.getId())).thenReturn(o3);
        when(spendProfileServiceMock.getSpendProfileStatusByProjectId(123L)).thenReturn(serviceSuccess(ApprovalType.APPROVED));
        when(spendProfileServiceMock.getSpendProfileTable(any(ProjectOrganisationCompositeId.class))).thenReturn(serviceSuccess(table));
        when(projectRepositoryMock.findOne(123L)).thenReturn(project);
        when(financeCheckServiceMock.getFinanceCheckSummary(project.getId())).thenReturn(ServiceResult.serviceSuccess(financeCheckSummaryResource));

        when(financeUtilMock.isUsingJesFinances(BUSINESS.getOrganisationTypeId())).thenReturn(false);
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), o1.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResourceZeroGrantClaim));
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), o2.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));
        when(financeRowServiceMock.financeDetails(project.getApplication().getId(), o3.getId())).thenReturn(ServiceResult.serviceSuccess(applicationFinanceResource));

        Map<String, BigDecimal> eligibleCostTotal = new HashMap<>();
        eligibleCostTotal.put(o1.getName(), new BigDecimal("1"));
        eligibleCostTotal.put(o2.getName(), new BigDecimal("1"));
        eligibleCostTotal.put(o3.getName(), new BigDecimal("1"));
        when(spendProfileTableCalculatorMock.createYearlyEligibleCostTotal(any(ProjectResource.class), any(Map.class), any(List.class))).thenReturn(eligibleCostTotal);

        ServiceResult<Void> result = service.generateGrantOfferLetterIfReady(123L);

        verify(rendererMock).renderTemplate(templateCaptor.capture(), templateArgsCaptor.capture());
        verify(fileServiceMock).createFile(fileEntryResCaptor.capture(), supplierCaptor.capture());

        assertTrue(checkGolTemplate());
        assertTrue(result.isSuccess());
        assertTrue(compareTemplate(templateArgs, templateArgsCaptor.getAllValues().get(0)));
    }

    @Override
    protected ProjectGrantOfferService supplyServiceUnderTest() {
        return new ProjectGrantOfferServiceImpl();
    }


    private Map<String, Object> setupTemplateArguments(YearlyGOLProfileTable yearlyGOLProfileTable) {

        Map<String, Object> templateArgs = new HashMap();
        templateArgs.put("SortedOrganisations", asList("OrgLeader&amp;", "Org2&quot;", "Org3&lt;"));
        templateArgs.put("ProjectLength", 10L);
        templateArgs.put("ProjectTitle", "project 1");
        templateArgs.put("LeadContact", "ab cd");
        templateArgs.put("ApplicationNumber", 3L);
        templateArgs.put("LeadOrgName", "OrgLeader&");
        templateArgs.put("CompetitionName", "Test Comp<");
        templateArgs.put("Address1", "InnovateUK>");
        templateArgs.put("Address2", "Northstar House\"");
        templateArgs.put("Address3", "");
        templateArgs.put("TownCity", "Swindon&");
        templateArgs.put("PostCode", "SN1 1AA'");
        templateArgs.put("ProjectStartDate", ZonedDateTime.now().format(DateTimeFormatter.ofPattern(GRANT_OFFER_LETTER_DATE_FORMAT)));
        templateArgs.put("Date", ZonedDateTime.now().toString());
        templateArgs.put("TableData", yearlyGOLProfileTable);
        return templateArgs;
    }

    private boolean compareTemplate(Map<String, Object> expectedTemplateArgs, Map<String, Object> templateArgs) {
        boolean result = true;
        result &= expectedTemplateArgs.get("SortedOrganisations").equals(templateArgs.get("SortedOrganisations"));
        result &= expectedTemplateArgs.get("ProjectLength").equals(templateArgs.get("ProjectLength"));
        result &= expectedTemplateArgs.get("ProjectTitle").equals(templateArgs.get("ProjectTitle"));
        result &= expectedTemplateArgs.get("ProjectStartDate").equals(templateArgs.get("ProjectStartDate"));
        result &= expectedTemplateArgs.get("LeadContact").equals(templateArgs.get("LeadContact"));
        result &= expectedTemplateArgs.get("ApplicationNumber").equals(templateArgs.get("ApplicationNumber"));
        result &= expectedTemplateArgs.get("LeadOrgName").equals(templateArgs.get("LeadOrgName"));
        result &= expectedTemplateArgs.get("CompetitionName").equals(templateArgs.get("CompetitionName"));
        result &= expectedTemplateArgs.get("Address1").equals(templateArgs.get("Address1"));
        result &= expectedTemplateArgs.get("Address2").equals(templateArgs.get("Address2"));
        result &= expectedTemplateArgs.get("Address3").equals(templateArgs.get("Address3"));
        result &= expectedTemplateArgs.get("TownCity").equals(templateArgs.get("TownCity"));
        result &= expectedTemplateArgs.get("PostCode").equals(templateArgs.get("PostCode"));
        result &= ZonedDateTime.parse((String) expectedTemplateArgs.get("Date")).isBefore(ZonedDateTime.parse((String)templateArgs.get("Date"))) || ZonedDateTime.parse((String)expectedTemplateArgs.get("Date")).isEqual(ZonedDateTime.parse((String)templateArgs.get("Date")));
        result &= compareYearlyGolProfileTable((YearlyGOLProfileTable) expectedTemplateArgs.get("TableData"), (YearlyGOLProfileTable) templateArgs.get("TableData"));
        return result;
    }

    private boolean compareYearlyGolProfileTable(YearlyGOLProfileTable a, YearlyGOLProfileTable b) {
        boolean result = true;
        if(a.getOrganisationAndGrantPercentageMap().entrySet().size() != b.getOrganisationAndGrantPercentageMap().entrySet().size()) {
            return false;
        }
        for(int i = 0; i < a.getOrganisationAndGrantPercentageMap().keySet().size(); i++) {
            Object k = a.getOrganisationAndGrantPercentageMap().keySet().toArray()[i];
            if(!b.getOrganisationAndGrantPercentageMap().containsKey(k) || !a.getOrganisationAndGrantPercentageMap().get(k).equals(b.getOrganisationAndGrantPercentageMap().get(k)))
            {
                result &= false;
            }
        }

        if(a.getOrganisationYearsMap().entrySet().size() != b.getOrganisationYearsMap().entrySet().size()) {
            return false;
        }
        for(int i = 0; i < a.getOrganisationYearsMap().keySet().size(); i++) {
            Object k = a.getOrganisationYearsMap().keySet().toArray()[i];
            if(!b.getOrganisationYearsMap().containsKey(k) || a.getOrganisationYearsMap().get(k).size() != b.getOrganisationYearsMap().get(k).size()) {
                result &= false;
            } else {
                for(int j = 0; j < a.getOrganisationYearsMap().get(k).size(); j++) {
                    if(!a.getOrganisationYearsMap().get(k).get(j).equals(b.getOrganisationYearsMap().get(k).get(j))) {
                        result &= false;
                    }
                }
            }
        }
        if(a.getOrganisationEligibleCostTotal().entrySet().size() != b.getOrganisationEligibleCostTotal().entrySet().size()) {
            return false;
        }
        for(int i = 0; i < a.getOrganisationEligibleCostTotal().keySet().size(); i++) {
            Object k = a.getOrganisationEligibleCostTotal().keySet().toArray()[i];
            if(!b.getOrganisationEligibleCostTotal().containsKey(k) || a.getOrganisationEligibleCostTotal().get(k).size() != b.getOrganisationEligibleCostTotal().get(k).size()) {
                result &= false;
            } else {
                for(int j = 0; j < a.getOrganisationEligibleCostTotal().get(k).size(); j++) {
                    if(!a.getOrganisationEligibleCostTotal().get(k).get(j).equals(b.getOrganisationEligibleCostTotal().get(k).get(j))) {
                        result &= false;
                    }
                }
            }
        }
        if(a.getOrganisationGrantAllocationTotal().entrySet().size() != b.getOrganisationGrantAllocationTotal().entrySet().size()) {
            return false;
        }
        for(int i = 0; i < a.getOrganisationGrantAllocationTotal().keySet().size(); i++) {
            Object k = a.getOrganisationGrantAllocationTotal().keySet().toArray()[i];
            if(!b.getOrganisationGrantAllocationTotal().containsKey(k) || a.getOrganisationGrantAllocationTotal().get(k).size() != b.getOrganisationGrantAllocationTotal().get(k).size()) {
                result &= false;
            } else {
                for(int j = 0; j < a.getOrganisationGrantAllocationTotal().get(k).size(); j++) {
                    if(!a.getOrganisationGrantAllocationTotal().get(k).get(j).equals(b.getOrganisationGrantAllocationTotal().get(k).get(j))) {
                        result &= false;
                    }
                }
            }
        }

        if(a.getYearEligibleCostTotal().entrySet().size() != b.getYearEligibleCostTotal().entrySet().size()) {
            return false;
        }
        for(int i = 0; i < a.getYearEligibleCostTotal().keySet().size(); i++) {
            Object k = a.getYearEligibleCostTotal().keySet().toArray()[i];
            if(!b.getYearEligibleCostTotal().containsKey(k) || !a.getYearEligibleCostTotal().get(k).equals(b.getYearEligibleCostTotal().get(k)))
            {
                result &= false;
            }
        }

        if(a.getYearGrantAllocationTotal().entrySet().size() != b.getYearGrantAllocationTotal().entrySet().size()) {
            return false;
        }
        for(int i = 0; i < a.getYearGrantAllocationTotal().keySet().size(); i++) {
            Object k = a.getYearGrantAllocationTotal().keySet().toArray()[i];
            if(!b.getYearGrantAllocationTotal().containsKey(k) || !a.getYearGrantAllocationTotal().get(k).equals(b.getYearGrantAllocationTotal().get(k)))
            {
                result &= false;
            }
        }
        result &= a.getEligibleCostGrandTotal() == b.getEligibleCostGrandTotal();
        return result;
    }
}
