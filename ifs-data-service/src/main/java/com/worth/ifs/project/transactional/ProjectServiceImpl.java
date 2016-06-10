package com.worth.ifs.project.transactional;

import com.worth.ifs.address.domain.Address;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.address.repository.AddressRepository;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.organisation.repository.OrganisationAddressRepository;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.mapper.ProjectUserMapper;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.util.CollectionFunctions.simpleFilter;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.EntityLookupCallbacks.getOnlyElementOrFail;

@Service
public class ProjectServiceImpl extends BaseTransactionalService implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrganisationAddressRepository organisationAddressRepository;

    @Override
    public ServiceResult<ProjectResource> getProjectById(@P("projectId") Long projectId) {
        return getProject(projectId).andOnSuccessReturn(projectMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<ProjectResource>> findAll() {
        return serviceSuccess(projectsToResources(projectRepository.findAll()));
    }

    @Override
    public ServiceResult<ProjectResource> createProjectFromApplication(Long applicationId) {
        return createProjectFromApplicationId(applicationId);
    }
    
    @Override
    public ServiceResult<Void> setProjectManager(Long projectId, Long projectManagerId) {
        return getProject(projectId).
                andOnSuccess(project -> validateProjectManager(project, projectManagerId).
                andOnSuccessReturnVoid(project::setProjectManager));
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> getProject(projectId).
                andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate)));
    }
    
	@Override
	public ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
		 return getProject(projectId).
				  andOnSuccess(project -> validateProjectOrganisationFinanceContact(project, organisationId, financeContactUserId).
				  andOnSuccess(projectUser -> createFinanceContactProjectUser(projectUser.getUser(), project, projectUser.getOrganisation()).
                  andOnSuccessReturnVoid(financeContact -> addFinanceContactToProject(project, financeContact))));
	}

    @Override
    public ServiceResult<Void> updateProjectAddress(Long organisationId, Long projectId, AddressType addressType, AddressResource address) {
        ServiceResult<Project> result = getProject(projectId);
        if(result.isSuccess()){
            Project project = result.getSuccessObject();
            Organisation leadOrganisation = organisationRepository.findOne(organisationId);
            if (address.getId() != null && addressRepository.exists(address.getId())) {
                Address existingAddress = addressRepository.findOne(address.getId());
                project.setAddress(existingAddress);
            } else {
                Address newAddress = addressMapper.mapToDomain(address);
                if(address.getOrganisations() == null || address.getOrganisations().size() == 0){
                    List<OrganisationAddress> existingOrgAddresses = organisationAddressRepository.findByOrganisationIdAndAddressType(leadOrganisation.getId(), addressType);
                    existingOrgAddresses.stream().forEach(oA -> organisationAddressRepository.delete(oA));
                    OrganisationAddress organisationAddress = new OrganisationAddress(leadOrganisation, newAddress, addressType);
                    organisationAddressRepository.save(organisationAddress);
                }
                project.setAddress(newAddress);
            }
            return serviceSuccess();
        } else {
            return serviceFailure(result.getFailure().getErrors());
        }
    }

    @Override
    public ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions) {
        applicationFundingDecisions.keySet().stream().forEach(this::createProjectFromApplicationId);
        return serviceSuccess();
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUserId(final Long userId) {
        return getUser(userId).andOnSuccessReturn(user -> {
            List<ProcessRole> roles = processRoleRepository.findByUser(user);
            List<Application> applications = simpleMap(roles, ProcessRole::getApplication);
            List<Project> projects = applications.stream().map(a -> projectRepository.findOneByApplicationId(a.getId())).collect(Collectors.toList());
            return projectsToResources(projects);
        });
    }

    public ServiceResult<List<ProjectUserResource>> getProjectUsers(Long projectId) {
        List<ProjectUser> projectUsers = projectUserRepository.findByProjectId(projectId);
        return serviceSuccess(simpleMap(projectUsers, projectUserMapper::mapToResource));
    }

    private void addFinanceContactToProject(Project project, ProjectUser financeContact) {

        ProjectUser existingUser = project.getExistingProjectUserWithRoleForOrganisation(FINANCE_CONTACT, financeContact.getOrganisation());

        if (existingUser != null) {
            project.removeProjectUser(existingUser);
        }
        
        project.addProjectUser(financeContact);
    }

    private ServiceResult<ProjectUser> createFinanceContactProjectUser(User user, Project project, Organisation organisation) {
        return createProjectUserForRole(project, user, organisation, FINANCE_CONTACT);
    }

    private ServiceResult<Void> validateProjectStartDate(LocalDate date) {

        if (date.getDayOfMonth() != 1) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));
        }

        if (date.isBefore(LocalDate.now())) {
            return serviceFailure(new Error(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));
        }

        return serviceSuccess();
    }

    private ServiceResult<ProjectUser> validateProjectOrganisationFinanceContact(Project project, Long organisationId, Long financeContactUserId) {

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

    private ServiceResult<ProcessRole> validateProjectManager(Project project, Long projectManagerId) {
        Application application = applicationRepository.findOne(project.getApplication().getId());
		Organisation leadPartner = application.getLeadOrganisation();

        List<ProcessRole> leadPartnerProcessRoles = simpleFilter(application.getProcessRoles(), pr -> leadPartner.equals(pr.getOrganisation()));
        List<ProcessRole> matchingProcessRoles = simpleFilter(leadPartnerProcessRoles, lppr -> projectManagerId.equals(lppr.getUser().getId()));

        if(!matchingProcessRoles.isEmpty()) {
			return getOnlyElementOrFail(matchingProcessRoles).andOnSuccess(processRole -> serviceSuccess(processRole));
		} else {
			return serviceFailure(new Error(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_IN_LEAD_ORGANISATION));
		}
	}
    
    private ServiceResult<ProjectResource> createProjectFromApplicationId(final Long applicationId){
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
        return createProjectUserForRole(project, user, organisation, PARTNER);
    }

    private ServiceResult<ProjectUser> createProjectUserForRole(Project project, User user, Organisation organisation, UserRoleType roleType) {
        return getRole(roleType).andOnSuccessReturn(role -> new ProjectUser(user, project, role, organisation));
    }

    private List<ProjectResource> projectsToResources(List<Project> filtered) {
        return simpleMap(filtered, project -> projectMapper.mapToResource(project));
    }
    
    private ServiceResult<Project> getProject(long projectId) {
        return find(projectRepository.findOne(projectId), notFoundError(Project.class, projectId));
    }

    private ServiceResult<Address> getAddress(long addressId) {
        return find(addressRepository.findOne(addressId), notFoundError(Address.class, addressId));
    }
}
