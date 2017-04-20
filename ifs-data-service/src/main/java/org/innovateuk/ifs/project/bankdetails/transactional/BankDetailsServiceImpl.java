package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.address.domain.AddressType;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.transactional.FinanceRowService;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.mapper.OrganisationAddressMapper;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.util.PrioritySorting;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.domain.VerificationCondition;
import org.innovateuk.ifs.project.bankdetails.mapper.BankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.mapper.SILBankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.repository.ProjectRepository;
import org.innovateuk.ifs.project.users.ProjectUsersHelper;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.Address;
import org.innovateuk.ifs.sil.experian.resource.Condition;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Short.parseShort;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.BANK_DETAILS;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class BankDetailsServiceImpl implements BankDetailsService {

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
        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findOne(leadProcessRole.getOrganisationId());
        List<Organisation> sortedOrganisations = new PrioritySorting<>(projectUsersHelper.getPartnerOrganisations(projectId), leadOrganisation, Organisation::getName).unwrap();
        final List<BankDetailsStatusResource> bankDetailsStatusResources = simpleMap(sortedOrganisations, org -> getBankDetailsStatusForOrg(project, org));

        ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary
                = new ProjectBankDetailsStatusSummary(project.getApplication().getCompetition().getId(),
                project.getApplication().getCompetition().getName(), project.getId(), project.getApplication().getId(),
                bankDetailsStatusResources, leadOrganisation.getName());
        return serviceSuccess(projectBankDetailsStatusSummary);
    }

    private BankDetailsStatusResource getBankDetailsStatusForOrg(Project project, Organisation org) {

        if (!isOrganisationSeekingFunding(project.getId(), project.getApplication().getId(), org.getId())) {
            return new BankDetailsStatusResource(org.getId(), org.getName(), NOT_REQUIRED);
        }

        return getByProjectAndOrganisation(project.getId(), org.getId()).handleSuccessOrFailure(
                failure -> new BankDetailsStatusResource(org.getId(), org.getName(), NOT_STARTED),
                success -> new BankDetailsStatusResource(org.getId(), org.getName(), success.isApproved() ? COMPLETE : ACTION_REQUIRED));
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        return find(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(projectId, organisationId), HttpStatus.NOT_FOUND)).
                andOnSuccessReturn(bankDetails -> bankDetailsMapper.mapToResource(bankDetails));
    }

    private boolean isOrganisationSeekingFunding(Long projectId, Long applicationId, Long organisationId) {
        Optional<Boolean> result = financeRowService.organisationSeeksFunding(projectId, applicationId, organisationId).getOptionalSuccessObject();
        return result.map(Boolean::booleanValue).orElse(false);
    }

    private Address toExperianAddressFormat(AddressResource addressResource) {
        return new Address(null, addressResource.getAddressLine1(), addressResource.getAddressLine2(), addressResource.getAddressLine3(), addressResource.getTown(), addressResource.getPostcode());
    }

    private ServiceResult<Void> bankDetailsDontExist(final Long projectId, final Long organisationId) {
        BankDetails bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if (bankDetails != null) {
            return serviceFailure(new Error(BANK_DETAILS_CAN_ONLY_BE_SUBMITTED_ONCE));
        }
        return serviceSuccess();
    }

    private ServiceResult<AccountDetails> saveSubmittedBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource) {
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

    private void updateAddressForExistingBankDetails(OrganisationAddressResource organisationAddressResource, AddressResource addressResource, BankDetailsResource bankDetailsResource, BankDetails bankDetails) {
        if (organisationAddressResource.getAddressType().getId().equals(BANK_DETAILS.getOrdinal())) {
            AddressType addressType = addressTypeRepository.findOne(BANK_DETAILS.getOrdinal());
            List<OrganisationAddress> bankOrganisationAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(bankDetailsResource.getOrganisation(), addressType);

            OrganisationAddress newOrganisationAddress;
            if (bankOrganisationAddresses != null && bankOrganisationAddresses.size() > 0) {
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
        if (existingBankDetails != null) {
            if (existingBankDetails.isManualApproval()) {
                return serviceFailure(CommonFailureKeys.BANK_DETAILS_HAVE_ALREADY_BEEN_APPROVED_AND_CANNOT_BE_UPDATED);
            }
            bankDetailsResource.setId(existingBankDetails.getId());
        } else {
            return serviceFailure(CommonFailureKeys.BANK_DETAILS_CANNOT_BE_UPDATED_BEFORE_BEING_SUBMITTED);
        }
        return saveSubmittedBankDetails(accountDetails, bankDetailsResource);
    }

    private ServiceResult<AccountDetails> validateBankDetails(BankDetailsResource bankDetailsResource) {
        AccountDetails accountDetails = silBankDetailsMapper.toAccountDetails(bankDetailsResource);
        SILBankDetails silBankDetails = silBankDetailsMapper.toSILBankDetails(bankDetailsResource);
        return silExperianEndpoint.validate(silBankDetails).
                handleSuccessOrFailure(
                        failure -> serviceFailure(failure.getErrors()),
                        validationResult -> {
                            if (validationResult.isCheckPassed()) {
                                return serviceSuccess(accountDetails);
                            } else {
                                return serviceFailure(convertExperianValidationMsgToUserMsg(validationResult.getConditions()));
                            }
                        }
                );
    }

    private ServiceResult<Void> verifyBankDetails(final AccountDetails accountDetails, BankDetails bankDetails) {
        silExperianEndpoint.verify(accountDetails).andOnSuccess(
                verificationResult -> {
                    if (verificationResult.getAddressScore() != null)
                        bankDetails.setAddressScore(parseShort(verificationResult.getAddressScore()));
                    if (verificationResult.getCompanyNameScore() != null)
                        bankDetails.setCompanyNameScore(parseShort(verificationResult.getCompanyNameScore()));
                    if (verificationResult.getRegNumberScore() != null) {
                        bankDetails.setRegistrationNumberMatched(verificationResult.getRegNumberScore().equals("Match"));
                    }

                    List<Condition> conditions = verificationResult.getConditions();

                    if (conditions != null && conditions.size() > 0) {
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

    private List<Error> convertExperianValidationMsgToUserMsg(List<Condition> conditions) {
        return conditions.stream().filter(condition -> condition.getSeverity().equals("error")).
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
