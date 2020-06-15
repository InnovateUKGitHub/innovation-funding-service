package org.innovateuk.ifs.project.projectdetails.service;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeAndTownResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;

import java.time.LocalDate;

/**
 * REST service for Project Details related operations
 */
public interface ProjectDetailsRestService {

    RestResult<Void> updateProjectManager(Long projectId, Long projectManagerUserId);

    RestResult<Void> updateProjectAddress(long projectId, AddressResource address);

    RestResult<Void> updateProjectStartDate(Long projectId, LocalDate projectStartDate);

    RestResult<Void> updateProjectDuration(long projectId, long durationInMonths);

    RestResult<Void> updateFinanceContact(ProjectOrganisationCompositeId compositeId, Long financeContactUserId);

    RestResult<Void> updatePartnerProjectLocation(long projectId, long organisationId, PostcodeAndTownResource postcodeAndTown);

}
