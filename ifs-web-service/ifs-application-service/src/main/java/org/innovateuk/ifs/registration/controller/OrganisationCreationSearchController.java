package org.innovateuk.ifs.registration.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.innovateuk.ifs.util.ExceptionFunctions.getOrRethrow;
import static org.springframework.web.util.UriUtils.encodeQueryParam;

/**
 * Provides methods for both:
 * - Finding your company or research type organisation through Companies House or JES search.
 * - Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationCreationSearchController.class)
@PreAuthorize("permitAll")
public class OrganisationCreationSearchController extends AbstractOrganisationCreationController {

    private static final Log LOG = LogFactory.getLog(OrganisationCreationSearchController.class);

    private static final String SELECTED_ORGANISATION = "selected-organisation";

    private static final String SAVE_ORGANISATION_DETAILS = "save-organisation-details";
    private static final String REFERER = "referer";
    private static final String ORGANISATION_NAME = "organisationName";
    private static final String MODEL = "model";

    @Autowired
    private MessageSource messageSource;

    private static final String SEARCH_ORGANISATION = "search-organisation";

    @GetMapping(value = {"/" + FIND_ORGANISATION,"/" + FIND_ORGANISATION + "/**"})
    public String createOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        registrationCookieService.deleteOrganisationIdCookie(response);

        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        model.addAttribute(ORGANISATION_FORM, organisationForm);

        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("searchLabel",getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchLabel",  request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchHint",  request.getLocale()));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());

        return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
    }

    @PostMapping(value = "/" + FIND_ORGANISATION + "/**", params = SEARCH_ORGANISATION)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());

    }

    @PostMapping(value = {"/" + SELECTED_ORGANISATION + "/**", "/" + FIND_ORGANISATION + "/**"}, params = SAVE_ORGANISATION_DETAILS)
    public String saveOrganisation(@Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request, HttpServletResponse response,
                                   @RequestHeader(value = REFERER, required = false) final String referer) {
        OrganisationCreationForm organisationCreationForm = registrationCookieService.getOrganisationCreationCookieValue(request).get();
        organisationCreationForm.setOrganisationName(organisationForm.getOrganisationName());
        organisationCreationForm.setSearchOrganisationId(organisationForm.getSearchOrganisationId());
        organisationCreationForm.setTriedToSave(true);
        organisationCreationForm.setManualEntry(true);
        organisationForm.setTriedToSave(true);

        addOrganisationType(organisationCreationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationCreationForm, model);

        bindingResult = new BeanPropertyBindingResult(organisationCreationForm, ORGANISATION_FORM);
        validator.validate(organisationCreationForm, bindingResult);

        if (!bindingResult.hasFieldErrors(ORGANISATION_NAME)) {
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return "redirect:" + BASE_URL + "/" + CONFIRM_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            registrationCookieService.saveToOrganisationCreationCookie(organisationCreationForm, response);
            return getRedirectUrlInvalidSave(organisationForm, referer);
        }
    }

    private String getRedirectUrlInvalidSave(OrganisationCreationForm organisationForm, String referer) {
        String redirectPart;
        if (referer.contains(FIND_ORGANISATION)) {
            redirectPart = FIND_ORGANISATION;
            organisationForm.setSearchOrganisationId("");
        } else {
            redirectPart = SELECTED_ORGANISATION;
        }

        if (!referer.contains(FIND_ORGANISATION)) {
            if (isNotBlank(organisationForm.getSearchOrganisationId())) {
                return String.format("redirect:%s/%s/%s", BASE_URL, redirectPart, organisationForm.getSearchOrganisationId());
            } else {
                return String.format("redirect:%s/%s", BASE_URL, redirectPart);
            }
        } else {
            return String.format("redirect:%s/%s", BASE_URL, redirectPart);
        }
    }

    private String getMessageByOrganisationType(OrganisationTypeEnum orgTypeEnum, String textKey, Locale locale) {
        try {
            return messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.toString(), textKey), null, locale);
        } catch (NoSuchMessageException e) {
            LOG.error("unable to get message", e);
            return messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
    }

    private String escapePathVariable(final String input) {
        return getOrRethrow(() -> encodeQueryParam(input, "UTF-8"));
    }

    private boolean checkOrganisationIsLead(HttpServletRequest request) {
        return registrationCookieService.isLeadJourney(request);
    }
}
