package org.innovateuk.ifs.project.projectdetails.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
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
    public RestResult<Void> updateProjectAddress(long leadOrganisationId, long projectId, OrganisationAddressType addressType, AddressResource address) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/address?addressType=" + addressType.name() + "&leadOrganisationId=" + leadOrganisationId, address, Void.class);
    }

    @Override
    public RestResult<Void> updateFinanceContact(ProjectOrganisationCompositeId composite, Long financeContactUserId) {
        return postWithRestResult(projectRestURL + "/" + composite.getProjectId() + "/organisation/" + composite.getOrganisationId() + "/finance-contact?financeContact=" + financeContactUserId, Void.class);
    }

    public RestResult<Void> inviteFinanceContact(Long projectId, InviteProjectResource inviteResource) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/invite-finance-contact", inviteResource, Void.class);
    }

    @Override public RestResult<Void> inviteProjectManager(final Long projectId, final InviteProjectResource inviteResource) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/invite-project-manager", inviteResource, Void.class);
    }
}
