package org.innovateuk.ifs.sil.crm.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.sil.crm.resource.SilContact;
import org.innovateuk.ifs.sil.crm.resource.SilApplication;

public interface SilCrmEndpoint {
    ServiceResult<Void> updateContact(SilContact silContact);
    ServiceResult<Void> updateApplicationEligibility(SilApplication silApplication);
}