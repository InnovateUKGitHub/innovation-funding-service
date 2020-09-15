package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.controller.RegistrationController;
import org.innovateuk.ifs.invite.populator.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.invite.populator.ConfirmOrganisationInviteModelPopulator;
import org.innovateuk.ifs.invite.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.innovateuk.ifs.organisation.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.innovateuk.ifs.exception.ErrorControllerAdvice.URL_HASH_INVALID_TEMPLATE;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;


/**
 * This class is used as an entry point to accept an invite, to an application.
 */
@Controller
@SecuredBySpring(value = "Controller",
        description = "All users with a valid invite hash are able to view and accept the corresponding invite",
        securedType = AcceptInviteController.class)
@PreAuthorize("permitAll")
public class AcceptInviteController extends AbstractAcceptInviteController {

    private OrganisationRestService organisationRestService;
    private InviteRestService inviteRestService;
    private AcceptRejectApplicationInviteModelPopulator acceptRejectApplicationInviteModelPopulator;
    private ConfirmOrganisationInviteModelPopulator confirmOrganisationInviteModelPopulator;
    private CompetitionOrganisationConfigRestService organisationConfigRestService;

    private static final String ACCEPT_INVITE_NEW_USER_VIEW = "registration/accept-invite-new-user";
    private static final String ACCEPT_INVITE_EXISTING_USER_VIEW = "registration/accept-invite-existing-user";

    public AcceptInviteController(final OrganisationRestService organisationRestService,
                                  final InviteRestService inviteRestService,
                                  final AcceptRejectApplicationInviteModelPopulator acceptRejectApplicationInviteModelPopulator,
                                  final ConfirmOrganisationInviteModelPopulator confirmOrganisationInviteModelPopulator,
                                  final CompetitionOrganisationConfigRestService organisationConfigRestService) {
        this.organisationRestService = organisationRestService;
        this.inviteRestService = inviteRestService;
        this.acceptRejectApplicationInviteModelPopulator = acceptRejectApplicationInviteModelPopulator;
        this.confirmOrganisationInviteModelPopulator = confirmOrganisationInviteModelPopulator;
        this.organisationConfigRestService = organisationConfigRestService;
    }

    @GetMapping("/accept-invite/{hash}")
    public String inviteEntryPage(
            @PathVariable("hash") final String hash,
            UserResource loggedInUser,
            HttpServletResponse response,
            Model model) {

        registrationCookieService.deleteAllRegistrationJourneyCookies(response);

        RestResult<String> view = inviteRestService.getInviteByHash(hash).andOnSuccess(invite ->
                inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                            if (!SENT.equals(invite.getStatus())) {
                                return alreadyAcceptedView(response);
                            }
                            if (loggedInAsNonInviteUser(invite, loggedInUser)) {
                                return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
                            }
                            // Success
                            registrationCookieService.saveToInviteHashCookie(hash, response);// Add the hash to a cookie for later flow lookup.
                            AcceptRejectApplicationInviteViewModel acceptRejectApplicationInviteViewModel = acceptRejectApplicationInviteModelPopulator.populateModel(invite, inviteOrganisation);
                            model.addAttribute("model", acceptRejectApplicationInviteViewModel);
                            CompetitionOrganisationConfigResource organisationConfigResource = organisationConfigRestService.findByCompetitionId(acceptRejectApplicationInviteViewModel.getCompetitionId()).getSuccess();
                            boolean international = organisationConfigResource.areInternationalApplicantsAllowed();
                            model.addAttribute("internationalCompetition", international);
                            return invite.getUser() == null ? ACCEPT_INVITE_NEW_USER_VIEW : ACCEPT_INVITE_EXISTING_USER_VIEW;
                        }
                )
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getStatusCode().is4xxClientError() ? URL_HASH_INVALID_TEMPLATE : view.getSuccess();
    }

    @GetMapping("/accept-invite/confirm-invited-organisation")
    public String confirmInvitedOrganisation(HttpServletResponse response,
                                             HttpServletRequest request,
                                             UserResource loggedInUser,
                                             Model model) {
        String hash = registrationCookieService.getInviteHashCookieValue(request).orElse(null);
        RestResult<String> view = inviteRestService.getInviteByHash(hash).andOnSuccess(invite ->
                inviteRestService.getInviteOrganisationByHash(hash).andOnSuccessReturn(inviteOrganisation -> {
                    if (!SENT.equals(invite.getStatus())) {
                                return alreadyAcceptedView(response);
                            }
                            if (loggedInAsNonInviteUser(invite, loggedInUser)) {
                                return LOGGED_IN_WITH_ANOTHER_USER_VIEW;
                            }
                            OrganisationResource organisation =
                                    organisationRestService.getOrganisationByIdForAnonymousUserFlow(
                                    inviteOrganisation.getOrganisation()).getSuccess();
                            registrationCookieService.saveToOrganisationIdCookie(inviteOrganisation.getOrganisation()
                                    , response);
                    ConfirmOrganisationInviteOrganisationViewModel viewModel =
                            confirmOrganisationInviteModelPopulator.populate(invite, organisation,
                            RegistrationController.BASE_URL);
                    model.addAttribute("model", viewModel);
                            return "registration/confirm-invited-organisation";
                })
        ).andOnFailure(clearDownInviteFlowCookiesFn(response));
        return view.getSuccess();
    }
}
