package org.innovateuk.ifs.assessment.controller.profile;

import org.innovateuk.ifs.affiliation.service.AffiliationService;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileAppointmentForm;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileDeclarationForm;
import org.innovateuk.ifs.assessment.form.profile.AssessorProfileFamilyAffiliationForm;
import org.innovateuk.ifs.assessment.form.profile.populator.AssessorProfileDeclarationFormPopulator;
import org.innovateuk.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.AffiliationResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * Controller to manage the Assessor Profile Declaration of Interest page
 */
@Controller
@RequestMapping("/profile/declaration")
@PreAuthorize("hasAuthority('assessor')")
public class AssessorProfileDeclarationController {

    @Autowired
    private AffiliationService affiliationService;

    @Autowired
    private AssessorProfileDeclarationModelPopulator assessorProfileDeclarationModelPopulator;

    @Autowired
    private AssessorProfileDeclarationFormPopulator assessorProfileDeclarationFormPopulator;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    private static final String FORM_ATTR_NAME = "form";

    @GetMapping
    public String getDeclaration(Model model,
                                 @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel(loggedInUser));
        return "profile/declaration-of-interest";
    }

    @GetMapping(path = "/edit")
    public String getEditDeclaration(@ModelAttribute("loggedInUser") UserResource loggedInUser,
                                     @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                     BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            assessorProfileDeclarationFormPopulator.populateForm(form, loggedInUser);
        }
        return doEditDeclaration();
    }

    @PostMapping(path = "/edit")
    public String submitDeclaration(@ModelAttribute("loggedInUser") UserResource loggedInUser,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> getEditDeclaration(loggedInUser, form, bindingResult);

        validateLists(form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = affiliationService.updateUserAffiliations(loggedInUser.getId(), populateAffiliationsFromForm(form));
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/profile/declaration");
        });
    }

    @PostMapping(path = "/edit", params = {"addAppointment"})
    public String addAppointment(@ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        form.getAppointments().add(new AssessorProfileAppointmentForm());
        return doEditDeclaration();
    }

    @PostMapping(path = "/edit", params = {"removeAppointment"})
    public String removeAppointment(@ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                    @RequestParam(name = "removeAppointment") Integer position) {
        form.getAppointments().remove(position.intValue());
        return doEditDeclaration();
    }

    @PostMapping(path = "/edit", params = {"addFamilyMemberAffiliation"})
    public String addFamilyMemberAffiliation(@ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        form.getFamilyAffiliations().add(new AssessorProfileFamilyAffiliationForm());
        return doEditDeclaration();
    }

    @PostMapping(path = "/edit", params = {"removeFamilyMemberAffiliation"})
    public String removeFamilyMemberAffiliation(@ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                                @RequestParam(name = "removeFamilyMemberAffiliation") Integer position) {
        form.getFamilyAffiliations().remove(position.intValue());
        return doEditDeclaration();
    }

    private String doEditDeclaration() {
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
        return AffiliationResourceBuilder.createPrincipalEmployer(form.getPrincipalEmployer(), form.getRole());
    }

    private AffiliationResource getProfessionalAffiliations(AssessorProfileDeclarationForm form) {
        return AffiliationResourceBuilder.createProfessaionAffiliations(form.getProfessionalAffiliations());
    }

    private List<AffiliationResource> getAppointments(AssessorProfileDeclarationForm form) {
        if (form.getHasAppointments()) {

            return form.getAppointments().stream().map(appointmentForm ->
                    AffiliationResourceBuilder.createAppointment(appointmentForm.getOrganisation(), appointmentForm.getPosition())
            )
                    .collect(toList());
        } else {
            return singletonList(AffiliationResourceBuilder.createEmptyAppointments());
        }
    }

    private AffiliationResource getFinancialInterests(AssessorProfileDeclarationForm form) {
        return AffiliationResourceBuilder.createFinancialInterests(form.getHasFinancialInterests(), form.getFinancialInterests());
    }

    private List<AffiliationResource> getFamilyAffiliations(AssessorProfileDeclarationForm form) {
        if (form.getHasFamilyAffiliations()) {
            return form.getFamilyAffiliations().stream()
                    .map(familyAffiliationForm ->
                            AffiliationResourceBuilder.createFamilyAffiliation(
                                    familyAffiliationForm.getRelation(),
                                    familyAffiliationForm.getOrganisation(),
                                    familyAffiliationForm.getPosition()
                            )
                    )
                    .collect(toList());
        } else {
            return singletonList(AffiliationResourceBuilder.createEmptyFamilyAffiliations());
        }
    }

    private AffiliationResource getFamilyFinancialInterests(AssessorProfileDeclarationForm form) {
        return AffiliationResourceBuilder.createFamilyFinancialInterests(form.getHasFamilyFinancialInterests(), form.getFamilyFinancialInterests());
    }
}
