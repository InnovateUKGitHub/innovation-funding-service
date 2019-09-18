package org.innovateuk.ifs.registration.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.registration.controller.RegistrationController;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

import static java.lang.String.format;

@Component
public class OrganisationJourneyEnd {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private RegistrationCookieService registrationCookieService;

    @Autowired
    private InviteRestService inviteRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private EncryptedCookieService cookieUtil;

    public String completeProcess(HttpServletRequest request, HttpServletResponse response, UserResource user, long organisationId) {
        if (user != null) {
            return handleExistingUser(request, response, user, organisationId);
        } else {
            registrationCookieService.saveToOrganisationIdCookie(organisationId, response);
            return "redirect:" + RegistrationController.BASE_URL;
        }
    }

    private String handleExistingUser(HttpServletRequest request, HttpServletResponse response, UserResource user, long organisationId) {
        if (!user.hasRole(Role.APPLICANT)) {
            userRestService.grantRole(user.getId(), Role.APPLICANT).getSuccess();
            cookieUtil.saveToCookie(response, "role", Role.APPLICANT.getName());
        }

        if (registrationCookieService.isCollaboratorJourney(request)) {
            return acceptInvite(request, response, user, organisationId);
        } else if (registrationCookieService.isLeadJourney(request)) {
            return createNewApplication(request, user, organisationId);
        } else {
            Optional<String> inviteHash = registrationCookieService.getInviteHashCookieValue(request);
            Optional<Long> competitionId = registrationCookieService.getCompetitionIdCookieValue(request);
            throw new ObjectNotFoundException("Could not create or find application",
                    Arrays.asList(String.valueOf(competitionId.orElse(null)), inviteHash.orElse(null), String.valueOf(user.getId())));
        }
    }

    private String createNewApplication(HttpServletRequest request, UserResource user, long organisationId) {
        ApplicationResource application = applicationService.createApplication(registrationCookieService.getCompetitionIdCookieValue(request).get(),
                user.getId(), organisationId, "");
        return redirectToApplicationOverview(application.getId());
    }

    private String acceptInvite(HttpServletRequest request, HttpServletResponse response, UserResource user, long organisationId) {
        String inviteHash = registrationCookieService.getInviteHashCookieValue(request).get();
        ApplicationInviteResource invite = inviteRestService.getInviteByHash(inviteHash).getSuccess();
        inviteRestService.acceptInvite(inviteHash, user.getId(), organisationId).getSuccess();
        registrationCookieService.deleteInviteHashCookie(response);
        return redirectToApplicationOverview(invite.getApplication());
    }

    private String redirectToApplicationOverview(long applicationId) {
        return format("redirect:/application/%s", applicationId);
    }
}