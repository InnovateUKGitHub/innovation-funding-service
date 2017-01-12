package org.innovateuk.ifs.assessment.controller.profile;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileAppointmentForm;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileDeclarationForm;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileFamilyAffiliationForm;
import org.innovateuk.ifs.assessment.form.profile.populator.AssessorProfileDeclarationFormPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileEditDeclarationModelPopulator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.user.resource.AffiliationType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * Controller to manage the Assessor Profile Declaration of Interest page
 */
@Controller
@RequestMapping("/profile/declaration")
public class AssessorProfileDeclarationController {

    @Autowired
    private UserService userService;

    @Autowired
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    @Autowired
    private AssessorProfileDeclarationFormPopulator assessorProfileDeclarationFormPopulator;

    @Autowired
    private AssessorProfileEditDeclarationModelPopulator assessorProfileEditDeclarationModelPopulator;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getDeclaration(Model model,
                                 @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel(loggedInUser));
        return "profile/declaration-of-interest";
    }

    @RequestMapping(path = "/edit", method = RequestMethod.GET)
    public String getEditDeclaration(Model model,
                                     @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                     @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                     BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            assessorProfileDeclarationFormPopulator.populateForm(form, loggedInUser);
        }
        return doEditDeclaration(model);
    }

    @RequestMapping(path = "/edit", method = RequestMethod.POST)
    public String submitDeclaration(Model model,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> getEditDeclaration(model, loggedInUser, form, bindingResult);

        validateLists(form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = userService.updateUserAffiliations(loggedInUser.getId(), populateAffiliationsFromForm(form));
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/profile/declaration");
        });
    }

    @RequestMapping(path = "/edit", params = {"addAppointment"}, method = RequestMethod.POST)
    public String addAppointment(Model model,
                                 @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        form.getAppointments().add(new AssessorProfileAppointmentForm());
        return doEditDeclaration(model);
    }

    @RequestMapping(path = "/edit", params = {"removeAppointment"}, method = RequestMethod.POST)
    public String removeAppointment(Model model,
                                    @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                    @RequestParam(name = "removeAppointment") Integer position) {
        form.getAppointments().remove(position.intValue());
        return doEditDeclaration(model);
    }

    @RequestMapping(path = "/edit", params = {"addFamilyMemberAffiliation"}, method = RequestMethod.POST)
    public String addFamilyMemberAffiliation(Model model,
                                             @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        form.getFamilyAffiliations().add(new AssessorProfileFamilyAffiliationForm());
        return doEditDeclaration(model);
    }

    @RequestMapping(path = "/edit", params = {"removeFamilyMemberAffiliation"}, method = RequestMethod.POST)
    public String removeFamilyMemberAffiliation(Model model,
                                                @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                                @RequestParam(name = "removeFamilyMemberAffiliation") Integer position) {
        form.getFamilyAffiliations().remove(position.intValue());
        return doEditDeclaration(model);
    }

    private String doEditDeclaration(Model model) {
        model.addAttribute("model", assessorProfileEditDeclarationModelPopulator.populateModel());
        return "profile/declaration-of-interest-edit";
    }

    private void validateLists(AssessorProfileDeclarationForm form, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(form.getHasFamilyAffiliations())) {
            ValidationUtils.invokeValidator(validator, form, bindingResult, AssessorProfileFamilyAffiliationForm.FamilyAffiliations.class);
        }

        if (Boolean.TRUE.equals(form.getHasAppointments())) {
            ValidationUtils.invokeValidator(validator, form, bindingResult, AssessorProfileAppointmentForm.Appointments.class);
        }
    }

    private List<AffiliationResource> populateAffiliationsFromForm(AssessorProfileDeclarationForm form) {
        return combineLists(
                combineLists(
                        getAppointments(form),
                        getFamilyAffiliations(form)
                ),
                getPrincipalEmployer(form),
                getProfessionalAffiliations(form),
                getFinancialInterests(form),
                getFamilyFinancialInterests(form)
        );
    }

    private AffiliationResource getPrincipalEmployer(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(EMPLOYER)
                .setExists(TRUE)
                .setOrganisation(form.getPrincipalEmployer())
                .setPosition(form.getRole())
                .createAffiliationResource();
    }

    private AffiliationResource getProfessionalAffiliations(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(PROFESSIONAL)
                .setExists(StringUtils.isNotBlank(form.getProfessionalAffiliations()))
                .setDescription(form.getProfessionalAffiliations())
                .createAffiliationResource();
    }

    private List<AffiliationResource> getAppointments(AssessorProfileDeclarationForm form) {
        if (form.getHasAppointments()) {
            return form.getAppointments().stream().map(appointmentForm -> new AffiliationResourceBuilder()
                    .setAffiliationType(PERSONAL)
                    .setExists(TRUE)
                    .setOrganisation(appointmentForm.getOrganisation())
                    .setPosition(appointmentForm.getPosition())
                    .createAffiliationResource()
            )
                    .collect(toList());
        } else {
            return singletonList(new AffiliationResourceBuilder()
                    .setAffiliationType(PERSONAL)
                    .setExists(FALSE)
                    .createAffiliationResource()
            );
        }
    }

    private AffiliationResource getFinancialInterests(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(PERSONAL_FINANCIAL)
                .setExists(form.getHasFinancialInterests())
                .setDescription(form.getHasFinancialInterests() ? form.getFinancialInterests() : null)
                .createAffiliationResource();
    }

    private List<AffiliationResource> getFamilyAffiliations(AssessorProfileDeclarationForm form) {
        if (form.getHasFamilyAffiliations()) {
            return form.getFamilyAffiliations().stream()
                    .map(familyAffiliationForm -> new AffiliationResourceBuilder()
                            .setAffiliationType(FAMILY)
                            .setExists(TRUE)
                            .setRelation(familyAffiliationForm.getRelation())
                            .setOrganisation(familyAffiliationForm.getOrganisation())
                            .setPosition(familyAffiliationForm.getPosition())
                            .createAffiliationResource()
                    )
                    .collect(toList());
        } else {
            return singletonList(new AffiliationResourceBuilder()
                    .setAffiliationType(FAMILY)
                    .setExists(FALSE)
                    .createAffiliationResource()
            );
        }
    }

    private AffiliationResource getFamilyFinancialInterests(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder()
                .setAffiliationType(FAMILY_FINANCIAL)
                .setExists(form.getHasFamilyFinancialInterests())
                .setDescription(form.getHasFamilyFinancialInterests() ? form.getFamilyFinancialInterests() : null)
                .createAffiliationResource();
    }
}
