package org.innovateuk.ifs.application;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.registration.OrganisationCreationController;
import org.innovateuk.ifs.registration.RegistrationController;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.registration.AbstractAcceptInviteController.INVITE_HASH;

/**
 * This controller will handle all requests that are related to the create of a application.
 * This is used when the users want create a new application and that also includes the creation of the organisation.
 * These URLs are publicly available, since there user might not have a account yet.
 * <p>
 * The user input is stored in cookies, so we can use the data after a page refresh / redirect.
 * For user-account creation, have a look at {@link RegistrationController}
 */
@Controller
@RequestMapping("/application/create")
@PreAuthorize("permitAll")
public class ApplicationCreationController {
    public static final String COMPETITION_ID = "competitionId";
    public static final String USER_ID = "userId";
    private static final String APPLICATION_ID = "applicationId";
    private static final Log log = LogFactory.getLog(ApplicationCreationController.class);

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CookieUtil cookieUtil;

    @GetMapping("/check-eligibility/{competitionId}")
    public String checkEligibility(Model model,
                                   @PathVariable(COMPETITION_ID) Long competitionId,
                                   HttpServletResponse response) {
        PublicContentItemResource publicContentItem = competitionService
                .getPublicContentOfCompetition(competitionId);
        if (!isCompetitionReady(publicContentItem)) {
            return "redirect:/competition/search";
        }
        model.addAttribute(COMPETITION_ID, competitionId);
        cookieUtil.saveToCookie(response, COMPETITION_ID, String.valueOf(competitionId));
        cookieUtil.removeCookie(response, INVITE_HASH);
        cookieUtil.removeCookie(response, OrganisationCreationController.ORGANISATION_ID);

        return "create-application/check-eligibility";
    }

    private boolean isCompetitionReady(PublicContentItemResource publicContentItem) {
        if (publicContentItem.getNonIfs()) {
            return false;
        }
        return (publicContentItem.getCompetitionOpenDate().isBefore(ZonedDateTime.now()) &&
                publicContentItem.getCompetitionCloseDate().isAfter(ZonedDateTime.now()));
    }
}
