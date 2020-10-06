package org.innovateuk.ifs.cofunder.controller;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.cofunder.transactional.CofunderDashboardService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/assessment")
public class CofunderDashboardController {

    @Autowired
    private CofunderDashboardService cofunderDashboardService;

    @GetMapping("/user/{userId}/dashboard")
    public RestResult<CofunderDashboardCompetitionResource> getCofunderDashboard(@PathVariable long userId) {
        return cofunderDashboardService.getCompetitionsForCofunding(userId).toGetResponse();
    }
}
