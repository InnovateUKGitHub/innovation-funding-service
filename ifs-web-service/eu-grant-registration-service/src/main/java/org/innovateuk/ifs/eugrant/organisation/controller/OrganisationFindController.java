package org.innovateuk.ifs.eugrant.organisation.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.eugrant.EuOrganisationType;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationForm;
import org.innovateuk.ifs.eugrant.organisation.form.OrganisationTypeForm;
import org.innovateuk.ifs.eugrant.organisation.saver.OrganisationSaver;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
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
import java.util.Locale;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Provides methods for both:
 * Finding your company or research type organisation through Companies House or JES search.
 * Verifying or amending the address attached to the organisation.
 */
@Controller
@RequestMapping(AbstractOrganisationController.BASE_URL)
@SecuredBySpring(value = "Controller", description = "TODO", securedType = OrganisationFindController.class)
@PreAuthorize("permitAll")
public class OrganisationFindController extends AbstractOrganisationController {

    private static final Log LOG = LogFactory.getLog(OrganisationFindController.class);

    @Autowired
    private OrganisationSearchRestService organisationSearchRestService;

    @Autowired
    private OrganisationSaver organisationSaver;

    @Autowired
    private MessageSource messageSource;

    @GetMapping(FIND_ORGANISATION)
    public String createOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationForm organisationForm,
                                     Model model,
                                     HttpServletRequest request) {
        return getOrganisationType(type -> {
            populateModel(model, type, organisationForm, request);
            return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
        });
    }

    @PostMapping(value = FIND_ORGANISATION, params = "organisationSearching")
    public String searchOrganisation(@Valid @ModelAttribute(name = ORGANISATION_FORM) OrganisationForm organisationForm,
                                     BindingResult bindingResult,
                                     Model model,
                                     HttpServletRequest request) {
        return getOrganisationType(type -> {
            populateModel(model, type, organisationForm, request);
            model.addAttribute("results", searchOrganisation(organisationForm, type));
            return TEMPLATE_PATH + "/" + FIND_ORGANISATION;
        });
    }

    @PostMapping(value = FIND_ORGANISATION)
    public String saveOrganisation(@Valid @ModelAttribute(name = ORGANISATION_FORM) OrganisationForm organisationForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpServletRequest request) {
        return getOrganisationType(type -> {
            organisationSaver.save(organisationForm, type);
            return "redirect:/organisation/view";
        });
    }

    private String getMessageByOrganisationType(EuOrganisationType orgTypeEnum, String textKey, Locale locale) {
        try {
            return messageSource.getMessage(String.format("registration.%s.%s", orgTypeEnum.name(), textKey), null, locale);
        } catch (NoSuchMessageException e) {
            LOG.error("unable to get message", e);
            return messageSource.getMessage(String.format("registration.DEFAULT.%s", textKey), null, locale);
        }
    }

    private String getOrganisationType(Function<EuOrganisationType, String> success) {
        return organisationCookieService.getOrganisationTypeCookieValue()
                .map(OrganisationTypeForm::getOrganisationType)
                .map(success)
                .orElse("redirect:/organisation/type");
    }

    private void populateModel(Model model, EuOrganisationType type, OrganisationForm organisationForm, HttpServletRequest request) {
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("searchLabel",getMessageByOrganisationType(type, "SearchLabel",  request.getLocale()));
        model.addAttribute("searchHint", getMessageByOrganisationType(type, "SearchHint",  request.getLocale()));
        model.addAttribute("type", type);
    }

    private List<OrganisationSearchResult> searchOrganisation(OrganisationForm form, EuOrganisationType type) {
        if (isNotBlank(form.getOrganisationSearchName())) {
            String trimmedSearchString = StringUtils.normalizeSpace(form.getOrganisationSearchName());
            return organisationSearchRestService.searchOrganisation(type, trimmedSearchString)
                    .getOptionalSuccessObject()
                    .orElse(emptyList());
        } else {
            return emptyList();
        }
    }
}
