package com.worth.ifs.project.bankdetails.transactional;

import com.worth.ifs.address.domain.AddressType;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.repository.AddressTypeRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.project.bankdetails.domain.BankDetails;
import com.worth.ifs.project.bankdetails.domain.VerificationCondition;
import com.worth.ifs.project.bankdetails.mapper.BankDetailsMapper;
import com.worth.ifs.project.bankdetails.mapper.SILBankDetailsMapper;
import com.worth.ifs.project.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.project.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import com.worth.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.mapper.OrganisationAddressMapper;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.users.ProjectUsersHelper;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowHandler;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.Address;
import com.worth.ifs.sil.experian.resource.Condition;
import com.worth.ifs.sil.experian.resource.SILBankDetails;
import com.worth.ifs.sil.experian.service.SilExperianEndpoint;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.worth.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.error.Error.globalError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.lang.Short.parseShort;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Service
public class BankDetailsServiceImpl implements BankDetailsService{

    private final int EXPERIAN_INVALID_ACC_NO_ERROR_ID = 4;
    private final int EXPERIAN_MODULUS_CHECK_FAILURE_ID = 7;

    @Autowired
    protected OrganisationRepository organisationRepository;

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

    @Autowired
    private ProjectUsersHelper projectUsersHelper;

    @Autowired
    private FinanceRowService financeRowService;

    @Autowired
    private ProjectDetailsWorkflowHandler projectDetailsWorkflowHandler;

    private SILBankDetailsMapper silBankDetailsMapper = new SILBankDetailsMapper();

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return find(bankDetailsRepository.findOne(id), notFoundError(BankDetails.class, id)).
                andOnSuccessReturn(bankDetailsMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> submitBankDetails(BankDetailsResource bankDetailsResource) {
        return bankDetailsDontExist(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation()).
                andOnSuccess(() ->
                        validateBankDetails(bankDetailsResource).
                                andOnSuccess(
                                        accountDetails -> saveSubmittedBankDetails(accountDetails, bankDetailsResource)).
                                andOnSuccess(accountDetails -> {
                                    BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
                                    return verifyBankDetails(accountDetails, bankDetails);
                                })
                );
    }

    @Override
    public ServiceResult<Void> updateBankDetails(BankDetailsResource bankDetailsResource) {
        Address address = toExperianAddressFormat(bankDetailsResource.getOrganisationAddress().getAddress());
        AccountDetails accountDetails = new AccountDetails(bankDetailsResource.getSortCode(), bankDetailsResource.getAccountNumber(), bankDetailsResource.getCompanyName(), bankDetailsResource.getRegistrationNumber(), address);
        return updateExistingBankDetails(accountDetails, bankDetailsResource).andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<ProjectBankDetailsStatusSummary> getProjectBankDetailsStatusSummary(Long projectId) {
        Project project = projectRepository.findOne(projectId);
        Organisation leadOrganisation = project.getApplication().getLeadOrganisation();
        List<BankDetailsStatusResource> bankDetailsStatusResources = new ArrayList<>();
        bankDetailsStatusResources.add(getBankDetailsStatusForOrg(project, leadOrganisation));
        List<Organisation> organisations = simpleFilter(projectUsersHelper.getPartnerOrganisations(projectId), org -> !org.getId().equals(leadOrganisation.getId()));
        bankDetailsStatusResources.addAll(simpleMap(organisations,org -> getBankDetailsStatusForOrg(project, org)));
        ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary = new ProjectBankDetailsStatusSummary(project.getApplication().getCompetition().getId(), project.getApplication().getCompetition().getName(), project.getId(), project.getApplication().getId(), bankDetailsStatusResources);
        return serviceSuccess(projectBankDetailsStatusSummary);
    }

    private BankDetailsStatusResource getBankDetailsStatusForOrg(Project project, Organisation org){

        return getByProjectAndOrganisation(project.getId(), org.getId()).handleSuccessOrFailure(
                failure -> new BankDetailsStatusResource(org.getId(), org.getName(), NOT_STARTED),
                success -> new BankDetailsStatusResource(org.getId(), org.getName(), success.isApproved() ? COMPLETE : PENDING));
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        return find(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(projectId, organisationId), HttpStatus.NOT_FOUND)).
                andOnSuccessReturn(bankDetails -> bankDetailsMapper.mapToResource(bankDetails));
    }

    private Address toExperianAddressFormat(AddressResource addressResource){
        return new Address(null, addressResource.getAddressLine1(), addressResource.getAddressLine2(), addressResource.getAddressLine3(), addressResource.getTown(), addressResource.getPostcode());
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
            updateAddressForExistingBankDetails(organisationAddressResource, addressResource, bankDetailsResource, bankDetails);
        }

        bankDetailsRepository.save(bankDetails);

        return serviceSuccess(accountDetails);
    }

    private void updateAddressForExistingBankDetails(OrganisationAddressResource organisationAddressResource, AddressResource addressResource, BankDetailsResource bankDetailsResource, BankDetails bankDetails){
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

    private ServiceResult<AccountDetails> updateExistingBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource) {
        BankDetails existingBankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
        if(existingBankDetails != null){
            bankDetailsResource.setId(existingBankDetails.getId());
        } else {
            return serviceFailure(CommonFailureKeys.BANK_DETAILS_CANNOT_BE_UPDATED_BEFORE_BEING_SUBMITTED);
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
                        return globalError(EXPERIAN_VALIDATION_FAILED_WITH_INCORRECT_ACC_NO);
                    } else if (condition.getCode().equals(EXPERIAN_MODULUS_CHECK_FAILURE_ID)) {
                        return globalError(EXPERIAN_VALIDATION_FAILED_WITH_INCORRECT_BANK_DETAILS);
                    }
                    return globalError(EXPERIAN_VALIDATION_FAILED, singletonList(condition.getDescription()));
                }).
                collect(Collectors.toList());
    }
}