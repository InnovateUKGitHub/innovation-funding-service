package org.innovateuk.ifs.application.transactional;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompletedPercentageResource;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationWorkflowHandler;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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
    private ApplicationWorkflowHandler applicationWorkflowHandler;

    @Autowired
    private ApplicationProgressService applicationProgressService;

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
                                                                                                          Long competitionId,
                                                                                                          Long userId) {
        return find(user(userId), competition(competitionId))
                .andOnSuccess((user, competition) ->
                        createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, user, competition));
    }

    private void generateProcessRolesForApplication(User user, Role role, Application application) {
        List<ProcessRole> usersProcessRoles = processRoleRepository.findByUser(user);
        List<Organisation> usersOrganisations = organisationRepository.findByUsers(user);
        Long userOrganisationId = !usersProcessRoles.isEmpty()
                ? usersProcessRoles.get(0).getOrganisationId()
                : usersOrganisations.get(0).getId();
        ProcessRole processRole = new ProcessRole(user, application.getId(), role, userOrganisationId);
        processRoleRepository.save(processRole);
        List<ProcessRole> processRoles = new ArrayList<>();
        processRoles.add(processRole);
        application.setProcessRoles(processRoles);
        applicationRepository.save(application);
    }

    private ServiceResult<ApplicationResource> createApplicationByApplicationNameForUserIdAndCompetitionId(String applicationName,
                                                                                                           User user,
                                                                                                           Competition competition) {
        Application application = new Application(applicationName);
        application.setStartDate(null);

        application.setCompetition(competition);
        setInnovationArea(application, competition);

        Application savedApplication = applicationRepository.save(application);
        generateProcessRolesForApplication(user, Role.LEADAPPLICANT, savedApplication);
        savedApplication = applicationRepository.findOne(savedApplication.getId());
        return serviceSuccess(applicationMapper.mapToResource(savedApplication));
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
    public ServiceResult<ApplicationResource> saveApplicationDetails(final Long id,
                                                                     ApplicationResource application) {
        return find(() -> getApplication(id)).andOnSuccess(
                foundApplication -> verifyApplicationIsOpen(foundApplication).andOnSuccessReturn(
                    openApplication -> {
                        openApplication.setName(application.getName());
                        openApplication.setDurationInMonths(application.getDurationInMonths());
                        openApplication.setStartDate(application.getStartDate());
                        openApplication.setStateAidAgreed(application.getStateAidAgreed());
                        openApplication.setResubmission(application.getResubmission());
                        openApplication.setPreviousApplicationNumber(application.getPreviousApplicationNumber());
                        openApplication.setPreviousApplicationTitle(application.getPreviousApplicationTitle());

                        Application savedApplication = applicationRepository.save(openApplication);
                        return applicationMapper.mapToResource(savedApplication);
                    }));
    }

    @Override
    @Transactional
    public ServiceResult<ApplicationResource> saveApplicationSubmitDateTime(final Long id,
                                                                            ZonedDateTime date) {
        return getOpenApplication(id).andOnSuccessReturn(existingApplication -> {
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
    public ServiceResult<ApplicationResource> updateApplicationState(final Long id,
                                                                     final ApplicationState state) {
        if (ApplicationState.SUBMITTED.equals(state) && !applicationProgressService.applicationReadyForSubmit(id)) {
            return serviceFailure(APPLICATION_NOT_READY_TO_BE_SUBMITTED);
        }

        return find(application(id)).andOnSuccess((application) -> {
            applicationWorkflowHandler.notifyFromApplicationState(application, state);
            applicationRepository.save(application);
            return serviceSuccess(applicationMapper.mapToResource(application));
        });
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
    @Transactional
    public ServiceResult<Void> withdrawApplication(long applicationId) {
        return find(application(applicationId))
                .andOnSuccess(application -> getCurrentlyLoggedInUser()
                        .andOnSuccess(user -> applicationWorkflowHandler.withdraw(application, user) ?
                                serviceSuccess() : serviceFailure(APPLICATION_MUST_BE_APPROVED)
                        )
                );
    }

    @Override
    public ServiceResult<Boolean> showApplicationTeam(Long applicationId,
                                                      Long userId) {
        return find(userRepository.findOne(userId), notFoundError(User.class, userId))
                .andOnSuccessReturn(User::isInternalUser);
    }

    @Override
    public ServiceResult<ZonedDateTime> findLatestEmailFundingDateByCompetitionId(Long id) {
        List<Application> applicationsForId = applicationRepository.findByCompetitionId(id);

        // Only competitions with at least one funded and informed application can be considered as in project setup
        return serviceSuccess(applicationsForId.stream()
                .filter(application -> application.getManageFundingEmailDate() != null)
                .max(Comparator.comparing(Application::getManageFundingEmailDate))
                .get().getManageFundingEmailDate());
    }

    @Override
    public ServiceResult<ApplicationResource> findByProcessRole(final Long id) {
        return getProcessRole(id).andOnSuccessReturn(processRole -> {
            Long appId = processRole.getApplicationId();
            Application application = applicationRepository.findOne(appId);
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
            List<Application> applications = simpleMap(applicationIds, appId -> appId != null ? applicationRepository.findOne(appId) : null);
            return simpleMap(applications, applicationMapper::mapToResource);
        });
    }

    @Override
    public ServiceResult<ApplicationPageResource> wildcardSearchById(String searchString, Pageable pageable) {

        Page<Application> pagedResult = applicationRepository.searchByIdLike(searchString, pageable);
        List<ApplicationResource> applicationResource = simpleMap(pagedResult.getContent(), application -> applicationMapper.mapToResource(application));
        return serviceSuccess(new ApplicationPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), applicationResource, pagedResult.getNumber(), pagedResult.getSize()));
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
    public ServiceResult<CompletedPercentageResource> getProgressPercentageByApplicationId(final Long applicationId) {
        return getApplicationById(applicationId).andOnSuccessReturn(applicationResource -> {
            CompletedPercentageResource resource = new CompletedPercentageResource();
            resource.setCompletedPercentage(applicationResource.getCompletion());
            return resource;
        });
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
    public ServiceResult<ApplicationResource> getApplicationById(final Long id) {
        return getApplication(id).andOnSuccessReturn(applicationMapper::mapToResource);
    }

    @Override
    public ServiceResult<ApplicationPageResource> findUnsuccessfulApplications(Long competitionId,
                                                                               int pageIndex,
                                                                               int pageSize,
                                                                               String sortField,
                                                                               String filter) {
        Sort sort = getApplicationSortField(sortField);
        Pageable pageable = new PageRequest(pageIndex, pageSize, sort);

        Collection<ApplicationState> applicationStates = getApplicationStatesFromFilter(filter);

        Page<Application> pagedResult = applicationRepository.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId, applicationStates, pageable);
        List<ApplicationResource> unsuccessfulApplications = simpleMap(pagedResult.getContent(), this::convertToApplicationResource);

        return serviceSuccess(new ApplicationPageResource(pagedResult.getTotalElements(), pagedResult.getTotalPages(), unsuccessfulApplications, pagedResult.getNumber(), pagedResult.getSize()));
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

            case "WITHDRAWN":
                applicationStates = Sets.immutableEnumSet(ApplicationState.WITHDRAWN);
                break;

            case "SUCCESSFUL":
                applicationStates = Sets.immutableEnumSet(ApplicationState.APPROVED);
                break;

            case "ALL":
            default:
                applicationStates = ApplicationState.unsuccessfulStates;
                break;
        }

        return applicationStates;

    }

    private Sort getApplicationSortField(String sortBy) {
        Sort result = APPLICATION_SORT_FIELD_MAP.get(sortBy);
        return result != null ? result : APPLICATION_SORT_FIELD_MAP.get("id");
    }

    private ApplicationResource convertToApplicationResource(Application application) {

        ApplicationResource applicationResource = applicationMapper.mapToResource(application);
        Organisation leadOrganisation = organisationRepository.findOne(application.getLeadOrganisationId());
        applicationResource.setLeadOrganisationName(leadOrganisation.getName());
        return applicationResource;
    }
}
