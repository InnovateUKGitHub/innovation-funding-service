package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping(AbstractOrganisationCreationController.BASE_URL + "/" + AbstractOrganisationCreationController.LEAD_ORGANISATION_TYPE)
@PreAuthorize("permitAll")
public class OrganisationCreationLeadTypeController extends AbstractOrganisationCreationController{

    private static final String ORGANISATION_TYPE_ID = "organisationTypeId";

    @Autowired
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @GetMapping
    public String selectOrganisationType(Model model,
                                         HttpServletRequest request) {
        model.addAttribute("model", organisationCreationSelectTypePopulator.populate());

        Optional<OrganisationCreationForm> organisationCreationFormFromCookie = registrationCookieService.getOrganisationCreationCookieValue(request);
        if(organisationCreationFormFromCookie.isPresent()) {
            model.addAttribute(ORGANISATION_FORM, organisationCreationFormFromCookie.get());
        }
        else {
            model.addAttribute(ORGANISATION_FORM, new OrganisationCreationForm());
        }


        return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
    }

    @PostMapping
    public String confirmSelectOrganisationType(Model model,
                                                @Valid @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                                BindingResult bindingResult,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        OrganisationCreationSelectTypeViewModel selectOrgTypeViewModel = organisationCreationSelectTypePopulator.populate();
        Long organisationTypeId = organisationForm.getOrganisationTypeId();
        if (organisationTypeId != null &&
                !isValidLeadOrganisationType(organisationTypeId)) {
            bindingResult.addError(new FieldError(ORGANISATION_FORM, ORGANISATION_TYPE_ID, "Please select an organisation type."));
        }

        if (!bindingResult.hasFieldErrors(ORGANISATION_TYPE_ID)) {
            OrganisationTypeForm organisationTypeForm = registrationCookieService.getOrganisationTypeCookieValue(request).orElse(new OrganisationTypeForm());
            organisationTypeForm.setOrganisationType(organisationTypeId);
            registrationCookieService.saveToOrganisationTypeCookie(organisationTypeForm, response);
            saveOrgansationTypeToCreationForm(request, response, organisationTypeForm);

            return "redirect:" + BASE_URL + "/" + FIND_ORGANISATION;
        } else {
            organisationForm.setTriedToSave(true);
            model.addAttribute("model", selectOrgTypeViewModel);
            return TEMPLATE_PATH + "/" + LEAD_ORGANISATION_TYPE;
        }
    }

    private boolean isValidLeadOrganisationType(Long organisationTypeId) {
        return OrganisationTypeEnum.getFromId(organisationTypeId) != null;
    }

    private void saveOrgansationTypeToCreationForm(HttpServletRequest request, HttpServletResponse response, OrganisationTypeForm organisationTypeForm) {
        OrganisationCreationForm newOrganisationCreationForm = new OrganisationCreationForm();
        newOrganisationCreationForm.setOrganisationTypeId(organisationTypeForm.getOrganisationType());
        registrationCookieService.saveToOrganisationCreationCookie(newOrganisationCreationForm, response);
    }
}
