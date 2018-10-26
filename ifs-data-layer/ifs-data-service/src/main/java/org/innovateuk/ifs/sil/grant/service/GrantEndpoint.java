package org.innovateuk.ifs.sil.grant.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.resource.ValidationResult;
import org.innovateuk.ifs.sil.experian.resource.VerificationResult;
import org.innovateuk.ifs.sil.grant.resource.Project;

/**
 * Sent project data to grant monitoring service.
 */
public interface GrantEndpoint {
    ServiceResult<Void> sendProject(Project project);
}
