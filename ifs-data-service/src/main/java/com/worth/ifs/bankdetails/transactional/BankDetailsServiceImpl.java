package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.mapper.BankDetailsMapper;
import com.worth.ifs.bankdetails.mapper.SILBankDetailsMapper;
import com.worth.ifs.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.resource.experian.AccountDetails;
import com.worth.ifs.bankdetails.resource.experian.ValidationResult;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.AbstractRestTemplateAdaptor;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Arrays.asList;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{

    @Autowired
    @Qualifier("sil_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    @Value("${sil.rest.baseURL}")
    private String silRestServiceUrl;

    @Value("${sil.rest.experianValidate}")
    private String silExperianValidate;

    @Value("${sil.rest.experianVerify}")
    private String silExperianVerify;

    @Autowired
    private BankDetailsMapper bankDetailsMapper;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private SILBankDetailsMapper silBankDetailsMapper = new SILBankDetailsMapper();

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return find(bankDetailsRepository.findOne(id), notFoundError(BankDetails.class, id)).
                andOnSuccessReturn(bankDetailsMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
        return validateBankDetails(bankDetailsResource).andOnSuccess(
                () -> {
                    OrganisationAddressResource organisationAddressResource = bankDetailsResource.getOrganisationAddress();
                    AddressResource addressResource = organisationAddressResource.getAddress();
                    BankDetails bankDetails = bankDetailsMapper.mapToDomain(bankDetailsResource);

                    if (organisationAddressResource.getId() != null) {
                        OrganisationAddress organisationAddress = organisationAddressRepository.findOne(organisationAddressResource.getId());
                        bankDetails.setOrganisationAddress(organisationAddress);

                        if (addressResource.getId() != null) { // Existing address selected.
                            organisationAddress.setAddress(addressRepository.findOne(addressResource.getId()));
                        }
                    }

                    bankDetailsRepository.save(bankDetails);

                    return serviceSuccess();
                }
        );
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if(bankDetails != null) {
            return serviceSuccess(bankDetailsMapper.mapToResource(bankDetails));
        } else {
            return serviceFailure(new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(projectId, organisationId), HttpStatus.NOT_FOUND));
        }
    }

    private ServiceResult<BankDetailsResource> validateBankDetails(BankDetailsResource bankDetailsResource){
        Project project = projectRepository.findOne(bankDetailsResource.getProject());

        if(project.getSubmittedDate() == null){
            return serviceFailure(new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS));
        } else {
            AccountDetails accountDetails = silBankDetailsMapper.toResource(bankDetailsResource);
            return validateWithExperian(accountDetails).mapLeftOrRight(
                    failure -> serviceFailure(new Error(EXPERIAN_VALIDATION_FAILED)),
                    success -> serviceSuccess(bankDetailsResource)
            );
        }
    }

    private Either<ResponseEntity<ValidationResult>, ResponseEntity<Void>> validateWithExperian(AccountDetails accountDetails) {
        return adaptor.restPostWithEntity(silRestServiceUrl + silExperianValidate, accountDetails,
                        Void.class,
                        ValidationResult.class,
                        HttpStatus.ACCEPTED);

    }
}