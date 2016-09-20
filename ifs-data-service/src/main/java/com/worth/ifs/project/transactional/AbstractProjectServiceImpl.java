package com.worth.ifs.project.transactional;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.bankdetails.domain.BankDetails;
import com.worth.ifs.bankdetails.repository.BankDetailsRepository;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.finance.transactional.FinanceRowService;
import com.worth.ifs.invite.domain.ProjectParticipantRole;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.domain.MonitoringOfficer;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.finance.domain.SpendProfile;
import com.worth.ifs.project.finance.repository.SpendProfileRepository;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.mapper.ProjectUserMapper;
import com.worth.ifs.project.repository.MonitoringOfficerRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.ProjectLeadStatusResource;
import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static com.worth.ifs.invite.domain.ProjectParticipantRole.PROJECT_PARTNER;
import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.user.resource.OrganisationTypeEnum.isResearch;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;

class AbstractProjectServiceImpl extends BaseTransactionalService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    ProjectUserRepository projectUserRepository;

    @Autowired
    ProjectUserMapper projectUserMapper;

    @Autowired
    MonitoringOfficerRepository monitoringOfficerRepository;

    @Autowired
    BankDetailsRepository bankDetailsRepository;

    @Autowired
    SpendProfileRepository spendProfileRepository;

    @Autowired
    FinanceRowService financeRowService;

    ProjectActivityStates createOtherDocumentStatus(final Project project) {
        if (project.getCollaborationAgreement() != null && project.getExploitationPlan() != null) {
            return COMPLETE;
        } else {
            return ACTION_REQUIRED;
        }
    }

    ProjectActivityStates createGrantOfferLetterStatus() {
        //TODO update logic when GrantOfferLetter is implemented
        return NOT_STARTED;
    }

    ServiceResult<MonitoringOfficer> getExistingMonitoringOfficerForProject(Long projectId) {
        return find(monitoringOfficerRepository.findOneByProjectId(projectId), notFoundError(MonitoringOfficer.class, projectId));
    }

    ProjectPartnerStatusResource getProjectPartnerStatus(Project project, Organisation partnerOrganisation) {
        Organisation leadOrganisation = project.getApplication().getLeadOrganisation();
        Optional<MonitoringOfficer> monitoringOfficer = getExistingMonitoringOfficerForProject(project.getId()).getOptionalSuccessObject();
        Optional<BankDetails> bankDetails = Optional.ofNullable(bankDetailsRepository.findByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId()));
        Optional<SpendProfile> spendProfile = Optional.ofNullable(spendProfileRepository.findOneByProjectIdAndOrganisationId(project.getId(), partnerOrganisation.getId()));
        OrganisationTypeEnum organisationType = OrganisationTypeEnum.getFromId(partnerOrganisation.getOrganisationType().getId());

        ProjectActivityStates bankDetailsStatus = createBankDetailStatus(project, bankDetails, partnerOrganisation);
        ProjectActivityStates financeChecksStatus = createFinanceCheckStatus(bankDetailsStatus);
        ProjectActivityStates leadProjectDetailsSubmitted = createProjectDetailsStatus(project);
        ProjectActivityStates monitoringOfficerStatus = createMonitoringOfficerStatus(monitoringOfficer, leadProjectDetailsSubmitted);
        ProjectActivityStates spendProfileStatus = createSpendProfileStatus(financeChecksStatus, spendProfile);
        ProjectActivityStates otherDocumentsStatus = createOtherDocumentStatus(project);
        ProjectActivityStates grantOfferLetterStatus = createGrantOfferLetterStatus();

        ProjectPartnerStatusResource projectPartnerStatusResource;

        if (partnerOrganisation.equals(leadOrganisation)) {
            projectPartnerStatusResource = new ProjectLeadStatusResource(
                    partnerOrganisation.getName(),
                    organisationType,
                    leadProjectDetailsSubmitted,
                    monitoringOfficerStatus,
                    bankDetailsStatus,
                    financeChecksStatus,
                    spendProfileStatus,
                    otherDocumentsStatus,
                    grantOfferLetterStatus);
        } else {
            projectPartnerStatusResource = new ProjectPartnerStatusResource(
                    partnerOrganisation.getName(),
                    organisationType,
                    leadProjectDetailsSubmitted,
                    NOT_REQUIRED,
                    bankDetailsStatus,
                    financeChecksStatus,
                    spendProfileStatus,
                    NOT_REQUIRED,
                    NOT_REQUIRED);
        }

        return projectPartnerStatusResource;
    }

    ServiceResult<Void> validateProjectStartDate(LocalDate date) {

        if (date.getDayOfMonth() != 1) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));
        }

        if (date.isBefore(LocalDate.now())) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));
        }

        return serviceSuccess();
    }

    ServiceResult<Project> validateIfProjectAlreadySubmitted(final Project project) {

        if (project.isProjectDetailsSubmitted()) {
            return serviceFailure(new Error(PROJECT_SETUP_PROJECT_DETAILS_CANNOT_BE_UPDATED_IF_ALREADY_SUBMITTED));
        }

        return serviceSuccess(project);
    }

    ServiceResult<ProjectUser> validateProjectOrganisationFinanceContact(Project project, Long organisationId, Long financeContactUserId) {

        List<ProjectUser> projectUsers = project.getProjectUsers();

        List<ProjectUser> matchingUserOrganisationProcessRoles = simpleFilter(projectUsers,
                pr -> organisationId.equals(pr.getOrganisation().getId()) && financeContactUserId.equals(pr.getUser().getId()));

        if (matchingUserOrganisationProcessRoles.isEmpty()) {
            return serviceFailure(new Error(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
        }

        List<ProjectUser> partnerUsers = simpleFilter(matchingUserOrganisationProcessRoles, ProjectUser::isPartner);

        if (partnerUsers.isEmpty()) {
            return serviceFailure(new Error(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));
        }

        return getOnlyElementOrFail(partnerUsers);
    }

    ServiceResult<ProjectUser> validateProjectManager(Project project, Long projectManagerUserId) {

        List<ProjectUser> leadPartners = getLeadPartners(project);
        List<ProjectUser> matchingProjectUsers = simpleFilter(leadPartners, pu -> pu.getUser().getId().equals(projectManagerUserId));

        if (!matchingProjectUsers.isEmpty()) {
            return getOnlyElementOrFail(matchingProjectUsers);
        } else {
            return serviceFailure(new Error(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER));
        }
    }

    private List<ProjectUser> getLeadPartners(Project project) {
        Application application = project.getApplication();
        Organisation leadPartnerOrganisation = application.getLeadOrganisation();
        return simpleFilter(project.getProjectUsers(), pu -> organisationsEqual(leadPartnerOrganisation, pu)
                && pu.getRole().isPartner());
    }

    private boolean organisationsEqual(Organisation leadPartnerOrganisation, ProjectUser pu) {
        return pu.getOrganisation().getId().equals(leadPartnerOrganisation.getId());
    }

    ServiceResult<ProjectResource> createProjectFromApplicationId(final Long applicationId) {
        return getApplication(applicationId).andOnSuccess(application -> {
            Project project = new Project();
            project.setApplication(application);
            project.setDurationInMonths(application.getDurationInMonths());
            project.setName(application.getName());
            project.setTargetStartDate(application.getStartDate());

            List<ProcessRole> collaborativeRoles = simpleFilter(application.getProcessRoles(), ProcessRole::isLeadApplicantOrCollaborator);
            List<ServiceResult<ProjectUser>> correspondingProjectUsers = simpleMap(collaborativeRoles, role -> createPartnerProjectUser(project, role.getUser(), role.getOrganisation()));
            ServiceResult<List<ProjectUser>> projectUserCollection = aggregate(correspondingProjectUsers);

            return projectUserCollection.andOnSuccessReturn(projectUsers -> {
                projectUsers.forEach(project::addProjectUser);
                Project createdProject = projectRepository.save(project);
                return projectMapper.mapToResource(createdProject);
            });
        });
    }

    private ServiceResult<ProjectUser> createPartnerProjectUser(Project project, User user, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, PROJECT_PARTNER);
    }

    ServiceResult<ProjectUser> createProjectUserForRole(Project project, User user, Organisation organisation, ProjectParticipantRole role) {
        return serviceSuccess(new ProjectUser(user, project, role, organisation));
    }

    List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }

    protected ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findOne(projectId), notFoundError(Project.class, projectId));
    }

    ServiceResult<Project> getProjectByApplication(long applicationId) {
        return find(projectRepository.findOneByApplicationId(applicationId), notFoundError(Project.class, applicationId));
    }

    boolean validateIsReadyForSubmission(final Project project) {
        return !(project.getAddress() == null
                || !getExistingProjectManager(project).isPresent()
                || project.getTargetStartDate() == null
                || allFinanceContactsNotSet(project.getId())
                || project.getSubmittedDate() != null);
    }

    boolean validateDocumentsUploaded(final Project project) {
        return project.getExploitationPlan() != null
                && project.getCollaborationAgreement() != null
                && getExistingProjectManager(project).isPresent()
                && project.getDocumentsSubmittedDate() == null;
    }

    List<ProjectUser> getProjectUsersByProjectId(Long projectId) {
        return projectUserRepository.findByProjectId(projectId);
    }

    private boolean allFinanceContactsNotSet(Long projectId) {
        List<ProjectUser> projectUserObjs = getProjectUsersByProjectId(projectId);
        List<ProjectUserResource> projectUserResources = simpleMap(projectUserObjs, projectUserMapper::mapToResource);
        List<Organisation> partnerOrganisations = getPartnerOrganisations(projectId);
        List<ProjectUserResource> financeRoles = simpleFilter(projectUserResources, ProjectUserResource::isFinanceContact);
        return financeRoles.size() < partnerOrganisations.size();
    }

    List<Organisation> getPartnerOrganisations(Long projectId) {
        List<ProjectUser> projectUserObjs = getProjectUsersByProjectId(projectId);
        List<ProjectUserResource> projectRoles = simpleMap(projectUserObjs, projectUserMapper::mapToResource);
        return getPartnerOrganisations(projectRoles);
    }

    private List<Organisation> getPartnerOrganisations(List<ProjectUserResource> projectRoles) {
        final Comparator<Organisation> compareById =
                Comparator.comparingLong(Organisation::getId);

        final Supplier<SortedSet<Organisation>> supplier = () -> new TreeSet<>(compareById);

        SortedSet<Organisation> organisationSet = projectRoles.stream()
                .filter(uar -> uar.getRoleName().equals(PROJECT_PARTNER.getName()))
                .map(uar -> organisationRepository.findOne(uar.getOrganisation()))
                .collect(Collectors.toCollection(supplier));

        return new ArrayList<>(organisationSet);
    }

    ServiceResult<Void> createOrUpdateProjectManagerForProject(Project project, ProjectUser leadPartnerUser) {

        Optional<ProjectUser> existingProjectManager = getExistingProjectManager(project);

        return existingProjectManager.map(pm -> {

            pm.setUser(leadPartnerUser.getUser());
            pm.setOrganisation(leadPartnerUser.getOrganisation());
            return serviceSuccess();

        }).orElseGet(() -> {
            ProjectUser projectUser = new ProjectUser(leadPartnerUser.getUser(), leadPartnerUser.getProcess(),
                    PROJECT_MANAGER, leadPartnerUser.getOrganisation());
            project.addProjectUser(projectUser);
            return serviceSuccess();
        });
    }

    Optional<ProjectUser> getExistingProjectManager(Project project) {
        List<ProjectUser> projectUsers = getProjectUsersByProjectId(project.getId());
        List<ProjectUser> projectManagers = simpleFilter(projectUsers, pu -> pu.getRole().isProjectManager());
        return getOnlyElementOrEmpty(projectManagers);
    }

    ProjectActivityStates createProjectDetailsStatus(Project project) {
        return project.isProjectDetailsSubmitted() ? COMPLETE : ACTION_REQUIRED;
    }

    ProjectActivityStates createMonitoringOfficerStatus(final Optional<MonitoringOfficer> monitoringOfficer, final ProjectActivityStates leadProjectDetailsSubmitted) {
        if (leadProjectDetailsSubmitted.equals(COMPLETE)) {
            return monitoringOfficer.isPresent() ? COMPLETE : PENDING;
        } else {
            return NOT_STARTED;
        }

    }

    ProjectActivityStates createBankDetailStatus(final Project project, final Optional<BankDetails> bankDetails, final Organisation partnerOrganisation) {
        if (bankDetails.isPresent()) {
            return bankDetails.get().isApproved() ? COMPLETE : PENDING;
        } else {
            Boolean isSeekingFunding = financeRowService.organisationSeeksFunding(project.getId(), project.getApplication().getId(), partnerOrganisation.getId()).getSuccessObject();
            if (isResearch(partnerOrganisation.getOrganisationType().getId()) || !isSeekingFunding) {
                return NOT_REQUIRED;
            } else {
                return ACTION_REQUIRED;
            }
        }
    }

    private ProjectActivityStates createFinanceCheckStatus(final ProjectActivityStates bankDetailsStatus) {
        if(bankDetailsStatus.equals(COMPLETE) || bankDetailsStatus.equals(PENDING) || bankDetailsStatus.equals(NOT_REQUIRED)){
            return ACTION_REQUIRED;
        } else {
            //TODO update logic when Finance checks are implemented
            return NOT_STARTED;
        }
    }

    ProjectActivityStates createSpendProfileStatus(final ProjectActivityStates financeCheckStatus, final Optional<SpendProfile> spendProfile) {
        //TODO - Implement REJECT status when internal spend profile action story is completed
        if (spendProfile.isPresent()) {
            if (spendProfile.get().isMarkedAsComplete()) {
                return COMPLETE;
            } else {
                return ACTION_REQUIRED;
            }
        } else {
            if(financeCheckStatus.equals(COMPLETE)){
                return PENDING;
            } else {
                return NOT_STARTED;
            }
        }
    }
}
