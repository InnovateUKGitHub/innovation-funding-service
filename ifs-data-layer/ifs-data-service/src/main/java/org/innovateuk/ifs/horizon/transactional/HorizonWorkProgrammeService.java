package org.innovateuk.ifs.horizon.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;

public interface HorizonWorkProgrammeService {

    ServiceResult<Void> updateWorkProgrammesForApplication(List<HorizonWorkProgramme> programmes, long applicationId);

    ServiceResult<List<ApplicationHorizonWorkProgrammeResource>> findSelectedForApplication(long applicationId);
}
