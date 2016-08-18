package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.repository.AddressTypeRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.domain.VerificationCondition;
import com.worth.ifs.bankdetails.mapper.BankDetailsMapper;
import com.worth.ifs.bankdetails.mapper.SILBankDetailsMapper;
import com.worth.ifs.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.Condition;
import com.worth.ifs.sil.experian.resource.SILBankDetails;
import com.worth.ifs.sil.experian.service.SilExperianEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.lang.Short.parseShort;
import static java.util.Arrays.asList;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{

    private final int EXPERIAN_INVALID_ACC_NO_ERROR_ID = 4;
    private final int EXPERIAN_MODULUS_CHECK_FAILURE_ID = 7;

    @Autowired
    private BankDetailsMapper bankDetailsMapper;

    @Autowired
    private OrganisationAddressMapper organisationAddressMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressTypeRepository addressTypeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SilExperianEndpoint silExperianEndpoint;

    private SILBankDetailsMapper silBankDetailsMapper = new SILBankDetailsMapper();

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return find(bankDetailsRepository.findOne(id), notFoundError(BankDetails.class, id)).
                andOnSuccessReturn(bankDetailsMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> submitBankDetails(BankDetailsResource bankDetailsResource) {
        return projectDetailsExist(bankDetailsResource.getProject()).
                andOnSuccess(() ->
                        bankDetailsDontExist(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation()).
                                andOnSuccess(() ->
                                        validateBankDetails(bankDetailsResource).
                                                andOnSuccess(
                                                        accountDetails -> saveSubmittedBankDetails(accountDetails, bankDetailsResource)).
                                                andOnSuccess(accountDetails -> {
                                                    BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
                                                    return verifyBankDetails(accountDetails, bankDetails);
                                                })
                                )
                );
    }

    @Override
    public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
        return projectDetailsExist(bankDetailsResource.getProject()).
                andOnSuccess(() ->
                        validateBankDetails(bankDetailsResource).
                                andOnSuccess(
                                        accountDetails -> updateExistingBankDetails(accountDetails, bankDetailsResource).
                                                andOnSuccess(() -> serviceSuccess())
                                )
                );
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        return find(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(projectId, organisationId), HttpStatus.NOT_FOUND)).
                andOnSuccessReturn(bankDetails -> bankDetailsMapper.mapToResource(bankDetails));
    }

    private ServiceResult<Void> projectDetailsExist(final Long projectId){
        return find(projectRepository.findOne(projectId),
                new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS)).
                andOnSuccess(project -> {
                    if (project.getSubmittedDate() == null) {
                        return serviceFailure(new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS));
                    }
                    return serviceSuccess();
                });
    }

    private ServiceResult<Void> bankDetailsDontExist(final Long projectId, final Long organisationId){
        BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if(bankDetails != null){
            return serviceFailure(new Error(BANK_DETAILS_CAN_ONLY_BE_SUBMITTED_ONCE));
        }
        return serviceSuccess();
    }

    private ServiceResult<AccountDetails> saveSubmittedBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource){
        BankDetails bankDetails = bankDetailsMapper.mapToDomain(bankDetailsResource);
        OrganisationAddressResource organisationAddressResource = bankDetailsResource.getOrganisationAddress();
        AddressResource addressResource = organisationAddressResource.getAddress();

        if (organisationAddressResource.getId() != null) {
            OrganisationAddress organisationAddress = organisationAddressRepository.findOne(organisationAddressResource.getId());
            bankDetails.setOrganisationAddress(organisationAddress);
            if (addressResource.getId() != null) { // Existing address selected.
                organisationAddress.setAddress(addressRepository.findOne(addressResource.getId()));
            }
        } else {
            if(organisationAddressResource.getAddressType().getId().equals(BANK_DETAILS.getOrdinal())) {
                AddressType addressType = addressTypeRepository.findOne(BANK_DETAILS.getOrdinal());
                List<OrganisationAddress> bankOrganisationAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(bankDetailsResource.getOrganisation(), addressType);

                OrganisationAddress newOrganisationAddress;
                if(bankOrganisationAddresses != null && bankOrganisationAddresses.size() > 0) {
                    newOrganisationAddress = bankOrganisationAddresses.get(0);
                    long oldAddressId = newOrganisationAddress.getAddress().getId();
                    newOrganisationAddress.setAddress(addressMapper.mapToDomain(addressResource));
                    addressRepository.delete(oldAddressId);
                } else {
                    newOrganisationAddress = organisationAddressRepository.save(organisationAddressMapper.mapToDomain(organisationAddressResource));
                }
                bankDetails.setOrganisationAddress(newOrganisationAddress);
            }
        }

        bankDetailsRepository.save(bankDetails);

        return serviceSuccess(accountDetails);
    }

    private ServiceResult<AccountDetails> updateExistingBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource) {
        BankDetails existingBankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
        if(existingBankDetails != null){
            bankDetailsResource.setId(existingBankDetails.getId());
        }
        return saveSubmittedBankDetails(accountDetails, bankDetailsResource);
    }

    private ServiceResult<AccountDetails> validateBankDetails(BankDetailsResource bankDetailsResource){
        AccountDetails accountDetails = silBankDetailsMapper.toAccountDetails(bankDetailsResource);
        SILBankDetails silBankDetails = silBankDetailsMapper.toSILBankDetails(bankDetailsResource);
        return silExperianEndpoint.validate(silBankDetails).
                handleSuccessOrFailure(
                        failure -> serviceFailure(failure.getErrors()),
                        validationResult -> {
                            if(validationResult.isCheckPassed()) {
                                return serviceSuccess(accountDetails);
                            } else {
                                return serviceFailure(convertExperianValidationMsgToUserMsg(validationResult.getConditions()));
                            }
                        }
                );
    }

    private ServiceResult<Void> verifyBankDetails(final AccountDetails accountDetails, BankDetails bankDetails){
        silExperianEndpoint.verify(accountDetails).andOnSuccess(
                verificationResult -> {
                    if(verificationResult.getAddressScore() != null)
                        bankDetails.setAddressScore(parseShort(verificationResult.getAddressScore()));
                    if(verificationResult.getCompanyNameScore() != null)
                        bankDetails.setCompanyNameScore(parseShort(verificationResult.getCompanyNameScore()));
                    if(verificationResult.getRegNumberScore() != null) {
                        bankDetails.setRegistrationNumberMatched(verificationResult.getRegNumberScore().equals("Match"));
                    }

                    List<Condition> conditions = verificationResult.getConditions();

                    if(conditions != null && conditions.size() > 0){
                        bankDetails.setVerificationConditions(conditions.stream().map(silCondition -> {
                            VerificationCondition verificationCondition = new VerificationCondition();
                            verificationCondition.setCode(silCondition.getCode());
                            verificationCondition.setSeverity(silCondition.getSeverity());
                            verificationCondition.setDescription(silCondition.getDescription());
                            verificationCondition.setBankDetails(bankDetails);
                            return verificationCondition;
                        }).collect(Collectors.toList()));
                    }

                    bankDetails.setVerified(true);

                    bankDetailsRepository.save(bankDetails);

                    return serviceSuccess();
                }
        );
        return serviceSuccess();
    }

    private List<Error> convertExperianValidationMsgToUserMsg(List<Condition> conditons){
        return conditons.stream().filter(condition -> condition.getSeverity().equals("error")).
                map(condition -> {
                    if (condition.getCode().equals(EXPERIAN_INVALID_ACC_NO_ERROR_ID)) {
                        return Error.globalError(EXPERIAN_VALIDATION_FAILED_WITH_INCORRECT_ACC_NO.getErrorKey(), "Account number is incorrect, please check and try again");
                    } else if (condition.getCode().equals(EXPERIAN_MODULUS_CHECK_FAILURE_ID)) {
                        return Error.globalError(EXPERIAN_VALIDATION_FAILED_WITH_INCORRECT_BANK_DETAILS.getErrorKey(), "Bank account details are incorrect, please check and try again");
                    }
                    return Error.globalError(EXPERIAN_VALIDATION_FAILED.getErrorKey(), condition.getDescription());
                }).
                collect(Collectors.toList());
    }
}