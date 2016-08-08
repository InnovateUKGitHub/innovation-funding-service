package com.worth.ifs.sil.experian.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.sil.experian.resource.AccountDetails;
import com.worth.ifs.sil.experian.resource.SILBankDetails;
import com.worth.ifs.sil.experian.resource.ValidationResult;
import com.worth.ifs.sil.experian.resource.VerificationResult;

/**
 * Represents the communication with the SIL endpoint for sending outbound emails from the application
 */
public interface SilExperianEndpoint {
    ServiceResult<ValidationResult> validate(SILBankDetails accountDetails);
    ServiceResult<VerificationResult> verify(AccountDetails accountDetails);
}
