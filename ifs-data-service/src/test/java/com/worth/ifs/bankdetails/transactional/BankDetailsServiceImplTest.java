package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.mapper.SILBankDetailsMapper;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.SILBankDetails;
import com.worth.ifs.sil.experian.resource.ValidationResult;
import com.worth.ifs.sil.experian.resource.VerificationResult;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static com.worth.ifs.address.builder.AddressBuilder.newAddress;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.bankdetails.builder.BankDetailsBuilder.newBankDetails;
import static com.worth.ifs.bankdetails.builder.BankDetailsResourceBuilder.newBankDetailsResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS;
import static com.worth.ifs.commons.error.CommonFailureKeys.BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.organisation.builder.OrganisationAddressBuilder.newOrganisationAddress;
import static com.worth.ifs.organisation.builder.OrganisationAddressResourceBuilder.newOrganisationAddressResource;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
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
        bankDetails = newBankDetails().withSortCode(bankDetailsResource.getSortCode()).withAccountNumber(bankDetailsResource.getAccountNumber()).withOrganisation(organisation).withOrganiationAddress(organisationAddress).build();
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
        project.setSubmittedDate(LocalDateTime.now());
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(true);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        VerificationResult verificationResult = new VerificationResult();
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(null, bankDetails);
        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testBankDetailsCannotBeSubmittedBeforeProjectDetails(){
        project.setSubmittedDate(null);
        bankDetailsResource.setOrganisationAddress(null);
        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS));
    }

    @Test
    public void testBankDetailsAreNotSavedIfExperianValidationFails(){
        project.setSubmittedDate(LocalDateTime.now());
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(false);
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        verify(silExperianEndpointMock, never()).verify(accountDetails);
        verify(bankDetailsRepositoryMock, times(1)).findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
    }

    @Test
    public void testVerificationOccursOnceBankDetailsAreSaved(){
        project.setSubmittedDate(LocalDateTime.now());
        ValidationResult validationResult = new ValidationResult();
        validationResult.setCheckPassed(true);
        VerificationResult verificationResult = new VerificationResult();
        when(silExperianEndpointMock.validate(silBankDetails)).thenReturn(serviceSuccess(validationResult));
        when(silExperianEndpointMock.verify(accountDetails)).thenReturn(serviceSuccess(verificationResult));
        when(bankDetailsRepositoryMock.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation())).thenReturn(null, bankDetails);
        ServiceResult<Void> result = service.updateBankDetails(bankDetailsResource);
        verify(silExperianEndpointMock, times(1)).verify(accountDetails);
        verify(bankDetailsRepositoryMock, times(2)).findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
    }

    @Override
    protected BankDetailsService supplyServiceUnderTest() {
        return new BankDetailsServiceImpl();
    }
}
