package org.innovateuk.ifs.dashboard.controller;

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
import javax.servlet.http.HttpServletResponse;
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
                            UserResource user,
                            HttpServletRequest request,
                            HttpServletResponse response) {

        if (isLoanPartBEnabled) {
            String referer = request.getHeader("referer");
            if (referer != null && referer.contains("loansCommunity")) {
                return redirectToApplicationOverviewPage(request, response, model, user);
            }
        }
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

    private String redirectToApplicationOverviewPage(HttpServletRequest request, HttpServletResponse response, Model model, UserResource user) {
        //using this existing API itself works ok now.
        Optional<String> url = pageHistoryService.getPreviousPage(request)
                .map(PageHistory::buildUrl);
        if (url.isPresent()) {
            return "redirect:" + url.get();
        }

        model.addAttribute("model", applicantDashboardPopulator.populate(user.getId()));
        return "applicant-dashboard";
    }
}
