package org.innovateuk.ifs.invite.transactional;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.QuestionStatus;
import org.innovateuk.ifs.application.repository.QuestionStatusRepository;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseEitherBackedResult;
import org.innovateuk.ifs.commons.service.FailingOrSucceedingResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.controller.CompetitionController;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputResponseRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Service
public class InviteServiceImpl extends BaseTransactionalService implements InviteService {

    private static final Log LOG = LogFactory.getLog(InviteServiceImpl.class);

    enum Notifications {
        INVITE_COLLABORATOR
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationInviteMapper applicationInviteMapper;

    @Autowired
    private InviteOrganisationMapper inviteOrganisationMapper;

    @Autowired
    private ApplicationInviteRepository applicationInviteRepository;

    @Autowired
    private QuestionStatusRepository questionStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private FormInputResponseRepository formInputResponseRepository;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    LocalValidatorFactoryBean validator;

    public InviteServiceImpl() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Override
    public List<ServiceResult<Void>> inviteCollaborators(String baseUrl, List<ApplicationInvite> invites) {
        return invites.stream().map(invite -> processCollaboratorInvite(baseUrl, invite)).collect(Collectors.toList());
    }

    private ServiceResult<Void> processCollaboratorInvite(String baseUrl, ApplicationInvite invite) {
        Errors errors = new BeanPropertyBindingResult(invite, invite.getClass().getName());
        validator.validate(invite, errors);

        if (errors.hasErrors()) {
            errors.getFieldErrors().stream().peek(e -> LOG.debug(format("Field error: %s ", e.getField())));
            return serviceFailure(internalServerErrorError()).andOnFailure(logInviteError(invite));
        } else {
            if (invite.getId() == null) {
                applicationInviteRepository.save(invite);
            }
            invite.setHash(generateInviteHash());
            applicationInviteRepository.save(invite);
            return inviteCollaboratorToApplication(baseUrl, invite).
                    andOnSuccessReturnVoid(() -> handleInviteSuccess(invite)).
                    andOnFailure(logInviteError(invite));
        }
    }

    private void handleInviteSuccess(ApplicationInvite invite) {
        applicationInviteRepository.save(invite.send(loggedInUserSupplier.get(), LocalDateTime.now()));
    }

    private Consumer<ServiceFailure> logInviteError(ApplicationInvite i) {
        return failure -> LOG.error(format("Invite failed %s , %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
    }

    private String getInviteUrl(String baseUrl, ApplicationInvite invite) {
        return format("%s/accept-invite/%s", baseUrl, invite.getHash());
    }

    private String getCompetitionDetailsUrl(String baseUrl, ApplicationInvite invite) {
        return baseUrl + "/competition/" + invite.getTarget().getCompetition().getId() + "/details";
    }

    @Override
    public ServiceResult<Void> inviteCollaboratorToApplication(String baseUrl, ApplicationInvite invite) {
        User loggedInUser = loggedInUserSupplier.get();
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new ExternalUserNotificationTarget(invite.getName(), invite.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        if (StringUtils.isNotEmpty(invite.getTarget().getName())) {
            notificationArguments.put("applicationName", invite.getTarget().getName());
        }
        notificationArguments.put("sentByName", loggedInUser.getName());
        notificationArguments.put("competitionName", invite.getTarget().getCompetition().getName());
        notificationArguments.put("competitionUrl", getCompetitionDetailsUrl(baseUrl, invite));
        notificationArguments.put("inviteUrl", getInviteUrl(baseUrl, invite));
        if (invite.getInviteOrganisation().getOrganisation() != null) {
            notificationArguments.put("inviteOrganisationName", invite.getInviteOrganisation().getOrganisation().getName());
        } else {
            notificationArguments.put("inviteOrganisationName", invite.getInviteOrganisation().getOrganisationName());
        }
        ProcessRole leadRole = invite.getTarget().getLeadApplicantProcessRole();
        Organisation organisation = organisationRepository.findOne(leadRole.getOrganisationId());
        notificationArguments.put("leadOrganisation", organisation.getName());
        notificationArguments.put("leadApplicant", invite.getTarget().getLeadApplicant().getName());

        if (invite.getTarget().getLeadApplicant().getTitle() != null) {
            notificationArguments.put("leadApplicantTitle", invite.getTarget().getLeadApplicant().getTitle());
        } else {
            notificationArguments.put("leadApplicantTitle", "");
        }
        notificationArguments.put("leadApplicantEmail", invite.getTarget().getLeadApplicant().getEmail());

        Notification notification = new Notification(from, singletonList(to), Notifications.INVITE_COLLABORATOR, notificationArguments);
        return notificationService.sendNotification(notification, EMAIL);
    }

    @Override
    public ServiceResult<ApplicationInvite> findOne(Long id) {
        return find(applicationInviteRepository.findOne(id), notFoundError(ApplicationInvite.class, id));
    }

    @Override
    public ServiceResult<InviteResultsResource> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource) {
        List<Error> errors = validateUniqueEmails(inviteOrganisationResource.getInviteResources());
        if (errors.size() > 0) {
            LOG.warn("Some double email addresses found");
            return serviceFailure(errors);
        }

        if (!inviteOrganisationResourceIsValid(inviteOrganisationResource)) {
            return serviceFailure(PROJECT_INVITE_INVALID);
        }

        return assembleInviteOrganisationFromResource(inviteOrganisationResource).andOnSuccessReturn(newInviteOrganisation -> {
            List<ApplicationInvite> newInvites = assembleInvitesFromInviteOrganisationResource(inviteOrganisationResource, newInviteOrganisation);
            inviteOrganisationRepository.save(newInviteOrganisation);
            Iterable<ApplicationInvite> savedInvites = applicationInviteRepository.save(newInvites);
            InviteResultsResource sentInvites = sendInvites(newArrayList(savedInvites));
            return sentInvites;
        });
    }

    @Override
    public ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {
        return getByHash(hash).andOnSuccessReturn(invite -> inviteOrganisationMapper.mapToResource(inviteOrganisationRepository.findOne(invite.getInviteOrganisation().getId())));
    }

    @Override
    public ServiceResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId) {
        return serviceSuccess(
                simpleMap(
                        inviteOrganisationRepository.findDistinctByInvitesApplicationId(applicationId),
                        inviteOrganisationMapper::mapToResource
                )
        );
    }

    @Override
    public ServiceResult<InviteResultsResource> saveInvites(List<ApplicationInviteResource> inviteResources) {
        List<Error> errors = validateUniqueEmails(inviteResources);
        if (errors.size() > 0) {
            LOG.warn("Some double email addresses found");
            return serviceFailure(errors);
        }

        List<ApplicationInvite> invites = simpleMap(inviteResources, invite -> mapInviteResourceToInvite(invite, null));
        applicationInviteRepository.save(invites);
        return serviceSuccess(sendInvites(invites));
    }

    @Override
    public ServiceResult<Void> acceptInvite(String inviteHash, Long userId) {
        return find(invite(inviteHash), user(userId)).andOnSuccess((invite, user) -> {
            if (invite.getEmail().equalsIgnoreCase(user.getEmail())) {
                invite.open();
                List<Organisation> usersOrganisations = organisationRepository.findByUsers(user);
                if (invite.getInviteOrganisation().getOrganisation() == null && !usersOrganisations.isEmpty()) {
                    invite.getInviteOrganisation().setOrganisation(usersOrganisations.get(0));
                }
                invite = applicationInviteRepository.save(invite);
                initializeInvitee(invite, user);

                return serviceSuccess();
            }
            LOG.error(format("Invited emailaddress not the same as the users emailaddress %s => %s ", user.getEmail(), invite.getEmail()));
            Error e = new Error("Invited emailaddress not the same as the users emailaddress", NOT_ACCEPTABLE);
            return serviceFailure(e);
        });
    }

    private void initializeInvitee(ApplicationInvite invite, User user) {
        Application application = invite.getTarget();
        Role role = roleRepository.findOneByName(COLLABORATOR.getName());
        Organisation organisation = invite.getInviteOrganisation().getOrganisation();
        ProcessRole processRole = new ProcessRole(user, application.getId(), role, organisation.getId());
        processRoleRepository.save(processRole);
    }

    private ApplicationInviteResource mapInviteToInviteResource(ApplicationInvite invite) {
        ApplicationInviteResource inviteResource = applicationInviteMapper.mapToResource(invite);
        Organisation organisation = organisationRepository.findOne(inviteResource.getLeadOrganisationId());
        inviteResource.setLeadOrganisation(organisation.getName());
        return inviteResource;
    }

    @Override
    public ServiceResult<ApplicationInviteResource> getInviteByHash(String hash) {
        return getByHash(hash).andOnSuccessReturn(this::mapInviteToInviteResource);
    }

    @Override
    public ServiceResult<Boolean> checkUserExistingByInviteHash(@P("hash") String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()))
                .andOnSuccess(u -> serviceSuccess(u.isPresent()));
    }

    @Override
    public ServiceResult<UserResource> getUserByInviteHash(@P("hash") String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()).map(userMapper::mapToResource))
                .andOnSuccess(u -> u.isPresent() ?
                        serviceSuccess(u.get()) :
                        serviceFailure(notFoundError(UserResource.class)));
    }

    @Override
    public ServiceResult<Void> removeApplicationInvite(Long applicationInviteId) {
        try {
            ApplicationInvite applicationInvite = applicationInviteMapper.mapIdToDomain(applicationInviteId);

            if (applicationInvite == null) {
                return serviceFailure(notFoundError(ApplicationInvite.class));
            }

            ProcessRole leadApplicantProcessRole = applicationInvite.getTarget().getLeadApplicantProcessRole();
            Long applicationId = applicationInvite.getTarget().getId();

            List<ProcessRole> processRoles = processRoleRepository.findByUserAndApplicationId(applicationInvite.getUser(), applicationInvite.getTarget().getId());

            setAssignedQuestionsToLeadApplicant(leadApplicantProcessRole, processRoles);
            setMarkedAsCompleteQuestionStatusesToLeadApplicant(leadApplicantProcessRole, applicationId, processRoles);
            setAssignedQuestionStatusesToLeadApplicant(leadApplicantProcessRole, applicationId, processRoles);

            removeProcessRolesOnApplication(processRoles);

            InviteOrganisation inviteOrganisation = applicationInvite.getInviteOrganisation();

            if (inviteOrganisation.getInvites().size() < 2) {
                inviteOrganisationRepository.delete(inviteOrganisation);
            } else {
                inviteOrganisation.getInvites().remove(applicationInvite);
                inviteOrganisationRepository.save(inviteOrganisation);
            }

            return serviceSuccess();
        } catch (IllegalArgumentException e) {
            return serviceFailure(notFoundError(ApplicationInvite.class));
        }
    }

    protected Supplier<ServiceResult<ApplicationInvite>> invite(final String hash) {
        return () -> getByHash(hash);
    }

    private ServiceResult<ApplicationInvite> getByHash(String hash) {
        return find(applicationInviteRepository.getByHash(hash), notFoundError(ApplicationInvite.class, hash));
    }

    private InviteResultsResource sendInvites(List<ApplicationInvite> invites) {
        List<ServiceResult<Void>> results = inviteCollaborators(webBaseUrl, invites);

        long failures = results.stream().filter(BaseEitherBackedResult::isFailure).count();
        long successes = results.stream().filter(BaseEitherBackedResult::isSuccess).count();
        LOG.info(format("Invite sending requests %s Success: %s Failures: %s", invites.size(), successes, failures));

        InviteResultsResource resource = new InviteResultsResource();
        resource.setInvitesSendFailure((int) failures);
        resource.setInvitesSendSuccess((int) successes);
        return resource;
    }

    private ServiceResult<InviteOrganisation> assembleInviteOrganisationFromResource(InviteOrganisationResource inviteOrganisationResource) {

        if (inviteOrganisationResource.getOrganisation() != null) {
            return find(organisation(inviteOrganisationResource.getOrganisation())).andOnSuccess(organisation -> {
                InviteOrganisation newInviteOrganisation = new InviteOrganisation(
                        inviteOrganisationResource.getOrganisationName(),
                        organisation,
                        null);
                return serviceSuccess(newInviteOrganisation);
            });
        } else {
            InviteOrganisation newInviteOrganisation = new InviteOrganisation(
                    inviteOrganisationResource.getOrganisationName(),
                    null,
                    null);
            return serviceSuccess(newInviteOrganisation);
        }
    }

    private List<ApplicationInvite> assembleInvitesFromInviteOrganisationResource(InviteOrganisationResource inviteOrganisationResource, InviteOrganisation newInviteOrganisation) {
        List<ApplicationInvite> invites = new ArrayList<>();
        inviteOrganisationResource.getInviteResources().forEach(inviteResource ->
                invites.add(mapInviteResourceToInvite(inviteResource, newInviteOrganisation))
        );

        return invites;
    }

    private ApplicationInvite mapInviteResourceToInvite(ApplicationInviteResource inviteResource, InviteOrganisation newInviteOrganisation) {
        Application application = applicationRepository.findOne(inviteResource.getApplication());
        if (newInviteOrganisation == null && inviteResource.getInviteOrganisation() != null) {
            newInviteOrganisation = inviteOrganisationRepository.findOne(inviteResource.getInviteOrganisation());
        }
        return new ApplicationInvite(inviteResource.getName(), inviteResource.getEmail(), application, newInviteOrganisation, null, InviteStatus.CREATED);
    }

    private boolean inviteOrganisationResourceIsValid(InviteOrganisationResource inviteOrganisationResource) {
        if (!inviteOrganisationResourceNameAndIdAreValid(inviteOrganisationResource)) {
            return false;
        }

        if (!allInviteResourcesAreValid(inviteOrganisationResource)) {
            return false;
        }

        return true;
    }

    private boolean inviteOrganisationResourceNameAndIdAreValid(InviteOrganisationResource inviteOrganisationResource) {
        if ((inviteOrganisationResource.getOrganisationName() == null ||
                inviteOrganisationResource.getOrganisationName().isEmpty())
                &&
                inviteOrganisationResource.getOrganisation() == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean allInviteResourcesAreValid(InviteOrganisationResource inviteOrganisationResource) {
        if (inviteOrganisationResource.getInviteResources()
                .stream()
                .filter(inviteResource -> !inviteResourceIsValid(inviteResource))
                .count() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean inviteResourceIsValid(ApplicationInviteResource inviteResource) {
        if (StringUtils.isEmpty(inviteResource.getEmail()) || StringUtils.isEmpty(inviteResource.getName()) || inviteResource.getApplication() == null) {
            return false;
        }

        return true;
    }

    private List<Error> validateUniqueEmails(List<ApplicationInviteResource> inviteResources) {
        List<Error> errors = new ArrayList<>();
        int inviteIndex = 0;

        List<ApplicationInviteResource> duplicatesInList = findDuplicatesInResourceList(inviteResources);

        for (ApplicationInviteResource invite : inviteResources) {
            if (duplicatesInList.contains(invite) || validateUniqueEmail(invite).equals(false)) {
                errors.add(fieldError("applicants[" + inviteIndex + "].email", invite.getEmail(), "email.already.in.invite"));
            }
            inviteIndex++;
        }

        return errors;
    }

    private Boolean validateUniqueEmail(ApplicationInviteResource inviteResource) {
        if (inviteResource.getEmail() == null) {
            return true;
        }

        Application application = applicationRepository.findOne(inviteResource.getApplication());

        Set<String> savedEmails = getSavedEmailAddresses(inviteResource.getApplication());
        if (application.getLeadApplicant() != null) {
            savedEmails.add(application.getLeadApplicant().getEmail());
        }

        return !savedEmails.contains(inviteResource.getEmail());
    }

    private Set<String> getSavedEmailAddresses(Long applicationId) {
        Set<String> savedEmails = new TreeSet<>();
        List<InviteOrganisationResource> savedInvites = newArrayList();
        savedInvites.addAll(getInvitesByApplication(applicationId).getSuccessObject());
        savedInvites.forEach(s -> {
            if (s.getInviteResources() != null) {
                s.getInviteResources().stream().forEach(i -> savedEmails.add(i.getEmail()));
            }
        });
        return savedEmails;
    }

    private List<ApplicationInviteResource> findDuplicatesInResourceList(List<ApplicationInviteResource> resourceList) {
        List<ApplicationInviteResource> result = new ArrayList();
        List<String> emails = resourceList.stream().map(inviteResource -> inviteResource.getEmail()).collect(Collectors.toList());
        Integer currentIndex = 0;

        for (String email : emails) {
            if (emails.subList(currentIndex + 1, emails.size()).contains(email)) {
                result.add(resourceList.get(currentIndex));
            }
            currentIndex++;
        }

        return result;

    }

    private void setMarkedAsCompleteQuestionStatusesToLeadApplicant(ProcessRole leadApplicantProcessRole, Long applicationId, List<ProcessRole> processRoles) {
        processRoles.forEach(processRole -> {
            List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationIdAndMarkedAsCompleteById(applicationId, processRole.getId());
            List<QuestionStatus> toDelete = new ArrayList<>();
            if (!questionStatuses.isEmpty()) {
                questionStatuses.forEach(questionStatus -> {
                    if (!questionStatus.getQuestion().getMultipleStatuses()) {
                        questionStatus.setMarkedAsCompleteBy(leadApplicantProcessRole);
                    } else {
                        setMarkedAsCompleteForQuestionWithMultipleQuestions(applicationId, processRole, questionStatus, toDelete);
                    }
                });
                questionStatuses.removeAll(toDelete);
                questionStatusRepository.save(questionStatuses);
            }
        });
    }

    private void setMarkedAsCompleteForQuestionWithMultipleQuestions(Long applicationId, ProcessRole roleToRemove, QuestionStatus questionStatus, List<QuestionStatus> statusesToDelete) {
        List<ProcessRole> rolesFromSameOrganisation = processRoleRepository.findByApplicationIdAndOrganisationId(applicationId, roleToRemove.getOrganisationId());
        rolesFromSameOrganisation.remove(roleToRemove);
        if (rolesFromSameOrganisation.isEmpty()) {
            questionStatusRepository.delete(questionStatus);
            statusesToDelete.add(questionStatus);
        } else {
            questionStatus.setMarkedAsCompleteBy(rolesFromSameOrganisation.get(0));
        }
    }

    private void setAssignedQuestionStatusesToLeadApplicant(ProcessRole leadApplicantProcessRole, Long applicationId, List<ProcessRole> processRoles) {
        processRoles.forEach(processRole -> {
            List<QuestionStatus> questionStatuses = questionStatusRepository.findByApplicationIdAndAssigneeIdOrAssignedById(applicationId, processRole.getId(), processRole.getId());
            if (!questionStatuses.isEmpty()) {
                questionStatuses.forEach(questionStatus ->
                        questionStatus.setAssignee(
                                leadApplicantProcessRole,
                                leadApplicantProcessRole,
                                LocalDateTime.now())
                );
                questionStatusRepository.save(questionStatuses);
            }
        });
    }

    private void setAssignedQuestionsToLeadApplicant(ProcessRole leadApplicantProcessRole, List<ProcessRole> processRoles) {
        processRoles.forEach(processRole -> {
            List<FormInputResponse> collaboratorQuestions = formInputResponseRepository.findByUpdatedById(processRole.getId());
            if (!collaboratorQuestions.isEmpty()) {
                collaboratorQuestions.forEach(collaboratorQuestion -> {
                    collaboratorQuestion.setUpdatedBy(leadApplicantProcessRole);
                    formInputResponseRepository.save(collaboratorQuestion);
                });
            }
        });
    }

    private void removeProcessRolesOnApplication(List<ProcessRole> processRoles) {
        if (!processRoles.isEmpty()) {
            processRoleRepository.delete(processRoles);
        }
    }
}
