package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.HibernateValidator;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.transactional.ApplicationProgressServiceImpl;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.BaseEitherBackedResult;
import org.innovateuk.ifs.commons.service.ServiceFailure;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.resource.InviteResultsResource;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.domain.Invite.generateInviteHash;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationInviteServiceImpl extends InviteService<ApplicationInvite> implements ApplicationInviteService {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationInviteServiceImpl.class);

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
    private OrganisationRepository organisationRepository;

    @Autowired
    private InviteOrganisationRepository inviteOrganisationRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationProgressServiceImpl applicationProgressService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private QuestionReassignmentService questionReassignmentService;

    LocalValidatorFactoryBean validator;

    public ApplicationInviteServiceImpl() {
        validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();
    }

    @Override
    protected Class<ApplicationInvite> getInviteClass() {
        return ApplicationInvite.class;
    }

    @Override
    protected InviteRepository<ApplicationInvite> getInviteRepository() {
        return applicationInviteRepository;
    }

    @Override
    @Transactional
    public List<ServiceResult<Void>> inviteCollaborators(String baseUrl, List<ApplicationInvite> invites) {
        return invites.stream().map(invite -> processCollaboratorInvite(baseUrl, invite)).collect(toList());
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
        applicationInviteRepository.save(invite.send(loggedInUserSupplier.get(), ZonedDateTime.now()));
    }

    private Consumer<ServiceFailure> logInviteError(ApplicationInvite i) {
        return failure -> LOG.error(format("Invite failed %s , %s (error count: %s)", i.getId(), i.getEmail(), failure.getErrors().size()));
    }

    private String getInviteUrl(String baseUrl, ApplicationInvite invite) {
        return format("%s/accept-invite/%s", baseUrl, invite.getHash());
    }

    private String getCompetitionDetailsUrl(String baseUrl, ApplicationInvite invite) {
        return baseUrl + "/competition/" + invite.getTarget().getCompetition().getId() + "/overview";
    }

    @Override
    @Transactional
    public ServiceResult<Void> inviteCollaboratorToApplication(String baseUrl, ApplicationInvite invite) {
        User loggedInUser = loggedInUserSupplier.get();
        NotificationSource from = systemNotificationSource;
        NotificationTarget to = new UserNotificationTarget(invite.getName(), invite.getEmail());

        Map<String, Object> notificationArguments = new HashMap<>();
        if (StringUtils.isNotEmpty(invite.getTarget().getName())) {
            notificationArguments.put("applicationName", invite.getTarget().getName());
        }
        notificationArguments.put("sentByName", loggedInUser.getName());
        notificationArguments.put("applicationId", invite.getTarget().getId());
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

        Notification notification = new Notification(from, singletonList(to), Notifications.INVITE_COLLABORATOR, notificationArguments);
        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    @Override
    public ServiceResult<ApplicationInvite> findOneByHash(String hash) {
        return getByHash(hash);
    }

    @Override
    @Transactional
    public ServiceResult<InviteResultsResource> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource, Optional<Long> applicationId) {
        return validateInviteOrganisationResource(inviteOrganisationResource).andOnSuccess(() ->
                validateUniqueEmails(inviteOrganisationResource.getInviteResources())).andOnSuccess(() ->
                findOrAssembleInviteOrganisationFromResource(inviteOrganisationResource, applicationId).andOnSuccessReturn(inviteOrganisation -> {
                            List<ApplicationInvite> invites = saveInviteOrganisationWithInvites(inviteOrganisation, inviteOrganisationResource.getInviteResources());
                            return sendInvites(invites);
                        }
                ));
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
    @Transactional
    public ServiceResult<InviteResultsResource> saveInvites(List<ApplicationInviteResource> inviteResources) {
        return validateUniqueEmails(inviteResources).andOnSuccess(() -> {
            List<ApplicationInvite> invites = simpleMap(inviteResources, invite -> mapInviteResourceToInvite(invite, null));
            applicationInviteRepository.save(invites);
            return serviceSuccess(sendInvites(invites));
        });
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
    public ServiceResult<Boolean> checkUserExistsForInvite(String inviteHash) {
        return super.checkUserExistsForInvite(inviteHash);
    }

    @Override
    public ServiceResult<UserResource> getUserByInviteHash(String hash) {
        return getByHash(hash)
                .andOnSuccessReturn(i -> userRepository.findByEmail(i.getEmail()).map(userMapper::mapToResource))
                .andOnSuccess(u -> u.isPresent() ?
                        serviceSuccess(u.get()) :
                        serviceFailure(notFoundError(UserResource.class)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> removeApplicationInvite(long applicationInviteId) {
        return find(applicationInviteMapper.mapIdToDomain(applicationInviteId), notFoundError(ApplicationInvite.class))
                .andOnSuccessReturnVoid(this::removeApplicationInvite);
    }

    private void removeApplicationInvite(ApplicationInvite applicationInvite) {
        Application application = applicationInvite.getTarget();

        List<ProcessRole> collaboratorProcessRoles =
                processRoleRepository.findByUserAndApplicationId(applicationInvite.getUser(), application.getId());

        questionReassignmentService.reassignCollaboratorResponsesAndQuestionStatuses(
                application.getId(),
                collaboratorProcessRoles,
                application.getLeadApplicantProcessRole()
        );

        processRoleRepository.delete(collaboratorProcessRoles);
        application.removeProcessRoles(collaboratorProcessRoles);

        InviteOrganisation inviteOrganisation = applicationInvite.getInviteOrganisation();

        if (isRemovingLastActiveCollaboratorUser(application, inviteOrganisation)) {
            deleteOrganisationFinanceData(inviteOrganisation.getOrganisation(), application);
            inviteOrganisation.setOrganisation(null);
        }

        if (inviteOrganisation.isOnLastInvite()) {
            inviteOrganisationRepository.delete(inviteOrganisation);
        } else {
            inviteOrganisation.getInvites().remove(applicationInvite);
        }
    }

    private boolean isRemovingLastActiveCollaboratorUser(
            Application application,
            InviteOrganisation inviteOrganisation
    ) {
        if (inviteOrganisation.getOrganisation() == null) {
            return false;
        }

        return !simpleAnyMatch(
                application.getProcessRoles(),
                processRole -> processRole.getOrganisationId().equals(inviteOrganisation.getOrganisation().getId())
        );
    }

    private void deleteOrganisationFinanceData(Organisation organisation, Application application) {
        if (organisation != null) {
            ApplicationFinance finance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId());
            if (finance != null) {
                applicationFinanceRepository.delete(finance);
            }
        }
        applicationProgressService.updateApplicationProgress(application.getId());
    }

    private InviteResultsResource sendInvites(List<ApplicationInvite> invites) {
        List<ServiceResult<Void>> results = inviteCollaborators(webBaseUrl, invites);

        long failures = results.stream().filter(BaseEitherBackedResult::isFailure).count();
        long successes = results.stream().filter(BaseEitherBackedResult::isSuccess).count();
        LOG.debug(format("Invite sending requests %s Success: %s Failures: %s", invites.size(), successes, failures));

        InviteResultsResource resource = new InviteResultsResource();
        resource.setInvitesSendFailure((int) failures);
        resource.setInvitesSendSuccess((int) successes);
        return resource;
    }

    private ServiceResult<InviteOrganisation> findOrAssembleInviteOrganisationFromResource(InviteOrganisationResource inviteOrganisationResource, Optional<Long> applicationId) {

        if (inviteOrganisationResource.getOrganisation() != null && applicationId.isPresent()) {
            return eitherFindExistingInviteOrganisationOrCreateNewInviteOrganisationForOrganisation(inviteOrganisationResource, applicationId.get());
        } else {
            return serviceSuccess(buildNewInviteOrganisation(inviteOrganisationResource));
        }
    }

    private InviteOrganisation buildNewInviteOrganisation(InviteOrganisationResource inviteOrganisationResource) {
        return new InviteOrganisation(
                inviteOrganisationResource.getOrganisationName(),
                null,
                null);
    }

    private InviteOrganisation buildNewInviteOrganisationForOrganisation(InviteOrganisationResource inviteOrganisationResource, Organisation organisation) {
        return new InviteOrganisation(inviteOrganisationResource.getOrganisationName(),
                organisation,null);
    }

    private List<ApplicationInvite> saveInviteOrganisationWithInvites(InviteOrganisation inviteOrganisation, List<ApplicationInviteResource> applicationInviteResources) {
        inviteOrganisationRepository.save(inviteOrganisation);
        return applicationInviteResources.stream().map(inviteResource ->
                applicationInviteRepository.save(mapInviteResourceToInvite(inviteResource, inviteOrganisation))).collect(toList());
    }

    private ApplicationInvite mapInviteResourceToInvite(ApplicationInviteResource inviteResource, InviteOrganisation newInviteOrganisation) {
        Application application = applicationRepository.findOne(inviteResource.getApplication());
        if (newInviteOrganisation == null && inviteResource.getInviteOrganisation() != null) {
            newInviteOrganisation = inviteOrganisationRepository.findOne(inviteResource.getInviteOrganisation());
        }
        return new ApplicationInvite(inviteResource.getName(), inviteResource.getEmail(), application, newInviteOrganisation, null, InviteStatus.CREATED);
    }

    private ServiceResult<Void> validateInviteOrganisationResource(InviteOrganisationResource inviteOrganisationResource) {
        if (inviteOrganisationResource.getOrganisation() != null || StringUtils.isNotBlank(inviteOrganisationResource.getOrganisationName())
                && inviteOrganisationResource.getInviteResources().stream().allMatch(this::applicationInviteResourceIsValid)) {
            return serviceSuccess();
        }
        return serviceFailure(PROJECT_INVITE_INVALID);
    }

    private boolean applicationInviteResourceIsValid(ApplicationInviteResource inviteResource) {
        return inviteResource.getApplication() != null && StringUtils.isNotBlank(inviteResource.getEmail()) && StringUtils.isNotBlank(inviteResource.getName());
    }

    private ServiceResult<Void> validateUniqueEmails(List<ApplicationInviteResource> inviteResources) {
        List<Error> failures = new ArrayList<>();
        long applicationId = inviteResources.get(0).getApplication();
        Set<String> uniqueEmails = getUniqueEmailAddressesForApplication(applicationId);
        forEachWithIndex(inviteResources, (index, invite) -> {
            if (!uniqueEmails.add(invite.getEmail())) {
                failures.add(fieldError(format("stagedInvite.email", index), invite.getEmail(), "email.already.in.invite"));
            }
        });
        return failures.isEmpty() ? serviceSuccess() : serviceFailure(failures);
    }

    private Set<String> getUniqueEmailAddressesForApplication(long applicationId) {
        Set<String> result = getUniqueEmailAddressesForApplicationInvites(applicationId);
        String leadApplicantEmail = getLeadApplicantEmail(applicationId);
        if (leadApplicantEmail != null) {
            result.add(leadApplicantEmail);
        }
        return result;
    }

    private Set<String> getUniqueEmailAddressesForApplicationInvites(long applicationId) {
        List<InviteOrganisationResource> inviteOrganisationResources = getInvitesByApplication(applicationId).getSuccess();
        return inviteOrganisationResources.stream().flatMap(inviteOrganisationResource ->
                inviteOrganisationResource.getInviteResources().stream().map(ApplicationInviteResource::getEmail)).collect(Collectors.toSet());
    }

    private String getLeadApplicantEmail(long applicationId) {
        Application application = applicationRepository.findOne(applicationId);
        return application.getLeadApplicant() != null ? application.getLeadApplicant().getEmail() : null;
    }

    private ServiceResult<Organisation> findOrganisationForInviteOrganisation(InviteOrganisationResource inviteOrganisationResource) {
        return find(organisationRepository.findOne(inviteOrganisationResource.getOrganisation()), notFoundError(Organisation.class, inviteOrganisationResource.getOrganisation()));
    }

    private ServiceResult<InviteOrganisation> findInviteOrganisationForOrganisationInApplication(InviteOrganisationResource inviteOrganisationResource, Long applicationId) {
        return find(inviteOrganisationRepository.findOneByOrganisationIdAndInvitesApplicationId(inviteOrganisationResource.getOrganisation(), applicationId), notFoundError(InviteOrganisation.class, inviteOrganisationResource.getOrganisation(), applicationId));
    }

    private ServiceResult<InviteOrganisation> eitherFindExistingInviteOrganisationOrCreateNewInviteOrganisationForOrganisation(InviteOrganisationResource inviteOrganisationResource, Long applicationId) {
        return findOrganisationForInviteOrganisation(inviteOrganisationResource)
                .andOnSuccess(organisation -> findInviteOrganisationForOrganisationInApplication(inviteOrganisationResource, applicationId)
                        .andOnSuccess(inviteOrganisation -> serviceSuccess(inviteOrganisation))
                        .andOnFailure(() -> serviceSuccess(buildNewInviteOrganisationForOrganisation(inviteOrganisationResource, organisation)))
                );
    }
}
