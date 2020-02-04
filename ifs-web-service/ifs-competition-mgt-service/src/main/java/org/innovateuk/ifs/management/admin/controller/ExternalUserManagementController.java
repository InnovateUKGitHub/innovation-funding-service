package org.innovateuk.ifs.management.admin.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.admin.form.SearchExternalUsersForm;
import org.innovateuk.ifs.user.resource.UserOrganisationResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * This controller will handle all requests that are related to management of users by IFS Administrators.
 */
@Controller
@RequestMapping("/admin/external")
public class ExternalUserManagementController {

    private static final String FORM_ATTR_NAME = "form";
    private static final String SEARCH_PAGE_TEMPLATE = "admin/search-external-users";

    @Autowired
    private UserRestService userRestService;

    @Autowired
    private InviteUserRestService inviteUserRestService;

    /* These external pages are still used by CSS. */
    @SecuredBySpring(value = "FIND_EXTERNAL_USERS", description = "Only the support user or IFS Admin can access external user information")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @GetMapping(value = "/users")
    public String viewFindExternalUsers(@ModelAttribute(FORM_ATTR_NAME) SearchExternalUsersForm form, Model model) {
        model.addAttribute("tab", "users");
        return emptyPage(model);
    }

    @SecuredBySpring(value = "FIND_EXTERNAL_INVITES", description = "Only the support user or IFS Admin can access external user invites")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @GetMapping(value = "/invites")
    public String viewFindExternalInvites(@ModelAttribute(FORM_ATTR_NAME) SearchExternalUsersForm form, Model model) {
        model.addAttribute("tab", "invites");
        return emptyPage(model);
    }

    @SecuredBySpring(value = "FIND_EXTERNAL_USERS", description = "Only the support user or IFS Admin can access external user information")
    @PreAuthorize("hasAnyAuthority('support', 'ifs_administrator')")
    @PostMapping({"/users", "/invites"})
    public String findExternalUsers(@Valid @ModelAttribute(FORM_ATTR_NAME) SearchExternalUsersForm form,
                                    @SuppressWarnings("unused") BindingResult bindingResult, ValidationHandler validationHandler,
                                    Model model, HttpServletRequest request) {
        Map<String, String[]> requestParams = request.getParameterMap();
        if (requestParams.containsKey("pending")) {
            return findExternalInvites(form, validationHandler, model);
        } else {
            return findExternalUsers(form, validationHandler, model);
        }
    }

    private static String emptyPage(Model model){
        model.addAttribute("mode", "init");
        model.addAttribute("users", emptyList());
        return SEARCH_PAGE_TEMPLATE;
    }

    private String findExternalUsers(SearchExternalUsersForm form, ValidationHandler validationHandler, Model model) {
        Supplier<String> failureView = () -> viewFindExternalUsers(form, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<List<UserOrganisationResource>> users = userRestService.findExternalUsers(form.getSearchString(), form.getSearchCategory());
            return validationHandler.addAnyErrors(users, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                                model.addAttribute("mode", "search");
                                model.addAttribute("tab", "users");
                                model.addAttribute("users", users.getSuccess());
                                return SEARCH_PAGE_TEMPLATE;
                            }
                    );
        });
    }

    private String findExternalInvites(SearchExternalUsersForm form, ValidationHandler validationHandler, Model model) {
        Supplier<String> failureView = () -> viewFindExternalInvites(form, model);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            RestResult<List<ExternalInviteResource>> invites = inviteUserRestService.findExternalInvites(form.getSearchString().trim(), form.getSearchCategory());
            return validationHandler.addAnyErrors(invites, fieldErrorsToFieldErrors(), asGlobalErrors()).
                    failNowOrSucceedWith(failureView, () -> {
                                model.addAttribute("mode", "search");
                                model.addAttribute("tab", "invites");
                                model.addAttribute("invites", invites.getSuccess());
                                return SEARCH_PAGE_TEMPLATE;
                            }
                    );
        });
    }
}