package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.project.bankdetails.builder.BankDetailsBuilder;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.mapper.SILBankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.resource.ValidationResult;
import org.innovateuk.ifs.sil.experian.resource.VerificationResult;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static org.innovateuk.ifs.project.bankdetails.builder.BankDetailsStatusResourceBuilder.newBankDetailsStatusResource;
import static org.innovateuk.ifs.project.bankdetails.builder.ProjectBankDetailsStatusSummaryBuilder.newProjectBankDetailsStatusSummary;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static org.innovateuk.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static freemarker.template.utility.Collections12.singletonList;
import static java.util.Arrays.asList;
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


    @Before
    public void setUp(){
        organisation = newOrganisation().build();
        project = newProject().build();
        AddressResource addressResource = newAddressResource().build();
        Address address = newAddress().build();
        OrganisationAddressResource organisationAddressResource = newOrganisationAddressResource().withAddress(addressResource).build();
        OrganisationAddress organisationAddress = newOrganisationAddress().build();
        bankDetailsResource = newBankDetailsResource().withProject(project.getId()).withSortCode("123123").withAccountNumber("12345678").withOrganisation(organisation.getId()).withOrganiationAddress(organisationAddressResource).build();
        bankDetails = BankDetailsBuilder.newBankDetails().withSortCode(bankDetailsResource.getSortCode()).withAccountNumber(bankDetailsResource.getAccountNumber()).withOrganisation(organisation).withOrganiationAddress(organisationAddress).build();
        accountDetails = silBankDetailsMapper.toAccountDetails(bankDetailsResource);
        silBankDetails = silBankDetailsMapper.toSILBankDetails(bankDetailsResource);

        when(bankDetailsMapperMock.mapToDomain(bankDetailsResource)).thenReturn(bankDetails);
        when(organisationAddressRepositoryMock.findOne(organisationAddressResource.getId())).thenReturn(organisationAddress);
        when(addressRepositoryMock.findOne(addressResource.getId())).thenReturn(address);
        when(bankDetailsRepositoryMock.save(bankDetails)).thenReturn(bankDetails);
        when(projectRepositoryMock.findOne(bankDetailsResource.getProject())).thenReturn(project);
    }

    @Test
    public void testGetBankDetailsByProjectAndOrganisation(){
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(bankDetails);
        when(bankDetailsMapperMock.mapToResource(bankDetails)).thenReturn(bankDetailsResource);
        ServiceResult<BankDetailsResource> result = service.getByProjectAndOrganisation(project.getId(), organisation.getId());
        assertTrue(result.isSuccess());
        assertEquals(result.getSuccessObject(), bankDetailsResource);
    }

    @Test
    public void testGetBankDetailsByProjectAndOrganisationButTheyDontExist(){
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(project.getId(), organisation.getId())).thenReturn(null);
        ServiceResult<BankDetailsResource> result = service.getByProjectAndOrganisation(project.getId(), organisation.getId());
        assertTrue(result.isFailure());
        Error expectedError = new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(project.getId(), organisation.getId()), NOT_FOUND);
        assertTrue(result.getFailure().is(expectedError));
    }

    @Test
    public void testSaveValidBankDetails(){
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(true);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        VerificationResult verificationResult = new VerificationResult();
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(null, bankDetails);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.submitBankDetails(bankDetailsResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testBankDetailsCanBeSubmittedBeforeProjectDetails(){
        ValidationResult validationResult = new ValidationResult();
        VerificationResult verificationResult = new VerificationResult();
        validationResult.setCheckPassed(true);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(false);
        when(bankDetailsMapperMock.mapToDomain(bankDetailsResource)).thenReturn(bankDetails);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(null, bankDetails);
        ServiceResult<Void> result = service.submitBankDetails(bankDetailsResource);
        assertTrue(result.isSuccess());
        verify(bankDetailsRepositoryMock, times(2)).save(bankDetails);
    }

    @Test
    public void testBankDetailsAreNotSavedIfExperianValidationFails(){
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(false);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        service.submitBankDetails(bankDetailsResource);
        verify(silExperianEndpointMock, never()).verify(accountDetails);
        verify(bankDetailsRepositoryMock, times(1)).findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
    }

    @Test
    public void testVerificationOccursOnceBankDetailsAreSaved(){
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(true);
        VerificationResult verificationResult = new VerificationResult();
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(null, bankDetails);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        service.submitBankDetails(bankDetailsResource);
        verify(silExperianEndpointMock, times(1)).verify(accountDetails);
        verify(bankDetailsRepositoryMock, times(2)).findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
    }

    @Test
    public void testUpdateOfBankDetailsWithExistingBankDetailsPresent(){
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(bankDetails);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testUpdateOfBankDetailsWithProjectDetailsNotSubmited(){
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(bankDetails);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(false);

        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        assertTrue(result.isSuccess());
        verify(bankDetailsRepositoryMock).save(bankDetails);
    }

    @Test
    public void testUpdateOfBankDetailsWithExistingBankDetailsNotPresent(){
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(null);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(BANK_DETAILS_CANNOT_BE_UPDATED_BEFORE_BEING_SUBMITTED));
    }

    @Test
    public void testUpdateOfBankDetailsWileApreadyApprovedNotAllowed(){
        bankDetailsResource.setManualApproval(true);
        bankDetails.setManualApproval(true);
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(bankDetails);
        when(projectDetailsWorkflowHandlerMock.isSubmitted(project)).thenReturn(true);

        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(BANK_DETAILS_HAVE_ALREADY_BEEN_APPROVED_AND_CANNOT_BE_UPDATED));
    }

    @Test
    public void testGetProjectBankDetailsStatusSummary(){
        Long projectId = 123L;
        Competition competition = newCompetition().withName("Greener Jet Engines").build();
        Application application = newApplication().withCompetition(competition).build();
        organisation.setOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build());
        ProcessRole leadApplicantRole = newProcessRole().withRole(UserRoleType.LEADAPPLICANT).withOrganisationId(organisation.getId()).withApplication(application).build();
        Project project = newProject().withId(projectId).withApplication(application).build();

        when(projectRepositoryMock.findOne(projectId)).thenReturn(project);
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(projectId, organisation.getId())).thenReturn(bankDetails);
        when(bankDetailsMapperMock.mapToResource(bankDetails)).thenReturn(bankDetailsResource);
        when(projectUsersHelperMock.getPartnerOrganisations(projectId)).thenReturn(singletonList(organisation));
        when(financeRowServiceMock.organisationSeeksFunding(project.getId(), project.getApplication().getId(), organisation.getId())).thenReturn(serviceSuccess(true));
        when(organisationRepositoryMock.findOne(leadApplicantRole.getOrganisationId())).thenReturn(organisation);

        List<BankDetailsStatusResource> bankDetailsStatusResource = newBankDetailsStatusResource().withOrganisationId(organisation.getId()).withOrganisationName(organisation.getName()).withBankDetailsStatus(ProjectActivityStates.ACTION_REQUIRED).build(1);

        ProjectBankDetailsStatusSummary expected = newProjectBankDetailsStatusSummary().build();
        expected.setProjectId(projectId);
        expected.setApplicationId(application.getId());
        expected.setCompetitionId(competition.getId());
        expected.setCompetitionName(competition.getName());
        expected.setBankDetailsStatusResources(bankDetailsStatusResource);
        ServiceResult<ProjectBankDetailsStatusSummary> result = service.getProjectBankDetailsStatusSummary(projectId);
        assertTrue(result.isSuccess());
        assertEquals(expected, result.getSuccessObject());
    }

    @Override
    protected BankDetailsService supplyServiceUnderTest() {
        return new BankDetailsServiceImpl();
    }
}
