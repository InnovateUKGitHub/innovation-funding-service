package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationService;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/assessment/competition/{competitionId}/assessors")
public class CompetitionManagementAssessmentsAssessorProgressController {

    @Autowired
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Autowired
    private CompetitionManagementApplicationService competitionManagementApplicationService;

    @GetMapping("/{assessorId}")
    public String assessorProgress(@PathVariable("competitionId") long competitionId,
                                   @PathVariable("assessorId") long assessorId,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "innovationArea", required = false) Optional<Long> innovationArea,
                                   @RequestParam(value = "sortField", defaultValue = "") String sortField,
                                   @RequestParam MultiValueMap<String, String> params,
                                   Model model) {
        params.add("assessorId", String.valueOf(assessorId));
        String originQuery = buildOriginQueryString(CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin.ASSESSOR_PROGRESS, params);
        model.addAttribute("originQuery", originQuery);
        model.addAttribute("model", assessorAssessmentProgressModelPopulator.populateModel(competitionId, assessorId, page, innovationArea, sortField, originQuery));

        return "competition/assessor-progress";
    }

    @PostMapping("/{assessorId}/application/{applicationId}/assign")
    public String assessorAssign(@PathVariable("competitionId") long competitionId,
                                 @PathVariable("assessorId") long assessorId,
                                 @PathVariable("applicationId") long applicationId) {

        competitionManagementApplicationService.addAssessorToApplication(applicationId, assessorId);

        return "redirect:/assessment/competition/" + competitionId + "/assessors/" + assessorId;
    }
}
