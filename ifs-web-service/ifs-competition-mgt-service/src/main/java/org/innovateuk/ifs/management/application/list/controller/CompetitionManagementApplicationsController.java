package org.innovateuk.ifs.management.application.list.controller;

import org.innovateuk.ifs.commons.exception.IncorrectStateForPageException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.list.populator.*;
import org.innovateuk.ifs.management.application.view.form.IneligibleApplicationsForm;
import org.innovateuk.ifs.management.funding.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;


/**
 * This controller will handle all requests that are related to the applications of a Competition within Competition Management.
 */
@Controller
@RequestMapping("/competition/{competitionId}/applications")
public class CompetitionManagementApplicationsController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "20";

    private static final String DEFAULT_SORT_BY = "id";

    private static final String PREVIOUS_APP_DEFAULT_FILTER = "ALL";

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";

    @Autowired
    private ApplicationsMenuModelPopulator applicationsMenuModelPopulator;

    @Autowired
    private AllApplicationsPageModelPopulator allApplicationsPageModelPopulator;

    @Autowired
    private SubmittedApplicationsModelPopulator submittedApplicationsModelPopulator;

    @Autowired
    private IneligibleApplicationsModelPopulator ineligibleApplicationsModelPopulator;


    @Autowired
    private CompetitionRestService competitionRestService;

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users, Innovation Leads and Stakeholders can view the applications menu")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping
    public String applicationsMenu(Model model, @PathVariable("competitionId") long competitionId, UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        checkCompetitionIsOpen(competition);
        model.addAttribute("model", applicationsMenuModelPopulator.populateModel(competition, user));
        return "competition/applications-menu";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users, Innovation Leads and Stakeholders can view the list of all applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/all")
    public String allApplications(Model model,
                                  @PathVariable("competitionId") long competitionId,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "sort", defaultValue = "") String sort,
                                  @RequestParam(value = "filterSearch") Optional<String> filter,
                                  UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        checkCompetitionIsOpen(competition);
        model.addAttribute("model", allApplicationsPageModelPopulator.populateModel(competitionId, page, sort, filter, user));

        return "competition/all-applications";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users, Innovation Leads and Stakeholders can view the list of submitted applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/submitted")
    public String submittedApplications(Model model,
                                        @PathVariable("competitionId") long competitionId,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "sort", defaultValue = "") String sort,
                                        @RequestParam(value = "filterSearch") Optional<String> filter) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        checkCompetitionIsOpen(competition);
        model.addAttribute("model", submittedApplicationsModelPopulator.populateModel(competitionId, page, sort, filter));

        return "competition/submitted-applications";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users, Innovation Leads and Stakeholders can view the list of ineligible applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead', 'stakeholder')")
    @GetMapping("/ineligible")
    public String ineligibleApplications(Model model,
                                         @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) IneligibleApplicationsForm filterForm,
                                         @PathVariable("competitionId") long competitionId,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "sort", defaultValue = "") String sort,
                                         UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        checkCompetitionIsOpen(competition);
        model.addAttribute("model", ineligibleApplicationsModelPopulator.populateModel(competitionId, page, sort, filterForm, user));

        return "competition/ineligible-applications";
    }

    private void checkCompetitionIsOpen(CompetitionResource competition) {
        if (!competition.getCompetitionStatus().isLaterThan(CompetitionStatus.READY_TO_OPEN)) {
            throw new IncorrectStateForPageException("Competition is not yet open.");
        }
    }
}
