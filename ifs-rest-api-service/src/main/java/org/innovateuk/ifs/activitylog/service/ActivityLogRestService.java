package org.innovateuk.ifs.activitylog.service;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.commons.rest.RestResult;

import java.util.List;

public interface ActivityLogRestService {

    RestResult<List<ActivityLogResource>> findByApplicationId(long applicationId);
}
