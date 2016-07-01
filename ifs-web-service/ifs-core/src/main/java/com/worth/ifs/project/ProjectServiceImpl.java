package com.worth.ifs.project;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.service.ProjectRestService;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.service.OrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.aggregate;
import static com.worth.ifs.util.CollectionFunctions.removeDuplicates;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private OrganisationRestService organisationRestService;

    @Override
    public List<ProjectUserResource> getProjectUsersForProject(Long projectId) {
        return projectRestService.getProjectUsersForProject(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectResource getById(Long projectId) {
        if (projectId == null) {
            return null;
        }

        return projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectResource getByApplicationId(Long applicationId) {
        if(applicationId == null) {
            return null;
        }
        RestResult<ProjectResource> restResult = projectRestService.getByApplicationId(applicationId);
        if(restResult.isSuccess()){
            return restResult.getSuccessObject();
        } else {
            return null;
        }
    }

    @Override
    public ServiceResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
        return projectRestService.updateProjectManager(projectId, projectManagerUserId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateFinanceContact(Long projectId, Long organisationId, Long financeContactUserId) {
        return projectRestService.updateFinanceContact(projectId, organisationId, financeContactUserId).toServiceResult();
    }

    @Override
    public ServiceResult<List<ProjectResource>> findByUser(Long userId) {
        return projectRestService.findByUserId(userId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return projectRestService.updateProjectStartDate(projectId, projectStartDate).toServiceResult();
    }

    @Override
    public ServiceResult<Void> updateAddress(Long leadOrganisationId, Long projectId, OrganisationAddressType addressType, AddressResource address) {
        return projectRestService.updateProjectAddress(leadOrganisationId, projectId, addressType, address).toServiceResult();
    }

    @Override
    public ServiceResult<Void> setApplicationDetailsSubmitted(Long projectId) {
        return projectRestService.setApplicationDetailsSubmitted(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSubmitAllowed(Long projectId) {
        return projectRestService.isSubmitAllowed(projectId).toServiceResult();
    }

    @Override
    public OrganisationResource getLeadOrganisation(Long projectId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccessObjectOrThrowException();
        return applicationService.getLeadOrganisation(project.getApplication());
    }

    @Override
    public List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId) {

        List<ProjectUserResource> projectUsers = getProjectUsersForProject(projectId);

        List<Long> organisationIds = removeDuplicates(simpleMap(projectUsers, pu -> pu.getOrganisation()));
        List<RestResult<OrganisationResource>> organisationResults = simpleMap(organisationIds, organisationRestService::getOrganisationById);
        RestResult<List<OrganisationResource>> organisationResultsCombined = aggregate(organisationResults);

        return organisationResultsCombined.getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> updateMonitoringOfficer(Long projectId, String firstName, String lastName, String emailAddress, String phoneNumber) {
        return projectRestService.updateMonitoringOfficer(projectId, firstName, lastName, emailAddress, phoneNumber).toServiceResult();
    }
}
