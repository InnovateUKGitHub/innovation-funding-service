package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.transactional.ApplicationProgressServiceImpl;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.EmployeesAndTurnoverRepository;
import org.innovateuk.ifs.finance.repository.GrowthTableRepository;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.Invite;
import org.innovateuk.ifs.invite.domain.InviteHistory;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.mapper.ApplicationInviteMapper;
import org.innovateuk.ifs.invite.mapper.InviteOrganisationMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.InviteHistoryRepository;
import org.innovateuk.ifs.invite.repository.InviteOrganisationRepository;
import org.innovateuk.ifs.invite.repository.InviteRepository;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.procurement.milestone.repository.ApplicationProcurementMilestoneRepository;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.forEachWithIndex;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationInviteServiceImpl extends InviteService<ApplicationInvite> implements ApplicationInviteService {

    private static final String NEW_EMAIL_FIELD = "applicants[%s].email";
    private static final String EDIT_EMAIL_FIELD = "stagedInvite.email";

    enum Notifications {
        INVITE_COLLABORATOR,
        INVITE_KTA,
        REMOVE_KTA
    }

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
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Autowired
    private ApplicationProgressServiceImpl applicationProgressService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private QuestionReassignmentService questionReassignmentService;

    @Autowired
    private ApplicationInviteNotificationService applicationInviteNotificationService;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Autowired
    private GrowthTableRepository growthTableRepository;

    @Autowired
    private EmployeesAndTurnoverRepository employeesAndTurnoverRepository;

    @Autowired
    private ApplicationProcurementMilestoneRepository applicationProcurementMilestoneRepository;

    @Autowired
    private InviteHistoryRepository inviteHistoryRepository;


    @Override
    protected Class<ApplicationInvite> getInviteClass() {
        return ApplicationInvite.class;
    }

    @Override
    protected InviteRepository<ApplicationInvite> getInviteRepository() {
        return applicationInviteRepository;
    }

    @Override
    public ServiceResult<ApplicationInvite> findOneByHash(String hash) {
        return getByHash(hash);
    }


    @Autowired
    private ApplicationInviteService applicationInviteService;


    @Override
    @Transactional
    public ServiceResult<Void> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource, Optional<Long> applicationId) {
        String errorField = applicationId.isPresent() ? EDIT_EMAIL_FIELD : NEW_EMAIL_FIELD;
        return validateInviteOrganisationResource(inviteOrganisationResource).andOnSuccess(() ->
                validateUniqueEmails(inviteOrganisationResource.getInviteResources(), errorField)).andOnSuccess(() ->
                findOrAssembleInviteOrganisationFromResource(inviteOrganisationResource, applicationId).andOnSuccess(inviteOrganisation -> {
                    List<ApplicationInvite> invites = saveInviteOrganisationWithInvites(inviteOrganisation, inviteOrganisationResource.getInviteResources());
                    return applicationInviteNotificationService.inviteCollaborators(invites);
                }));
    }

    @Override
    public ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash) {

        ApplicationInviteResource applicationInviteResource = applicationInviteService.getInviteByHash(hash).toGetResponse().getSuccess();
        InviteHistory inviteHistory = getInviteHistory(applicationInviteResource,InviteStatus.OPENED);
        inviteHistoryRepository.save(inviteHistory);

        return getByHash(hash).andOnSuccessReturn(invite -> inviteOrganisationMapper.mapToResource(inviteOrganisationRepository.findById(invite.getInviteOrganisation().getId()).orElse(null)));
    }

    private InviteHistory getInviteHistory(ApplicationInviteResource applicationInviteResource, InviteStatus status) {
        Invite invite = mapInviteResourceToInvite(applicationInviteResource, null);

        InviteHistory inviteHistory = new InviteHistory();
        inviteHistory.setStatus(status);
        inviteHistory.setUpdatedOn(ZonedDateTime.now());
        inviteHistory.setUpdatedBy(null);
        inviteHistory.setId(RandomUtils.nextLong());
        inviteHistory.setInvite(invite);
        return inviteHistory;
    }

    private ApplicationInvite mapInviteResourceToInvite(ApplicationInviteResource inviteResource, InviteOrganisation newInviteOrganisation) {
        Application application = applicationRepository.findById(inviteResource.getApplication()).orElse(null);
        if (newInviteOrganisation == null && inviteResource.getInviteOrganisation() != null) {
            newInviteOrganisation = inviteOrganisationRepository.findById(inviteResource.getInviteOrganisation()).orElse(null);
        }
        return new ApplicationInvite(inviteResource.getId(), inviteResource.getName(), inviteResource.getEmail(), application, newInviteOrganisation, null, InviteStatus.CREATED);
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
    public ServiceResult<Void> saveInvites(List<ApplicationInviteResource> inviteResources) {
        return validateUniqueEmails(inviteResources, EDIT_EMAIL_FIELD).andOnSuccess(() -> {
            List<ApplicationInvite> invites = simpleMap(inviteResources, invite -> mapInviteResourceToInvite(invite, null));
            applicationInviteRepository.saveAll(invites);
            return applicationInviteNotificationService.inviteCollaborators(invites);
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> resendInvite(ApplicationInviteResource inviteResource) {
        ApplicationInvite invite = applicationInviteMapper.mapToDomain(inviteResource);
        invite.send(loggedInUserSupplier.get(), now());
        applicationInviteRepository.save(invite);
        return applicationInviteNotificationService.resendCollaboratorInvite(invite);
    }

    private ApplicationInviteResource mapInviteToInviteResource(ApplicationInvite invite) {
        ApplicationInviteResource inviteResource = applicationInviteMapper.mapToResource(invite);
        Organisation organisation = organisationRepository.findById(inviteResource.getLeadOrganisationId()).get();
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

    @Override
    public ServiceResult<Void> updateInviteHistory(ApplicationInviteResource inviteResource) {
        InviteHistory inviteHistory = getInviteHistory(inviteResource,InviteStatus.ACCEPTED);

        inviteHistoryRepository.save(inviteHistory);
        return serviceSuccess();
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

        processRoleRepository.deleteAll(collaboratorProcessRoles);
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
            applicationInviteRepository.delete(applicationInvite);
        }
    }

    private boolean isRemovingLastActiveCollaboratorUser(
            Application application,
            InviteOrganisation inviteOrganisation
    ) {
        if (inviteOrganisation.getOrganisation() == null) {
            return false;
        }

        return !application.getProcessRoles()
                .stream()
                .filter(p -> p.getOrganisationId() != null)
                .anyMatch(p -> p.getOrganisationId().equals(inviteOrganisation.getOrganisation().getId()));
    }

    private void deleteOrganisationFinanceData(Organisation organisation, Application application) {
        if (organisation != null) {
            Optional<ApplicationFinance> finance = applicationFinanceRepository.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId());
            if (finance.isPresent()) {
                if (finance.get().getGrowthTable() != null) {
                    growthTableRepository.delete(finance.get().getGrowthTable());
                }
                if (finance.get().getEmployeesAndTurnover() != null) {
                    employeesAndTurnoverRepository.delete(finance.get().getEmployeesAndTurnover());
                }
                applicationProcurementMilestoneRepository.deleteByApplicationFinanceId(finance.get().getId());
                applicationFinanceRepository.delete(finance.get());
            }
        }
        applicationProgressService.updateApplicationProgress(application.getId());
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
                organisation, null);
    }

    private List<ApplicationInvite> saveInviteOrganisationWithInvites(InviteOrganisation inviteOrganisation, List<ApplicationInviteResource> applicationInviteResources) {
        inviteOrganisationRepository.save(inviteOrganisation);
        return applicationInviteResources.stream().map(inviteResource ->
                applicationInviteRepository.save(mapInviteResourceToInvite(inviteResource, inviteOrganisation))).collect(toList());
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

    private ServiceResult<Void> validateUniqueEmails(List<ApplicationInviteResource> inviteResources, String errorField) {
        List<Error> failures = new ArrayList<>();
        long applicationId = inviteResources.get(0).getApplication();
        Set<String> uniqueEmails = getUniqueEmailAddressesForApplication(applicationId);
        forEachWithIndex(inviteResources, (index, invite) -> {
            if (!uniqueEmails.add(invite.getEmail())) {
                failures.add(fieldError(format(errorField, index), invite.getEmail(), "email.already.in.invite"));
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
        Application application = applicationRepository.findById(applicationId).get();
        return application.getLeadApplicant() != null ? application.getLeadApplicant().getEmail() : null;
    }

    private ServiceResult<Organisation> findOrganisationForInviteOrganisation(InviteOrganisationResource inviteOrganisationResource) {
        return find(organisationRepository.findById(inviteOrganisationResource.getOrganisation()), notFoundError(Organisation.class, inviteOrganisationResource.getOrganisation()));
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
