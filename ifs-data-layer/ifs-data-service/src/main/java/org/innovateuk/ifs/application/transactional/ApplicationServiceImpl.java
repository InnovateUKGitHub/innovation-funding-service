package org.innovateuk.ifs.application.transactional;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationOrganisationAddress;
import org.innovateuk.ifs.application.domain.ApplicationProcess;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationOrganisationAddressRepository;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.PreviousApplicationResource;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.mapper.CompetitionMapper;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.domain.OrganisationAddress;
import org.innovateuk.ifs.organisation.repository.OrganisationAddressRepository;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

import static org.innovateuk.ifs.address.resource.OrganisationAddressType.INTERNATIONAL;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.state.ApplicationStateVerificationFunctions.verifyApplicationIsOpen;
import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * Transactional and secured service focused around the processing of Applications.
 */
@Service
public class ApplicationServiceImpl extends BaseTransactionalService implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private CompetitionMapper competitionMapper;

    @Autowired
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @Autowired
    private ApplicationValidationUtil applicationValidationUtil;

    @Autowired
    private ApplicationNotificationService applicationNotificationService;

    @Autowired
    private ApplicationOrganisationAddressRepository applicationOrganisationAddressRepository;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    private static final Map<String, Sort> APPLICATION_SORT_FIELD_MAP;

    static {
        Map<String, Sort> applicationSortFieldMap = new HashMap<>();
        applicationSortFieldMap.put("id", new Sort(ASC, "id"));
        applicationSortFieldMap.put("name", new Sort(ASC, "name", "id"));

        APPLICATION_SORT_FIELD_MAP = Collections.unmodifiableMap(applicationSortFieldMap);
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName,
                                                                                                          long competitionId,
                                                                                                          long userId,
                                                                                                          long organisationId
    ) {
        return find(user(userId), competition(competitionId))
                .andOnSuccess((user, competition) ->
                        createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, user, competition, organisationId));
    }

    private void generateProcessRolesForApplication(User user, Role role, Application application, long organisationId) {
        ProcessRole processRole = new ProcessRole(user, application.getId(), role, organisationId);
        processRoleRepository.save(processRole);
        List<ProcessRole> processRoles = new ArrayList<>();
        processRoles.add(processRole);
        application.setProcessRoles(processRoles);
    }

    private ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName,
                                                                                                           User user,
                                                                                                           Competition competition,
                                                                                                           long organisationId
    ) {
        Application application = new Application(applicationName);
        application.setStartDate(null);

        application.setCompetition(competition);
        setInnovationArea(application, competition);

        application = applicationRepository.save(application);
        generateProcessRolesForApplication(user, Role.LEADAPPLICANT, application, organisationId);

        linkAddressesToOrganisation(organisationId, application.getId());

        return serviceSuccess(applicationMapper.mapToResource(application));
    }

    // Default to the competition's innovation area if only one set.
    private void setInnovationArea(Application application, Competition competition) {
        if (competition.getInnovationAreas().size() == 1) {
            application.setInnovationArea(competition.getInnovationAreas().stream().findFirst().orElse(null));
            application.setNoInnovationAreaApplicable(false);
        }
    }

    @Override
    @Transactional
    public ServiceResult<ValidationMessages> saveApplicationDetails(final Long applicationId,
                                                                    ApplicationResource applicationResource) {
        return find(() -> getApplication(applicationId)).andOnSuccessReturn(foundApplication
                -> saveApplication(foundApplication, applicationResource));
    }

    private ValidationMessages saveApplication(Application application, ApplicationResource applicationResource) {
        return verifyApplicationIsOpen(application).andOnSuccessReturn(() -> {
                    saveApplicationDetails(application, applicationResource);
                    return validateApplication(application);
                }
        ).getSuccess();
    }

    private ValidationMessages validateApplication(Application application) {
        return applicationValidationUtil.isApplicationDetailsValid(application)
                .stream()
                .reduce(ValidationMessages.noErrors(), (vm1, vm2) -> {
                    vm1.addAll(vm2);
                    return vm1;
                });
    }

    private void saveApplicationDetails(Application application, ApplicationResource resource) {
        application.setName(resource.getName());
        application.setDurationInMonths(resource.getDurationInMonths());
        application.setStartDate(resource.getStartDate());
        application.setResubmission(resource.getResubmission());
        application.setPreviousApplicationNumber(resource.getPreviousApplicationNumber());
        application.setPreviousApplicationTitle(resource.getPreviousApplicationTitle());
        application.setCompetitionReferralSource(resource.getCompetitionReferralSource());
        application.setCompanyAge(resource.getCompanyAge());
        application.setCompanyPrimaryFocus(resource.getCompanyPrimaryFocus());
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(final Long applicationId,
                                                                            ZonedDateTime date) {
        return getOpenApplication(applicationId).andOnSuccessReturn(existingApplication -> {
            existingApplication.setSubmittedDate(date);
            Application savedApplication = applicationRepository.save(existingApplication);
            return applicationMapper.mapToResource(savedApplication);
        });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> setApplicationFundingEmailDateTime(final Long applicationId,
                                                                                 final ZonedDateTime fundingEmailDateTime) {
        return getApplication(applicationId).andOnSuccessReturn(application -> {
            application.setManageFundingEmailDate(fundingEmailDateTime);
            Application savedApplication = applicationRepository.save(application);
            return applicationMapper.mapToResource(savedApplication);
        });
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> updateApplicationState(long applicationId,
                                                                     ApplicationState state) {
        if (ApplicationState.SUBMITTED.equals(state) && !applicationProgressService.applicationReadyForSubmit(applicationId)) {
            return serviceFailure(APPLICATION_NOT_READY_TO_BE_SUBMITTED);
        }

        return find(application(applicationId)).andOnSuccess((application) -> {
            applicationWorkflowHandler.notifyFromApplicationState(application, state);
            applicationRepository.save(application);
            return serviceSuccess(applicationMapper.mapToResource(application));
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> reopenApplication(long applicationId) {
        return find(application(applicationId)).andOnSuccess((application) -> {
            return validateCompetitionIsOpen(application).andOnSuccess(() -> {
                validateFundingDecisionHasNotBeSent(application).andOnSuccess(() -> {
                    validateApplicationIsSubmitted(application).andOnSuccess(() -> {
                        applicationWorkflowHandler.notifyFromApplicationState(application, ApplicationState.OPENED);
                        application.setSubmittedDate(null);
                        applicationRepository.save(application);
                        return applicationNotificationService.sendNotificationApplicationReopened(application.getId());
                    });
                });
            });
        });
    }

    private ServiceResult<Void> validateCompetitionIsOpen(Application application) {
        return CompetitionStatus.OPEN.equals(application.getCompetition().getCompetitionStatus()) ? serviceSuccess() : serviceFailure(COMPETITION_NOT_OPEN);
    }

    private ServiceResult<Void> validateApplicationIsSubmitted(Application application) {
        return application.isSubmitted() ? serviceSuccess() : serviceFailure(APPLICATION_MUST_BE_SUBMITTED);
    }

    private ServiceResult<Void> validateFundingDecisionHasNotBeSent(Application application) {
        return application.getFundingDecision() == null ? serviceSuccess() : serviceFailure(APPLICATION_CANNOT_BE_REOPENED);
    }

    private static boolean applicationContainsUserRole(List<ProcessRole> roles,
                                                       final Long userId,
                                                       Role role) {
        boolean contains = false;
        int i = 0;
        while (!contains && i < roles.size()) {
            contains = roles.get(i).getUser().getId().equals(userId)
                    && roles.get(i).getRole() == role;
            i++;
        }

        return contains;
    }

    @Override
    @Transactional
    public ServiceResult<Void> markAsIneligible(long applicationId,
                                                IneligibleOutcome reason) {
        return find(application(applicationId)).andOnSuccess(application ->
                applicationWorkflowHandler.markIneligible(application, reason) ?
                        serviceSuccess() : serviceFailure(APPLICATION_MUST_BE_SUBMITTED)
        );
    }

    @Override
    public ServiceResult<Boolean> showApplicationTeam(Long applicationId,
                                                      Long userId) {
        return find(userRepository.findById(userId), notFoundError(User.class, userId))
                .andOnSuccessReturn(user -> user.isInternalUser() || user.hasRole(STAKEHOLDER));
    }

    @Override
    public ServiceResult<ZonedDateTime> findLatestEmailFundingDateByCompetitionId(Long id) {
        return find(applicationRepository.findTopByCompetitionIdOrderByManageFundingEmailDateDesc(id),
                notFoundError(Application.class, id)).andOnSuccessReturn(Application::getManageFundingEmailDate);
    }

    @Override
    public ServiceResult<ApplicationResource> findByProcessRole(final Long id) {
        return getProcessRole(id).andOnSuccessReturn(processRole -> {
            Long appId = processRole.getApplicationId();
            Application application = applicationRepository.findById(appId).orElse(null);
            return applicationMapper.mapToResource(application);
        });
    }

    @Override
    public ServiceResult<List<ApplicationResource>> findAll() {
        return serviceSuccess(simpleMap(applicationRepository.findAll(), applicationMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<ApplicationResource>> findByUserId(final Long userId) {
        return getUser(userId).andOnSuccessReturn(user -> {
            List<ProcessRole> roles = processRoleRepository.findByUser(user);
            Set<Long> applicationIds = simpleMapSet(roles, ProcessRole::getApplicationId);
            List<Application> applications = simpleMap(applicationIds, appId -> appId != null ? applicationRepository.findById(appId).orElse(null) : null);
            return simpleMap(applications, applicationMapper::mapToResource);
        });
    }

    @Override
    public ServiceResult<ApplicationPageResource> wildcardSearchById(String searchString, Pageable pageable) {
        return getCurrentlyLoggedInUser().andOnSuccess(user -> {
            if (user.hasRole(INNOVATION_LEAD)) {
                return handleApplicationSearchResultPage(applicationRepository.searchApplicationsByUserIdAndInnovationLeadRole(user.getId(), searchString, pageable));
            } else if (user.hasRole(STAKEHOLDER)) {
                return handleApplicationSearchResultPage(applicationRepository.searchApplicationsByUserIdAndStakeholderRole(user.getId(), searchString, pageable));
            } else {
                return handleApplicationSearchResultPage(applicationRepository.searchByIdLike(searchString, pageable));
            }
        });
    }

    @Override
    public ServiceResult<List<ApplicationResource>> getApplicationsByCompetitionIdAndUserId(final Long competitionId,
                                                                                            final Long userId,
                                                                                            final Role role) {
        List<Application> allApps = applicationRepository.findAll();
        List<Application> filtered = simpleFilter(allApps, app -> app.getCompetition().getId().equals(competitionId) &&
                applicationContainsUserRole(app.getProcessRoles(), userId, role));
        return serviceSuccess(simpleMap(filtered, applicationMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<Application>> getApplicationsByCompetitionIdAndState(Long competitionId,
                                                                                   Collection<ApplicationState> applicationStates) {
        List<Application> applicationResults =
                applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateIn(
                        competitionId,
                        applicationStates
                );
        return serviceSuccess(applicationResults);
    }

    @Override
    public ServiceResult<ApplicationResource> getApplicationById(Long applicationId) {
        return getApplication(applicationId).andOnSuccessReturn(applicationMapper::mapToResource);
    }

    @Override
    public ServiceResult<CompetitionResource> getCompetitionByApplicationId(long applicationId) {
        return find(application(applicationId)).andOnSuccessReturn(application ->
                competitionMapper.mapToResource(application.getCompetition()));
    }

    @Override
    public ServiceResult<Void> linkAddressesToOrganisation(long organisationId, long applicationId) {
        return find(application(applicationId), organisation(organisationId)).andOnSuccessReturnVoid((application, organisation) -> {
            if (organisation.isInternational()) {
                Optional<ApplicationOrganisationAddress> existingApplicationAddress = applicationOrganisationAddressRepository.findByApplicationIdAndOrganisationAddressOrganisationIdAndOrganisationAddressAddressTypeId(applicationId, organisationId, INTERNATIONAL.getId());
                if (existingApplicationAddress.isPresent()) {
                    // organisation already has an address on this application.
                    return;
                }
                Optional<OrganisationAddress> organisationAddress = organisationAddressRepository.findFirstByOrganisationIdAndAddressTypeIdOrderByModifiedOnDesc(organisation.getId(), INTERNATIONAL.getId());
                if (organisationAddress.isPresent()) {
                    OrganisationAddress organisationAddressToLinkToApplication;

                    //if the organisation has an unlinked international address then this will be their first application and this is the address created first.
                    if (organisationAddress.get().getApplicationAddresses().isEmpty()) {
                        organisationAddressToLinkToApplication = organisationAddress.get();
                    } else {
                        organisationAddressToLinkToApplication = organisationAddressRepository.save(copyNewOrganisationAddress(organisationAddress.get()));
                    }

                    ApplicationOrganisationAddress applicationOrganisationAddress = new ApplicationOrganisationAddress(organisationAddressToLinkToApplication, application);
                    applicationOrganisationAddressRepository.save(applicationOrganisationAddress);
                }
            }
        });
    }

    @Override
    public ServiceResult<ApplicationProcess> getApplicationProcess(long applicationId) {
        return find(application(applicationId)).andOnSuccessReturn(Application::getApplicationProcess);
    }

    private OrganisationAddress copyNewOrganisationAddress(OrganisationAddress organisationAddress) {
        return new OrganisationAddress(organisationAddress.getOrganisation(),
                new Address(organisationAddress.getAddress()),
                organisationAddress.getAddressType());
    }

    private ServiceResult<ApplicationPageResource> handleApplicationSearchResultPage(Page<Application> pagedResult) {
        List<ApplicationResource> applicationResource = simpleMap(pagedResult.getContent(), application -> applicationMapper.mapToResource(application));
        return serviceSuccess(new ApplicationPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), applicationResource, pagedResult.getNumber(), pagedResult.getSize()));
    }

    private Collection<ApplicationState> getApplicationStatesFromFilter(String filter) {

        Collection<ApplicationState> applicationStates;

        switch (filter.toUpperCase()) {
            case "INELIGIBLE":
                applicationStates = ApplicationState.ineligibleStates;
                break;

            case "REJECTED":
                applicationStates = Sets.immutableEnumSet(ApplicationState.REJECTED);
                break;

            case "SUCCESSFUL":
                applicationStates = Sets.immutableEnumSet(ApplicationState.APPROVED);
                break;

            case "ALL":
            default:
                applicationStates = ApplicationState.previousStates;
                break;
        }

        return applicationStates;

    }

    private Sort getApplicationSortField(String sortBy) {
        Sort result = APPLICATION_SORT_FIELD_MAP.get(sortBy);
        return result != null ? result : APPLICATION_SORT_FIELD_MAP.get("id");
    }

    private PreviousApplicationResource convertToPreviousApplicationResource(Application application) {

        ApplicationResource applicationResource = applicationMapper.mapToResource(application);
        Organisation leadOrganisation = organisationRepository.findById(application.getLeadOrganisationId()).get();

        return new PreviousApplicationResource(
                applicationResource.getId(),
                applicationResource.getName(),
                leadOrganisation.getName(),
                applicationResource.getApplicationState(),
                applicationResource.getCompetition()
        );

    }
}
