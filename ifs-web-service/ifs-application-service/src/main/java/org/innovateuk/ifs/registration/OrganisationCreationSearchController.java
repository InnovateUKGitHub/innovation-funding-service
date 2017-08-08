package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.form.AddressForm;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * TODO: Add description
 */
@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.FIND_ORGANISATION)
@PreAuthorize("permitAll")
public class OrganisationCreationSearchController extends AbstractOrganisationCreationController {

    @Autowired
    private MessageSource messageSource;

    private static final String SEARCH_ORGANISATION = "search-organisation";
    private static final String NOT_IN_COMPANY_HOUSE = "not-in-company-house";

    @GetMapping
    public String createOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                     Model model,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        registrationCookieService.deleteOrganisationIdCookie(response);

        organisationForm.setOrganisationSearching(false);
        organisationForm = getFormDataFromCookie(organisationForm, model, request);

        AddressForm addressForm = organisationForm.getAddressForm();
        addAddressOptions(addressForm);
        addSelectedAddress(addressForm);


        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        model.addAttribute(ORGANISATION_FORM, organisationForm);

        model.addAttribute("isLeadApplicant", checkOrganisationIsLead(request));
        model.addAttribute("searchLabel",getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchLabel",  request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(organisationForm.getOrganisationTypeEnum(), "SearchHint",  request.getLocale()));

        return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
    }

    @PostMapping(params = SEARCH_ORGANISATION)
    public String searchOrganisation(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                     HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setOrganisationSearching(true);
        organisationForm.setManualEntry(false);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION + "?searchTerm=" + escapePathVariable(organisationForm.getOrganisationSearchName());

    }

    @PostMapping(params = NOT_IN_COMPANY_HOUSE)
    public String manualOrganisationEntry(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                          HttpServletRequest request, HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm.setOrganisationSearching(false);
        boolean currentManualEntryValue = organisationForm.isManualEntry();
        organisationForm.setManualEntry(!currentManualEntryValue);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    @PostMapping(params = MANUAL_ADDRESS)
    public String manualAddressWithCompanyHouse(@ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                HttpServletRequest request, HttpServletResponse response) {
        organisationForm.setAddressForm(new AddressForm());
        organisationForm.getAddressForm().setManualAddress(true);
        organisationForm.setOrganisationSearching(false);
        organisationForm.setManualEntry(true);
        registrationCookieService.saveToOrganisationCreationCookie(organisationForm, response);
        return "redirect:/organisation/create/" + FIND_ORGANISATION;
    }

    private String getMessageByOrganisationType(OrganisationTypeEnum orgTypeEnum, String textKey, Locale locale) {
        try {
            return messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.toString(), textKey), null, locale);
        } catch (NoSuchMessageException e) {
            return  messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
    }
}
