package org.innovateuk.ifs.dashboard.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * This controller will handle third party redirection request comes to Applicant dashboard.
 */
@Controller
@RequestMapping("/applicant/dashboard")
@SecuredBySpring(value = "Controller", description = "Each applicant has permission to view their own dashboard",
        securedType = ThirdPartyRedirectedApplicantDashboardController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ThirdPartyRedirectedApplicantDashboardController extends ApplicantDashboardController {

    private static final Log LOG = LogFactory.getLog(ThirdPartyRedirectedApplicantDashboardController.class);

    @Autowired
    private PageHistoryService pageHistoryService;

    @Value("${ifs.loan.partb.enabled}")
    private boolean isLoanPartBEnabled;

    @SecuredBySpring(value = "LOANS_COMMUNITY_TO_APPLICATION_OVERVIEW", description = "Loans applicant will be redirected to application overview from SalesForce")
    @PreAuthorize("hasAuthority('applicant')")
    @GetMapping("/loansCommunity")
    public String loansToApplicationsOverviewPage(Model model,
                                                  UserResource user,
                                                  HttpServletRequest request) {

        if (isLoanPartBEnabled) {
            Optional<String> url = pageHistoryService.getPreviousPage(request)
                    .map(PageHistory::buildUrl);
            if (url.isPresent()) {
                return "redirect:" + url.get();
            }
            LOG.error("Application overview redirection failed due to URL issue" + url);
        }
        return dashboard(model, user);
    }
}
