package org.innovateuk.ifs.project.projectdetails.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeAndTownResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * REST service for Project Details related operations
 */
@Service
public class ProjectDetailsRestServiceImpl extends BaseRestService implements ProjectDetailsRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/project-manager/" + projectManagerUserId, Void.class);
    }

    @Override
    public RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/startdate?projectStartDate=" + projectStartDate, Void.class);
    }

    @Override
    public RestResult<Void> updateProjectDuration(long projectId, long durationInMonths) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/duration/" + durationInMonths, Void.class);
    }

    @Override
    public RestResult<Void> updateProjectAddress(long projectId, AddressResource address) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/address", address, Void.class);
    }

    @Override
    public RestResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return postWithRestResult(projectRestURL + "/" + composite.getProjectId() + "/organisation/" + composite.getOrganisationId() + "/finance-contact?financeContact=" + financeContactUserId, Void.class);
    }

    @Override
    public RestResult<Void> updatePartnerProjectLocation(long projectId, long organisationId, PostcodeAndTownResource postcodeAndTown) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/organisation/" + organisationId + "/partner-project-location", postcodeAndTown, Void.class);
    }
}
