package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.OPERATING;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;

/**
 * Provides methods for confirming and saving the organisation as an intermediate step in the registration flow.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@SecuredBySpring(value = "Controller",
        description = "Any user can confirm and save their organisation as part of registering their account",
        securedType = OrganisationCreationSaveController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationSaveController extends AbstractOrganisationCreationController {

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private QuestionRestService questionRestService;

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private InviteRestService inviteRestService;

    @GetMapping("/" + CONFIRM_ORGANISATION)
    public String confirmOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());

        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                   Model model,
                                   UserResource user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        BindingResult bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);

        //Ignore not null errors on organisationSearchName as its not relevant here. This is due to the same form being used.
        if (bindingResult.hasErrors() && (bindingResult.getAllErrors().size() != 1 || !bindingResult.hasFieldErrors("organisationSearchName"))) {
            return "redirect:/";
        }

        OrganisationSearchResult selectedOrganisation = addSelectedOrganisation(organisationForm, model);
        AddressResource address = organisationForm.getAddressForm().getSelectedPostcode();

        List<OrganisationAddressResource> organisationAddressResources = new ArrayList<>();

        if (address != null && !organisationForm.isUseSearchResultAddress()) {
            organisationAddressResources.add(
                    new OrganisationAddressResource(address,
                                                    new AddressTypeResource(OPERATING.getOrdinal(), OPERATING.name())));
        }
        if (selectedOrganisation != null && selectedOrganisation.getOrganisationAddress() != null) {
            organisationAddressResources.add(
                    new OrganisationAddressResource(selectedOrganisation.getOrganisationAddress(),
                                                    new AddressTypeResource(REGISTERED.getOrdinal(), REGISTERED.name())));
        }

        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationTypeId());
        organisationResource.setAddresses(organisationAddressResources);

        if (!OrganisationTypeEnum.RESEARCH.getId().equals(organisationForm.getOrganisationTypeId())) {
            organisationResource.setCompanyHouseNumber(organisationForm.getSearchOrganisationId());
        }

        organisationResource = createOrRetrieveOrganisation(organisationResource, request);
        if (user != null) {
            return handleExistingUser(user, registrationCookieService.getCompetitionIdCookieValue(request), organisationResource.getId(), response, request);
        } else {
            registrationCookieService.saveToOrganisationIdCookie(organisationResource.getId(), response);
            return "redirect:" + RegistrationController.BASE_URL;
        }
    }

    private OrganisationResource createOrRetrieveOrganisation(OrganisationResource organisationResource, HttpServletRequest request) {
        Optional<String> cookieHash = registrationCookieService.getInviteHashCookieValue(request);
        if(cookieHash.isPresent()) {
            return organisationService.createAndLinkByInvite(organisationResource, cookieHash.get());
        }

        return organisationService.createOrMatch(organisationResource);
    }

    private String handleExistingUser(UserResource user, Optional<Long> competitionId, long organisationId, HttpServletResponse response, HttpServletRequest request) {
        if (!user.hasRole(Role.APPLICANT)) {
            userRestService.grantRole(user.getId(), Role.APPLICANT).getSuccess();
            cookieUtil.saveToCookie(response, "role", Role.APPLICANT.getName());
        }

        Optional<String> inviteHash = registrationCookieService.getInviteHashCookieValue(request);

        ApplicationResource application = null;
        if (inviteHash.isPresent()) {
            application = acceptInvite(inviteHash.get(), response, request, user);
        } else if (competitionId.isPresent()) {
            application = applicationService.createApplication(competitionId.get(), user.getId(), organisationId, "");
        }

        if (application == null) {
            throw new ObjectNotFoundException("Could not create or find application",
                    Arrays.asList(String.valueOf(competitionId.orElse(null)), inviteHash.orElse(null), String.valueOf(user.getId())));
        }

        return redirectToApplication(application);
    }

    private ApplicationResource acceptInvite(String inviteHash, HttpServletResponse response, HttpServletRequest request, UserResource userResource) {
        ApplicationInviteResource invite = inviteRestService.getInviteByHash(inviteHash).getSuccess();
        inviteRestService.acceptInvite(inviteHash, userResource.getId()).getSuccess();
        registrationCookieService.deleteInviteHashCookie(response);
        return applicationService.getById(invite.getApplication());
    }

    private String redirectToApplication(ApplicationResource application) {
        return questionRestService
                .getQuestionByCompetitionIdAndQuestionSetupType(application.getCompetition(), APPLICATION_TEAM)
                .handleSuccessOrFailure(
                        failure -> format("redirect:/application/%s/team", application.getId()),
                        question -> format("redirect:/application/%s/form/question/%s", application.getId(),
                                question.getId())
                );
    }
}
