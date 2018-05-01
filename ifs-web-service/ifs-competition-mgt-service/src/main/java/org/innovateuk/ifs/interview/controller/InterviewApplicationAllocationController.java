package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.AssessorInterviewAllocationPageResource;
import org.innovateuk.ifs.interview.service.InterviewInviteRestService;
import org.innovateuk.ifs.interview.viewmodel.InterviewAvailableAssessorRowViewModel;
import org.innovateuk.ifs.invite.resource.AvailableAssessorPageResource;
import org.innovateuk.ifs.management.controller.CompetitionManagementAssessorProfileController;
import org.innovateuk.ifs.management.model.InterviewApplicationAllocationModelPopulator;
import org.innovateuk.ifs.management.model.ManageAssessorsModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.innovateuk.ifs.management.viewmodel.InterviewApplicationAllocationViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.management.controller.CompetitionManagementCookieController.SELECTION_LIMIT;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * This controller will handle all Competition Management requests related to allocating applications to assessors for interview panel.
 */
@Controller
@RequestMapping("/assessment/interview/competition/{competitionId}/allocate")
@SecuredBySpring(value = "Controller", description = "Comp Admins and Project Finance users can assign application to assessors for an Interview Panel", securedType = InterviewApplicationAllocationController.class)
//@PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'INTERVIEW')")
public class InterviewApplicationAllocationController {

    @Autowired
    private InterviewApplicationAllocationModelPopulator interviewApplicationAllocationModelPopulator;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private AssessorCountSummaryRestService assessorCountSummaryRestService;

    @Autowired
    private InterviewInviteRestService interviewInviteRestService;

    @GetMapping("/assessors")
    public String find(Model model,
                       @PathVariable("competitionId") long competitionId,
                       @RequestParam MultiValueMap<String, String> queryParams,
                       @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

//        AssessorCountSummaryPageResource assessorCount = getCounts(competitionId, page);

        String originQuery = buildOriginQueryString(CompetitionManagementAssessorProfileController.AssessorProfileOrigin.INTERVIEW_ACCEPTED, queryParams);

        model.addAttribute("model", interviewApplicationAllocationModelPopulator.populateModel(
                competitionResource,
                originQuery
        ));
        model.addAttribute("originQuery", originQuery);

        return "competition/allocate-applications-assessors";
    }

//    private AssessorCountSummaryPageResource getCounts(long competitionId,  int page) {
//        return assessorCountSummaryRestService
//                .getAssessorCountSummariesByCompetitionIdOnInterviewPanel(competitionId, page, 20)
//                .getSuccess();
//    }
}
