package org.innovateuk.ifs.sil.experian.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.experian.resource.AccountDetails;
import org.innovateuk.ifs.sil.experian.resource.SILBankDetails;
import org.innovateuk.ifs.sil.experian.resource.ValidationResult;
import org.innovateuk.ifs.sil.experian.resource.VerificationResult;

/**
 * Represents the communication with the SIL endpoint for sending outbound emails from the application
 */
public interface SilExperianEndpoint {
    ServiceResult<ValidationResult> validate(SILBankDetails accountDetails);
    ServiceResult<VerificationResult> verify(AccountDetails accountDetails);
}
