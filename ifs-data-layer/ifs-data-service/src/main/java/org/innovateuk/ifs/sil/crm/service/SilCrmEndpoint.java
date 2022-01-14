package org.innovateuk.ifs.sil.crm.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilLoanApplication;
import org.innovateuk.ifs.sil.crm.resource.SilLoanAssessment;

public interface SilCrmEndpoint {
    ServiceResult<Void> updateContact(SilContact silContact);
    ServiceResult<Void> updateLoanApplicationState(SilLoanApplication silApplication);
    ServiceResult<Void> updateLoanAssessment(SilLoanAssessment silLoanAssessment);
}