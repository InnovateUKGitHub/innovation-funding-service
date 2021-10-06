package org.innovateuk.ifs.crm.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * A Service that covers basic operations concerning CRM data
 */
public interface CrmService {

    @NotSecured(value = "Anyone can update crm", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> syncCrmContact(final long userId);

    @NotSecured(value = "Anyone can update crm", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> syncCrmContact(final long userId, final long projectId);

    @NotSecured(value = "Anyone can update crm", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> syncCrmContact(long userId, long competitionId, Long applicationId);

    @NotSecured(value = "Anyone can update crm", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> updateCrmApplicationEligibility(Long applicationId);

}