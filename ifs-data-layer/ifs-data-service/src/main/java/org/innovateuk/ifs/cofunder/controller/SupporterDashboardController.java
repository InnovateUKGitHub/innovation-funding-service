package org.innovateuk.ifs.supporter.controller;

import org.innovateuk.ifs.supporter.resource.AssessorDashboardState;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardCompetitionResource;
import org.innovateuk.ifs.supporter.transactional.SupporterDashboardService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/supporter/dashboard")
public class SupporterDashboardController {
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private SupporterDashboardService supporterDashboardService;

    @GetMapping("/user/{userId}/dashboard")
    public RestResult<Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>>> getSupporterDashboard(@PathVariable long userId) {
        return supporterDashboardService.getCompetitionsForCofunding(userId).toGetResponse();
    }

    @GetMapping("/user/{userId}/competition/{competitionId}")
    public RestResult<SupporterDashboardApplicationPageResource> getSupporterDashboard(@PathVariable long userId, @PathVariable long competitionId, @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"activityState"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return supporterDashboardService.getApplicationsForCofunding(userId, competitionId, pageable).toGetResponse();
    }
}
