package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.horizon.resource.ApplicationHorizonWorkProgrammeResource;
import org.innovateuk.ifs.horizon.resource.HorizonWorkProgramme;

import java.util.List;

public interface HorizonWorkProgrammeRestService {

    RestResult<Void> updateWorkProgrammeForApplication(List<HorizonWorkProgramme> selectedProgrammes, long applicationId);

    RestResult<List<ApplicationHorizonWorkProgrammeResource>> findAllWithApplicationId(long applicationId);
}
