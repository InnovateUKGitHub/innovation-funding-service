package org.innovateuk.ifs.invite.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.transactional.ApplicationProgressServiceImpl;
import org.innovateuk.ifs.commons.error.Error;
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
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_INVITE_INVALID;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
public class ApplicationInviteServiceImpl extends InviteService<ApplicationInvite> implements ApplicationInviteService {

    private String newEmailField = "applicants[%s].email";
    private String editEmailField = "stagedInvite.email";

    enum Notifications {
        INVITE_COLLABORATOR
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

    @Override
    @Transactional
    public ServiceResult<Void> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource, Optional<Long> applicationId) {
        String errorField = applicationId.isPresent() ? editEmailField  : newEmailField;
        return validateInviteOrganisationResource(inviteOrganisationResource).andOnSuccess(() ->
                validateUniqueEmails(inviteOrganisationResource.getInviteResources(), errorField)).andOnSuccess(() ->
                findOrAssembleInviteOrganisationFromResource(inviteOrganisationResource, applicationId).andOnSuccess(inviteOrganisation -> {
                    List<ApplicationInvite> invites = saveInviteOrganisationWithInvites(inviteOrganisation, inviteOrganisationResource.getInviteResources());
                    return applicationInviteNotificationService.inviteCollaborators(invites);
                }));
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
    public ServiceResult<Void> saveInvites(List<ApplicationInviteResource> inviteResources) {
        return validateUniqueEmails(inviteResources, editEmailField).andOnSuccess(() -> {
            List<ApplicationInvite> invites = simpleMap(inviteResources, invite -> mapInviteResourceToInvite(invite, null));
            applicationInviteRepository.save(invites);
            return applicationInviteNotificationService.inviteCollaborators(invites);
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
