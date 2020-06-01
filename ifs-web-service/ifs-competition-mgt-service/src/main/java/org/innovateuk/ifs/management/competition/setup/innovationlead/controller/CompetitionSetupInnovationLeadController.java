package org.innovateuk.ifs.management.competition.setup.innovationlead.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupInnovationLeadService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.initialdetail.populator.ManageInnovationLeadsModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for managing innovation leads
 */
@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "Controller for managing innovation leads", securedType = CompetitionSetupInnovationLeadController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupInnovationLeadController {
    private static final String COMPETITION_ID_KEY = "competitionId";
    private static final String MODEL = "model";

    @Autowired
    private ManageInnovationLeadsModelPopulator manageInnovationLeadsModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupInnovationLeadService competitionSetupInnovationLeadService;

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @GetMapping("/{competitionId}/manage-innovation-leads/find")
    public String manageInnovationLead(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                       Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));

        return "competition/manage-innovation-leads-find";
    }

    @PreAuthorize("hasPermission(#competitionId,'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @GetMapping("/{competitionId}/manage-innovation-leads/overview")
    public String manageInnovationLeadOverview(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                               Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));

        return "competition/manage-innovation-leads-overview";
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @PostMapping("/{competitionId}/add-innovation-lead/{innovationLeadUserId}")
    public String addInnovationLead(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @PathVariable("innovationLeadUserId") long innovationLeadUserId,
                                    Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        return competitionSetupInnovationLeadService.addInnovationLead(competitionId, innovationLeadUserId).handleSuccessOrFailure(
                failure -> "redirect:/competition/manage-innovation-leads/find",
                success -> {
                    model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));
                    return "competition/manage-innovation-leads-find";
                }
        );
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @PostMapping("/{competitionId}/remove-innovation-lead/{innovationLeadUserId}")
    public String removeInnovationLead(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                       @PathVariable("innovationLeadUserId") long innovationLeadUserId,
                                       Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        return competitionSetupInnovationLeadService.removeInnovationLead(competitionId, innovationLeadUserId).handleSuccessOrFailure(
                failure -> "redirect:/competition/manage-innovation-leads/overview",
                success -> {
                    model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));
                    return "competition/manage-innovation-leads-overview";
                }
        );
    }
}
