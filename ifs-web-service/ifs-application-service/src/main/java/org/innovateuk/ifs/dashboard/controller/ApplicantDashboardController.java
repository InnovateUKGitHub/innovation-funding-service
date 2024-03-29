package org.innovateuk.ifs.dashboard.controller;

import lombok.extern.log4j.Log4j2;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.dashboard.populator.ApplicantDashboardPopulator;
import org.innovateuk.ifs.navigation.NavigationRoot;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.lang.String.format;

/**
 * This controller will handle requests related to the current applicant's dashboard. So pages that are relative to
 * that user are implemented here, for example the my-applications page.
 */
@Controller
@RequestMapping("/applicant/dashboard")
@SecuredBySpring(value = "Controller", description = "Each applicant has permission to view their own dashboard",
        securedType = ApplicantDashboardController.class)
@PreAuthorize("hasAuthority('applicant')")
@Log4j2
public class ApplicantDashboardController {


    @Autowired
    private ApplicantDashboardPopulator applicantDashboardPopulator;

    @Autowired
    private ApplicationRestService applicationRestService;

    @Autowired
    private PageHistoryService pageHistoryService;

    @Value("${ifs.loan.partb.enabled}")
    private boolean isLoanPartBEnabled;

    @SecuredBySpring(value = "ApplicantDashboardController", description = "applicant and kta has permission to view their own dashboard")
    @PreAuthorize("hasAnyAuthority('applicant', 'knowledge_transfer_adviser')")
    @GetMapping
    @NavigationRoot
    public String dashboard(Model model,
                            UserResource user) {
        model.addAttribute("model", applicantDashboardPopulator.populate(user.getId()));
        return "applicant-dashboard";
    }

    @PostMapping(params = "hide-application")
    public String hideApplication(@RequestParam("hide-application") long applicationId,
                                  UserResource user) {
        applicationRestService.hideApplication(applicationId, user.getId());
        return format("redirect:/applicant/dashboard");
    }

    @PostMapping(params = "delete-application")
    public String deleteApplication(@RequestParam("delete-application") long applicationId) {
        applicationRestService.deleteApplication(applicationId);
        return format("redirect:/applicant/dashboard");
    }


    /**
     * @deprecated  As of release 1.1.189, replaced by {@link #applicationsOverviewPage(Model, UserResource, HttpServletRequest)} ()}
     * To be removed once existing consumer of this API (SF Loans community) is moved to generic endpoint applicationsOverviewPage()
     */

    @Deprecated(since ="1.1.189",forRemoval = true)
    @SecuredBySpring(value = "LOANS_COMMUNITY_TO_APPLICATION_OVERVIEW", description = "Loans applicant will be redirected to application overview from SalesForce")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/loansCommunity")
    public String loansToApplicationsOverviewPage(Model model,
                                                  UserResource user,
                                                  HttpServletRequest request) {

        if (isLoanPartBEnabled) {
            Optional<String> url = pageHistoryService.getApplicationOverviewPage(request)
                    .map(PageHistory::buildUrl);
            if (url.isPresent()) {
                return "redirect:" + url.get();
            }
            log.error("Application overview redirection failed due to URL issue" + url);
        }
        return dashboard(model, user);
    }

    @SecuredBySpring(value = "GENERIC_APPLICATION_OVERVIEW_PAGE", description = "external endpoint to redirect user to application overview page from external site")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/overview")
    public String applicationsOverviewPage(Model model,
                                           UserResource user,
                                           HttpServletRequest request) {

        log.info("Received redirection request from host:" + request.getRemoteHost());
        Optional<String> url = pageHistoryService.getApplicationOverviewPage(request)
                .map(PageHistory::buildUrl);
        return url.map(s -> "redirect:" + s).orElseGet(() -> dashboard(model, user));
    }
}
