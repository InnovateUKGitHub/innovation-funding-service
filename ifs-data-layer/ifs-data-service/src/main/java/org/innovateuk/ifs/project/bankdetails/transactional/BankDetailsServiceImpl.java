package org.innovateuk.ifs.project.bankdetails.transactional;

import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.repository.AddressRepository;
import org.innovateuk.ifs.address.repository.AddressTypeRepository;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.BankDetailsReviewResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.project.bankdetails.domain.BankDetails;
import org.innovateuk.ifs.project.bankdetails.domain.VerificationCondition;
import org.innovateuk.ifs.project.bankdetails.mapper.BankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.mapper.SILBankDetailsMapper;
import org.innovateuk.ifs.project.bankdetails.repository.BankDetailsRepository;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsResource;
import org.innovateuk.ifs.project.bankdetails.resource.BankDetailsStatusResource;
import org.innovateuk.ifs.project.bankdetails.resource.ProjectBankDetailsStatusSummary;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.util.ProjectUsersHelper;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.Address;
import org.innovateuk.ifs.sil.experian.resource.Condition;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.service.SilExperianEndpoint;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.util.PrioritySorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Short.parseShort;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.error.Error.globalError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional(readOnly = true)
public class BankDetailsServiceImpl implements BankDetailsService {

    private final int EXPERIAN_INVALID_ACC_NO_ERROR_ID = 4;
    private final int EXPERIAN_MODULUS_CHECK_FAILURE_ID = 7;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    private BankDetailsMapper bankDetailsMapper;

    @Autowired
    private BankDetailsRepository bankDetailsRepository;

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
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private AddressMapper addressMapper;

    private SILBankDetailsMapper silBankDetailsMapper = new SILBankDetailsMapper();

    @Override
    public ServiceResult<BankDetailsResource> getById(Long id) {
        return find(bankDetailsRepository.findById(id), notFoundError(BankDetails.class, id)).
                andOnSuccessReturn(bankDetailsMapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> submitBankDetails(ProjectOrganisationCompositeId projectOrganisationCompositeId, BankDetailsResource bankDetailsResource) {
        return bankDetailsDontExist(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation()).
                andOnSuccess(() -> validateBankDetails(bankDetailsResource).
                andOnSuccess(accountDetails -> saveSubmittedBankDetails(accountDetails, bankDetailsResource)).
                andOnSuccess(accountDetails -> {
                    Optional<BankDetails> bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
                    return verifyBankDetails(accountDetails, bankDetails.get());
                }));
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateBankDetails(ProjectOrganisationCompositeId projectOrganisationCompositeId, BankDetailsResource bankDetailsResource) {
        Address address = toExperianAddressFormat(bankDetailsResource.getAddress());
        AccountDetails accountDetails = new AccountDetails(
                bankDetailsResource.getSortCode(),
                bankDetailsResource.getAccountNumber(),
                bankDetailsResource.getCompanyName(),
                bankDetailsResource.getRegistrationNumber(),
                address
        );
        return updateExistingBankDetails(accountDetails, bankDetailsResource).andOnSuccessReturnVoid();
    }

    @Override
    public ServiceResult<ProjectBankDetailsStatusSummary> getProjectBankDetailsStatusSummary(Long projectId) {
        Project project = projectRepository.findById(projectId).get();
        ProcessRole leadProcessRole = project.getApplication().getLeadApplicantProcessRole();
        Organisation leadOrganisation = organisationRepository.findById(leadProcessRole.getOrganisationId()).get();
        List<Organisation> sortedOrganisations = new PrioritySorting<>(projectUsersHelper.getPartnerOrganisations(projectId), leadOrganisation, Organisation::getName).unwrap();
        final List<BankDetailsStatusResource> bankDetailsStatusResources = simpleMap(sortedOrganisations, org -> getBankDetailsStatusForOrg(project, org));

        ProjectBankDetailsStatusSummary projectBankDetailsStatusSummary
                = new ProjectBankDetailsStatusSummary(project.getApplication().getCompetition().getId(),
                project.getApplication().getCompetition().getName(), project.getId(), project.getName(), project.getApplication().getId(),
                bankDetailsStatusResources, leadOrganisation.getName());
        return serviceSuccess(projectBankDetailsStatusSummary);
    }

    private BankDetailsStatusResource getBankDetailsStatusForOrg(Project project, Organisation org) {

        if (areBankDetailsRequired(project, org)) {
            return getByProjectAndOrganisation(project.getId(), org.getId()).handleSuccessOrFailure(
                    failure -> new BankDetailsStatusResource(org.getId(), org.getName(), NOT_STARTED),
                    success -> new BankDetailsStatusResource(org.getId(), org.getName(), success.isApproved() ? COMPLETE : ACTION_REQUIRED));
        }

        return new BankDetailsStatusResource(org.getId(), org.getName(), NOT_REQUIRED);
    }

    private boolean areBankDetailsRequired(Project project, Organisation org) {
        return !org.isInternational() && isOrganisationSeekingFunding(project.getId(), org.getId());
    }

    @Override
    public ServiceResult<BankDetailsResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        return find(bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId),
                new Error(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION, asList(projectId, organisationId), HttpStatus.NOT_FOUND)).
                andOnSuccessReturn(bankDetails -> bankDetailsMapper.mapToResource(bankDetails));
    }

    private boolean isOrganisationSeekingFunding(long projectId, long organisationId) {
        return projectFinanceService.financeChecksDetails(projectId, organisationId)
                .andOnSuccessReturn(ProjectFinanceResource::isRequestingFunding)
                .getOptionalSuccessObject()
                .orElse(false);
    }

    private Address toExperianAddressFormat(AddressResource addressResource) {
        return new Address(null, addressResource.getAddressLine1(), addressResource.getAddressLine2(), addressResource.getAddressLine3(), addressResource.getTown(), addressResource.getPostcode());
    }

    private ServiceResult<Void> bankDetailsDontExist(final Long projectId, final Long organisationId) {
        Optional<BankDetails> bankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(projectId, organisationId);
        if (bankDetails.isPresent()) {
            return serviceFailure(new Error(BANK_DETAILS_CAN_ONLY_BE_SUBMITTED_ONCE));
        }
        return serviceSuccess();
    }

    private ServiceResult<AccountDetails> saveSubmittedBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource) {
        BankDetails bankDetails = bankDetailsMapper.mapToDomain(bankDetailsResource);
        bankDetails.setAddress(addressRepository.save(addressMapper.mapToDomain(bankDetailsResource.getAddress())));
        bankDetailsRepository.save(bankDetails);

        return serviceSuccess(accountDetails);
    }

    private ServiceResult<AccountDetails> updateExistingBankDetails(AccountDetails accountDetails, BankDetailsResource bankDetailsResource) {
        Optional<BankDetails> existingBankDetails = bankDetailsRepository.findByProjectIdAndOrganisationId(bankDetailsResource.getProject(), bankDetailsResource.getOrganisation());
        if (existingBankDetails.isPresent()) {
            if (existingBankDetails.get().isManualApproval()) {
                return serviceFailure(CommonFailureKeys.BANK_DETAILS_HAVE_ALREADY_BEEN_APPROVED_AND_CANNOT_BE_UPDATED);
            }
            bankDetailsResource.setId(existingBankDetails.get().getId());
        } else {
            return serviceFailure(CommonFailureKeys.BANK_DETAILS_CANNOT_BE_UPDATED_BEFORE_BEING_SUBMITTED);
        }
        updateAddressForExistingBankDetails(bankDetailsResource, existingBankDetails.get());
        existingBankDetails.get().setAccountNumber(bankDetailsResource.getAccountNumber());
        existingBankDetails.get().setSortCode(bankDetailsResource.getSortCode());
        existingBankDetails.get().setManualApproval(bankDetailsResource.isManualApproval());
        return serviceSuccess(accountDetails);  
    }

    private void updateAddressForExistingBankDetails(BankDetailsResource bankDetailsResource, BankDetails bankDetails) {
        if (bankDetails.getAddress() != null) {
            bankDetails.getAddress().copyFrom(bankDetailsResource.getAddress());
        } else {
            bankDetails.setAddress(addressRepository.save(addressMapper.mapToDomain(bankDetailsResource.getAddress())));
        }
    }

    private ServiceResult<AccountDetails> validateBankDetails(BankDetailsResource bankDetailsResource) {
        AccountDetails accountDetails = silBankDetailsMapper.toAccountDetails(bankDetailsResource);
        SILBankDetails silBankDetails = silBankDetailsMapper.toSILBankDetails(bankDetailsResource);
        return silExperianEndpoint.validate(silBankDetails).
                handleSuccessOrFailure(
                        failure -> serviceFailure(failure.getErrors()),
                        validationResult -> {
                            if (validationResult
                                    .getConditions()
                                    .stream()
                                    .anyMatch(condition -> condition.getSeverity().equals("error"))) {
                                return serviceFailure(convertExperianValidationMsgToUserMsg(validationResult.getConditions()));
                            } else {
                                return serviceSuccess(accountDetails);
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

                    if (conditions != null && !conditions.isEmpty()) {
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

    @Override
    public ServiceResult<List<BankDetailsReviewResource>> getPendingBankDetailsApprovals() {

        List<BankDetailsReviewResource> pendingBankDetails = bankDetailsRepository.getPendingBankDetailsApprovalsForProjectStateNotIn(asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE));

        return serviceSuccess(pendingBankDetails);
    }

    @Override
    public ServiceResult<Long> countPendingBankDetailsApprovals() {

        Long countBankDetails = bankDetailsRepository.countPendingBankDetailsApprovalsForProjectStateNotIn(asList(WITHDRAWN, HANDLED_OFFLINE, COMPLETED_OFFLINE));

        return serviceSuccess(countBankDetails);
    }
}
