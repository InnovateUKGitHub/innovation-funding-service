package com.worth.ifs.bankdetails.transactional;

import com.worth.ifs.address.repository.AddressRepository;
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
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.Condition;
import com.worth.ifs.sil.experian.service.SilExperianEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.lang.Short.parseShort;
import static java.util.Arrays.asList;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{
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

    @Autowired
    private SilExperianEndpoint silExperianEndpoint;

    private SILBankDetailsMapper silBankDetailsMapper = new SILBankDetailsMapper();

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return find(bankDetailsRepository.findOne(id), notFoundError(BankDetails.class, id)).
                andOnSuccessReturn(bankDetailsMapper::mapToResource);
    }

    private ServiceResult<Void> projectDetailsExist(final Long projectId){
        Project project = projectRepository.findOne(projectId);
        if(project.getSubmittedDate() == null){
            return serviceFailure(new Error(BANK_DETAILS_CANNOT_BE_SUBMITTED_BEFORE_PROJECT_DETAILS));
        }
        return serviceSuccess();
    }

    private ServiceResult<Void> bankDetailsDontExist(final Long projectId, final Long organisationId){
        BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if(bankDetails != null){
            return serviceFailure(new Error(BANK_DETAILS_CAN_ONLY_BE_SUBMITTED_ONCE));
        }
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
        return projectDetailsExist(bankDetailsResource.getProject()).
                andOnSuccess(() ->
                        bankDetailsDontExist(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation()).
                                andOnSuccess(() ->
                                        validateBankDetails(bankDetailsResource).
                                                andOnSuccess(
                                                        accountDetails -> saveBankDetails(accountDetails, bankDetailsResource)).
                                                andOnSuccess(accountDetails -> {
                                                    BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
                                                    return verifyBankDetails(accountDetails, bankDetails);
                                                })
                                )
                );
    }

    private ServiceResult<AccountDetails> saveBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource){
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

        return serviceSuccess(accountDetails);
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

    private ServiceResult<AccountDetails> validateBankDetails(BankDetailsResource bankDetailsResource){
            AccountDetails accountDetails = silBankDetailsMapper.toResource(bankDetailsResource);
            return silExperianEndpoint.validate(accountDetails).
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
        return conditons.stream().map(condition -> Error.globalError("", condition.getDescription())).collect(Collectors.toList());
    }
}