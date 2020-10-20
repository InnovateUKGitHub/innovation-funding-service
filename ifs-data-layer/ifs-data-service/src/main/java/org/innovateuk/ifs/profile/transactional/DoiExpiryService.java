package org.innovateuk.ifs.profile.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.schedule.transactional.ScheduleResponse;
import org.springframework.security.access.prepost.PreAuthorize;

public interface DoiExpiryService {

    @SecuredBySpring(value = "DOI_EXPIRY", description = "A System Registration User can notify assessors of DOI expiry.")
    @PreAuthorize("hasAuthority('system_registrar')")
    ServiceResult<ScheduleResponse> notifyExpiredDoi();
}
