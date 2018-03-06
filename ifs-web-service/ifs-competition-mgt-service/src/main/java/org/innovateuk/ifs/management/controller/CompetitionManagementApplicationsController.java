package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.management.form.IneligibleApplicationsForm;
import org.innovateuk.ifs.management.model.*;
import org.innovateuk.ifs.management.service.CompetitionManagementApplicationServiceImpl.ApplicationOverviewOrigin;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.BackLinkUtil.buildOriginQueryString;


/**
 * This controller will handle all requests that are related to the applications of a Competition within Competition Management.
 */
@Controller
@RequestMapping("/competition/{competitionId}/applications")
public class CompetitionManagementApplicationsController {

    private static final String DEFAULT_PAGE_NUMBER = "0";

    private static final String DEFAULT_PAGE_SIZE = "20";

    private static final String DEFAULT_SORT_BY = "id";

    private static final String FILTER_FORM_ATTR_NAME = "filterForm";

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationsMenuModelPopulator applicationsMenuModelPopulator;

    @Autowired
    private AllApplicationsPageModelPopulator allApplicationsPageModelPopulator;

    @Autowired
    private SubmittedApplicationsModelPopulator submittedApplicationsModelPopulator;

    @Autowired
    private IneligibleApplicationsModelPopulator ineligibleApplicationsModelPopulator;

    @Autowired
    private UnsuccessfulApplicationsModelPopulator unsuccessfulApplicationsModelPopulator;

    @Autowired
    private NavigateApplicationsModelPopulator navigateApplicationsModelPopulator;

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users and Innovation Leads can view the applications menu")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
    @GetMapping
    public String applicationsMenu(Model model, @PathVariable("competitionId") long competitionId, UserResource user) {
        model.addAttribute("model", applicationsMenuModelPopulator.populateModel(competitionId, user));
        return "competition/applications-menu";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users and Innovation Leads can view the list of all applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
    @GetMapping("/all")
    public String allApplications(Model model,
                                  @PathVariable("competitionId") long competitionId,
                                  @RequestParam MultiValueMap<String, String> queryParams,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "sort", defaultValue = "") String sort,
                                  @RequestParam(value = "filterSearch") Optional<String> filter,
                                  UserResource user) {
        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.ALL_APPLICATIONS, queryParams);
        model.addAttribute("model", allApplicationsPageModelPopulator.populateModel(competitionId, originQuery, page, sort, filter, user));
        model.addAttribute("originQuery", originQuery);

        return "competition/all-applications";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users and Innovation Leads can view the list of submitted applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
    @GetMapping("/submitted")
    public String submittedApplications(Model model,
                                        @PathVariable("competitionId") long competitionId,
                                        @RequestParam MultiValueMap<String, String> queryParams,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "sort", defaultValue = "") String sort,
                                        @RequestParam(value = "filterSearch") Optional<String> filter) {
        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.SUBMITTED_APPLICATIONS, queryParams);
        model.addAttribute("model", submittedApplicationsModelPopulator.populateModel(competitionId, originQuery, page, sort, filter));
        model.addAttribute("originQuery", originQuery);

        return "competition/submitted-applications";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users and Innovation Leads can view the list of ineligible applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'innovation_lead')")
    @GetMapping("/ineligible")
    public String ineligibleApplications(Model model,
                                         @Valid @ModelAttribute(FILTER_FORM_ATTR_NAME) IneligibleApplicationsForm filterForm,
                                         @PathVariable("competitionId") long competitionId,
                                         @RequestParam MultiValueMap<String, String> queryParams,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "sort", defaultValue = "") String sort,
                                         UserResource user) {
        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.INELIGIBLE_APPLICATIONS, queryParams);
        model.addAttribute("model", ineligibleApplicationsModelPopulator.populateModel(competitionId, originQuery, page, sort, filterForm, user));
        model.addAttribute("originQuery", originQuery);

        return "competition/ineligible-applications";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users and IFS Admins can view the list of unsuccessful applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'ifs_administrator', 'innovation_lead')")
    @GetMapping("/unsuccessful")
    public String unsuccessfulApplications(Model model,
                                           @PathVariable("competitionId") long competitionId,
                                           @RequestParam MultiValueMap<String, String> queryParams,
                                           @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER) int page,
                                           @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE) int size,
                                           @RequestParam(value = "sort", defaultValue = DEFAULT_SORT_BY) String sortBy,
                                           UserResource loggedInUser) {

        String originQuery = buildOriginQueryString(ApplicationOverviewOrigin.UNSUCCESSFUL_APPLICATIONS, queryParams);
        model.addAttribute("model", unsuccessfulApplicationsModelPopulator.populateModel(competitionId, page, size, sortBy, loggedInUser, originQuery));
        model.addAttribute("originQuery", originQuery);

        return "competition/unsuccessful-applications";
    }

    @SecuredBySpring(value = "READ", description = "Comp Admins, Project Finance users, Support users and IFS Admins can navigate between different lists of applications to a competition")
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'support', 'ifs_administrator', 'innovation_lead')")
    @GetMapping("/manage")
    public String manageApplications(Model model,
                                     @PathVariable("competitionId") long competitionId) {

        model.addAttribute("model", navigateApplicationsModelPopulator.populateModel(competitionId));
        return "competition/navigate-applications";
    }

    @SecuredBySpring(value = "UPDATE", description = "Only the IFS admin is able to mark an application as successful after funding decisions have been made")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @PostMapping("/mark-successful/application/{applicationId}")
    public String markApplicationAsSuccessful(
                                              @PathVariable("competitionId") long competitionId,
                                              @PathVariable("applicationId") long applicationId)  {

        applicationFundingDecisionService.saveApplicationFundingDecisionData(competitionId, FundingDecision.FUNDED, singletonList(applicationId)).getSuccess();
        projectService.createProjectFromApplicationId(applicationId).getSuccess();

        return "redirect:/competition/{competitionId}/applications/unsuccessful";
    }
}
