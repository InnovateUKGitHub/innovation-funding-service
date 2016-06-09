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
import com.worth.ifs.project.mapper.ProjectMapper;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

@Service
public class ProjectServiceImpl extends BaseTransactionalService implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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
        return serviceSuccess(createProjectFromApplicationId(applicationId));
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return validateProjectStartDate(projectStartDate).
                andOnSuccess(() -> getProject(projectId).
                andOnSuccessReturnVoid(project -> project.setTargetStartDate(projectStartDate)));
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
    public ServiceResult<List<ProjectResource>> findByUserId(final Long userId){
        return getUser(userId).andOnSuccessReturn(user -> {
            List<ProcessRole> roles = processRoleRepository.findByUser(user);
            List<Application> applications = simpleMap(roles, ProcessRole::getApplication);
            List<Project> projects = applications.stream().map(a -> projectRepository.findOneByApplicationId(a.getId())).collect(Collectors.toList());
            return projectsToResources(projects);
        });
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

    private ProjectResource createProjectFromApplicationId(final Long applicationId){
        Application application = applicationRepository.findOne(applicationId);
        Project project = new Project();
        project.setId(applicationId);
        project.setApplication(application);
        project.setDurationInMonths(application.getDurationInMonths());
        project.setName(application.getName());
        project.setTargetStartDate(application.getStartDate());
        Project createdProject = projectRepository.save(project);
        return projectMapper.mapToResource(createdProject);
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
