package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.resource.ApplicationHeukarLocationResource;
import org.innovateuk.ifs.heukar.resource.HeukarLocation;

import java.util.List;

public interface ApplicationHeukarLocationService {

    ServiceResult<Void> updateLocationsForApplication(List<HeukarLocation> locations, long applicationId);

    ServiceResult<List<ApplicationHeukarLocationResource>> findSelectedForApplication(long applicationId);
}
