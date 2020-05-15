package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.mapper.OrganisationApplicationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationApplicationAddressRepository;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.mapper.BankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.mapper.SILBankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.util.ProjectUsersHelper;
import org.innovateuk.ifs.project.projectdetails.workflow.configuration.ProjectDetailsWorkflowHandler;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.sil.experian.resource.*;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsStatusResourceBuilder.newBankDetailsStatusResource;
import static org.innovateuk.ifs.project.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;


public class BankDetailsServiceImplTest extends BaseServiceUnitTest<BankDetailsService> {
    private BankDetailsResource bankDetailsResource;
    private Project project;
    private Organisation organisation;
    private BankDetails bankDetails;
    private SILBankDetails silBankDetails;
    private AccountDetails accountDetails;
    private SILBankDetailsMapper silBankDetailsMapper = new SILBankDetailsMapper();

    @Mock
    private BankDetailsRepository bankDetailsRepositoryMock;

    @Mock
    private BankDetailsMapper bankDetailsMapperMock;

    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private SilExperianEndpoint silExperianEndpointMock;

    @Mock
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandlerMock;

    @Mock
    private ProjectUsersHelper projectUsersHelperMock;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private AddressTypeRepository addressTypeRepository;

    @Mock
    private OrganisationApplicationAddressMapper organisationApplicationAddressMapper;

    @Mock
    private OrganisationApplicationAddressRepository organisationApplicationAddressRepository;

    @Mock
    private AddressMapper addressMapper;

    @Before
    public void setUp() {
        organisation = newOrganisation().build();
        project = newProject().build();
        AddressResource addressResource = newAddressResource().build();
        Address address = newAddress().build();

        bankDetailsResource = newBankDetailsResource()
                .withProject(project.getId())
                .withSortCode("123123")
                .withAccountNumber("12345678")
                .withOrganisation(organisation.getId())
                .withAddress(addressResource)
                .build();

        bankDetails = BankDetailsBuilder.newBankDetails()
                .withSortCode(bankDetailsResource.getSortCode())
                .withAccountNumber(bankDetailsResource.getAccountNumber())
                .withOrganisation(organisation)
                .withAddress(address)
                .build();

        accountDetails = silBankDetailsMapper.toAccountDetails(bankDetailsResource);
        silBankDetails = silBankDetailsMapper.toSILBankDetails(bankDetailsResource);

        when(bankDetailsMapperMock.mapToDomain(bankDetailsResource)).thenReturn(bankDetails);
        when(bankDetailsRepositoryMock.save(bankDetails)).thenReturn(bankDetails);
        when(addressMapper.mapToDomain(addressResource)).thenReturn(address);
        when(addressRepositoryMock.save(address)).thenReturn(address);
        when(projectRepositoryMock.findById(bankDetailsResource.getProject())).thenReturn(Optional.of(project));
    }

    @Test
    public void getBankDetailsByProjectAndOrganisation() {
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(Optional.of(bankDetails));
        when(bankDetailsMapperMock.mapToResource(bankDetails)).thenReturn(bankDetailsResource);
        ServiceResult<BankDetailsResource> result = service.getByProjectAndOrganisation(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess(), bankDetailsResource);
    }

    @Test
    public void getBankDetailsByProjectAndOrganisationButTheyDontExist() {
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(Optional.empty());
        ServiceResult<BankDetailsResource> result = service.getByProjectAndOrganisation(project.getId(), organisation.getId());
        assertTrue(result.isFailure());
        Error expectedError = new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(project.getId(), organisation.getId()), NOT_FOUND);
        assertTrue(result.getFailure().is(expectedError));
    }

    @Test
    public void saveValidBankDetails() {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(true);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        VerificationResult verificationResult = new VerificationResult();
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.empty(), Optional.of(bankDetails));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.submitBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()),bankDetailsResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void bankDetailsCanBeSubmittedBeforeProjectDetails() {
        ValidationResult validationResult = new ValidationResult();
        VerificationResult verificationResult = new VerificationResult();
        validationResult.setCheckPassed(true);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(false);
        when(bankDetailsMapperMock.mapToDomain(bankDetailsResource)).thenReturn(bankDetails);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.empty(), Optional.of(bankDetails));
        ServiceResult<Void> result = service.submitBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()),bankDetailsResource);
        assertTrue(result.isSuccess());
        verify(bankDetailsRepositoryMock, times(2)).save(bankDetails);
    }

    @Test
    public void bankDetailsAreNotSavedIfExperianValidationFails() {
        ValidationResult validationResult = new ValidationResult();
        Condition condition = new Condition();
        condition.setSeverity("error");
        condition.setDescription("Invalid sort code");
        condition.setCode(5);
        validationResult.setConditions(singletonList(condition));
        validationResult.setCheckPassed(false);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        service.submitBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()), bankDetailsResource);
        verify(silExperianEndpointMock, never()).verify(accountDetails);
        verify(bankDetailsRepositoryMock, times(1)).findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
    }

    @Test
    public void testVerificationOccursOnceBankDetailsAreSaved() {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(true);
        VerificationResult verificationResult = new VerificationResult();
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.empty(), Optional.of(bankDetails));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        service.submitBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()), bankDetailsResource);
        verify(silExperianEndpointMock, times(1)).verify(accountDetails);
        verify(bankDetailsRepositoryMock, times(2)).findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
    }

    @Test
    public void updateOfBankDetailsWithExistingBankDetailsPresent() {
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.of(bankDetails));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.updateBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()),bankDetailsResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void updateOfBankDetailsWithProjectDetailsNotSubmited() {
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.of(bankDetails));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(false);

        ServiceResult<Void> result = service.updateBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()),bankDetailsResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void updateOfBankDetailsWithExistingBankDetailsNotPresent() {
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.empty());
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.updateBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()),bankDetailsResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(BANK_DETAILS_CANNOT_BE_UPDATED_BEFORE_BEING_SUBMITTED));
    }

    @Test
    public void updateOfBankDetailsWileApreadyApprovedNotAllowed() {
        bankDetailsResource.setManualApproval(true);
        bankDetails.setManualApproval(true);
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(Optional.of(bankDetails));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.updateBankDetails(new ProjectOrganisationCompositeId(project.getId(), organisation.getId()),bankDetailsResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(BANK_DETAILS_HAVE_ALREADY_BEEN_APPROVED_AND_CANNOT_BE_UPDATED));
    }

    @Test
    public void getProjectBankDetailsStatusSummary() {
        Long projectId = 123L;
        Competition competition = newCompetition().withName("Greener Jet Engines").build();
        Application application = newApplication().withCompetition(competition).build();
        organisation.setOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build());
        ProcessRole leadApplicantRole = newProcessRole().withRole(Role.LEADAPPLICANT).withOrganisationId(organisation.getId()).withApplication(application).build();
        Project project = newProject().withId(projectId).withApplication(application).build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.of(project));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisation.getId())).thenReturn(Optional.of(bankDetails));
        when(bankDetailsMapperMock.mapToResource(bankDetails)).thenReturn(bankDetailsResource);
        when(projectUsersHelperMock.getPartnerOrganisations(projectId)).thenReturn(singletonList(organisation));
        when(projectFinanceService.financeChecksDetails(projectId, organisation.getId())).thenReturn(serviceSuccess(newProjectFinanceResource().thatIsRequestingFunding().build()));
        when(organisationRepositoryMock.findById(leadApplicantRole.getOrganisationId())).thenReturn(Optional.of(organisation));

        List<BankDetailsStatusResource> bankDetailsStatusResource = newBankDetailsStatusResource()
                .withOrganisationId(organisation.getId())
                .withOrganisationName(organisation.getName())
                .withBankDetailsStatus(ProjectActivityStates.ACTION_REQUIRED)
                .build(1);

        ProjectBankDetailsStatusSummary expected = newProjectBankDetailsStatusSummary().build();
        expected.setProjectId(projectId);
        expected.setApplicationId(application.getId());
        expected.setCompetitionId(competition.getId());
        expected.setCompetitionName(competition.getName());
        expected.setBankDetailsStatusResources(bankDetailsStatusResource);
        ServiceResult<ProjectBankDetailsStatusSummary> result = service.getProjectBankDetailsStatusSummary(projectId);
        assertTrue(result.isSuccess());
        assertEquals(expected, result.getSuccess());
    }

    @Test
    public void getPendingBankDetailsApprovals() {

        List<BankDetailsReviewResource> pendingBankDetails = singletonList(new BankDetailsReviewResource(
                1L, 11L, "Comp1", 12L, "project1", 22L, "Org1"));

        when(bankDetailsRepositoryMock.getPendingBankDetailsApprovalsForProjectStateNotIn(asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE))).thenReturn(pendingBankDetails);

        ServiceResult<List<BankDetailsReviewResource>> result = service.getPendingBankDetailsApprovals();

        assertTrue(result.isSuccess());
        assertEquals(pendingBankDetails, result.getSuccess());
    }

    @Test
    public void countPendingBankDetailsApprovals() {

        Long pendingBankDetailsCount = 8L;

        when(bankDetailsRepositoryMock.countPendingBankDetailsApprovalsForProjectStateNotIn(asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE))).thenReturn(pendingBankDetailsCount);

        ServiceResult<Long> result = service.countPendingBankDetailsApprovals();

        assertTrue(result.isSuccess());
        assertEquals(pendingBankDetailsCount, result.getSuccess());
    }

    @Override
    protected BankDetailsService supplyServiceUnderTest() {
        return new BankDetailsServiceImpl();
    }
}
