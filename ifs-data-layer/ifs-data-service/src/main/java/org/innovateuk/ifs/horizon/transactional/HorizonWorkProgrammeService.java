package org.innovateuk.ifs.horizon.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HorizonWorkProgrammeService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'UPDATE')")
    ServiceResult<Void> updateWorkProgrammesForApplication(List<HorizonWorkProgramme> programmes, long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(long applicationId);
}
