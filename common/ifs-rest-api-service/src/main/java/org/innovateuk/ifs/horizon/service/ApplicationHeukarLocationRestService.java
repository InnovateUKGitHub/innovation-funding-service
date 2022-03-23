package org.innovateuk.ifs.horizon.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.heukar.resource.ApplicationHeukarLocationResource;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;

import java.util.List;

public interface ApplicationHeukarLocationRestService {

    RestResult<Void> updateLocationsForApplication(List<HeukarLocation> selectedLocations, long applicationId);

    RestResult<List<ApplicationHeukarLocationResource>> findAllWithApplicationId(long applicationId);

}
