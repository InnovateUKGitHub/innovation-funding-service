package org.innovateuk.ifs.activitylog.service;

import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.activityLogResourceListType;

@Service
public class ActivityLogRestServiceImpl extends BaseRestService implements ActivityLogRestService {

    @Override
    public RestResult<List<ActivityLogResource>> findByApplicationId(long applicationId) {
        return getWithRestResult(String.format("activity-log?applicationId=%d", applicationId), activityLogResourceListType());
    }
}
