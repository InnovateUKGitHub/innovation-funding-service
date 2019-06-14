package org.innovateuk.ifs.project.queries.controller;

import org.innovateuk.ifs.activitylog.domain.ActivityType;
import org.innovateuk.ifs.activitylog.transactional.ActivityLogService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.threads.controller.CommonThreadController;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.threads.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project/finance/queries")
public class ProjectFinanceQueriesController extends CommonThreadController<QueryResource> {

    private ActivityLogService activityLogService;

    @Autowired
    public ProjectFinanceQueriesController(ThreadService<QueryResource, PostResource> service, ActivityLogService activityLogService) {
        super(service);
        this.activityLogService = activityLogService;
    }

    @Override
    public RestResult<Long> create(QueryResource thread) {
        return super.create(thread)
                .andOnSuccessReturn(threadId ->  {
                    activityLogService.recordQueryActivityByProjectFinanceId(thread.contextClassPk,
                                    ActivityType.FINANCE_QUERY, threadId);
                    return threadId;
                });
    }
}