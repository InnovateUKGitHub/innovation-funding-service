package org.innovateuk.ifs.organisation.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.service.AddressRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.AddressTypeResource;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.address.resource.OrganisationAddressType.REGISTERED;

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

    private static final Log LOG = LogFactory.getLog(OrganisationCreationSaveController.class);




    @GetMapping("/" + CONFIRM_ORGANISATION)
    public String confirmOrganisation(@ModelAttribute(name = ORGANISATION_FORM, binding = false) OrganisationCreationForm organisationForm,
                                 Model model,
                                 HttpServletRequest request,
                                 UserResource user) {
        organisationForm = getImprovedSearchFormDataFromCookie(organisationForm, model, request, DEFAULT_PAGE_NUMBER_VALUE, false);
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        addSelectedOrganisation(organisationForm, model);
        model.addAttribute(ORGANISATION_FORM, organisationForm);
        model.addAttribute("isApplicantJourney", registrationCookieService.isApplicantJourney(request));
        model.addAttribute("isLeadApplicant", registrationCookieService.isLeadJourney(request));
        model.addAttribute("organisationType", organisationTypeRestService.findOne(organisationForm.getOrganisationTypeId()).getSuccess());
        model.addAttribute("includeInternationalQuestion", registrationCookieService.getOrganisationInternationalCookieValue(request).isPresent());
        model.addAttribute("improvedSearchEnabled", isNewOrganisationSearchEnabled);
        addPageSubtitleToModel(request, user, model);
        return TEMPLATE_PATH + "/" + CONFIRM_ORGANISATION;
    }

    @PostMapping("/save-organisation")
    public String saveOrganisation(@Valid @ModelAttribute(name = ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                   Model model,
                                   UserResource user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        addOrganisationType(organisationForm, organisationTypeIdFromCookie(request));
        organisationForm = getImprovedSearchFormDataFromCookie(organisationForm, model, request, DEFAULT_PAGE_NUMBER_VALUE, false);
        organisationForm.setOrganisationTypeId(registrationCookieService.getOrganisationCreationCookieValue(request).get().getOrganisationTypeId());
        BindingResult bindingResult = new BeanPropertyBindingResult(organisationForm, ORGANISATION_FORM);
        validator.validate(organisationForm, bindingResult);

        //Ignore not null errors on organisationSearchName as its not relevant here. This is due to the same form being used.
        if (bindingResult.hasErrors() &&  !bindingResult.hasFieldErrors("organisationSearchName") && !bindingResult.hasFieldErrors("addressForm.postcodeInput")) {
            return "redirect:/";
        }
        OrganisationResource organisationResource = getOrganisationResourceToPersist(organisationForm);
        organisationResource = organisationRestService.createOrMatch(organisationResource).getSuccess();
        return organisationJourneyEnd.completeProcess(request, response, user, organisationResource.getId());
    }

     private OrganisationResource getOrganisationResourceToPersist(OrganisationCreationForm organisationForm) {
        OrganisationResource organisationResource = new OrganisationResource();
        organisationResource.setName(organisationForm.getOrganisationName());
        organisationResource.setOrganisationType(organisationForm.getOrganisationTypeId());

        if (isNewOrganisationSearchEnabled)  {
            organisationResource.setDateOfIncorporation(organisationForm.getDateOfIncorporation());
            AddressResource addressResource = organisationForm.getOrganisationAddress();

            AddressForm addressForm = organisationForm.getAddressForm();
            // Address form populated on address entry
            if(addressForm != null ) {
                addressResource = getAddressResourceFromForm(addressForm);
                organisationResource.setBusinessType(organisationForm.getBusinessType());
                organisationResource.setOrganisationNumber(organisationForm.getOrganisationNumber());
            }
            OrganisationAddressResource orgAddressResource = new OrganisationAddressResource(organisationResource, addressResource, new AddressTypeResource(REGISTERED.getId(), REGISTERED.name()));
            organisationResource.setAddresses(asList(orgAddressResource));
            organisationResource.setSicCodes(organisationForm.getSicCodes());
            organisationResource.setExecutiveOfficers(organisationForm.getExecutiveOfficers());

            // Check if it is for updating the existing organisation
            if (organisationForm.getSelectedExistingOrganisationId() != null) {
                OrganisationResource existingOrganisationResource = organisationRestService.getOrganisationById(organisationForm.getSelectedExistingOrganisationId()).getSuccess();
                organisationResource.setId(existingOrganisationResource.getId());
            }
        }

        if (OrganisationTypeEnum.RESEARCH.getId() != organisationForm.getOrganisationTypeId()) {
            organisationResource.setCompaniesHouseNumber(organisationForm.getSearchOrganisationId());
        }
        return organisationResource;
    }

    @PostMapping(value= "organisation-type/" + MANUALLY_ENTER_ORGANISATION_DETAILS)
    public String addressFormAction(Model model,
                                    @ModelAttribute(ORGANISATION_FORM) OrganisationCreationForm organisationForm,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler,
                                    UserResource loggedInUser) {

        organisationForm.getAddressForm().validateAction(bindingResult);


        AddressForm addressForm = organisationForm.getAddressForm();
        addressForm.handleAction(this::searchPostcode);

        return "registration/organisation/" + MANUALLY_ENTER_ORGANISATION_DETAILS;
    }

}
