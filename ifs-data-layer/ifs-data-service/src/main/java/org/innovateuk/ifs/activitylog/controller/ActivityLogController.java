package org.innovateuk.ifs.activitylog.controller;


import org.innovateuk.ifs.activitylog.resource.ActivityLogResource;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity-log")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping(params = "applicationId")
    public RestResult<List<ActivityLogResource>> findByApplicationId(@RequestParam long applicationId) {
        return activityLogService.findByApplicationId(applicationId).toGetResponse();
    }
}
