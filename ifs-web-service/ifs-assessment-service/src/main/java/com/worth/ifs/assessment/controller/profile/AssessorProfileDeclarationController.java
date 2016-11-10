package com.worth.ifs.assessment.controller.profile;

import com.worth.ifs.assessment.form.profile.AssessorProfileAppointmentForm;
import com.worth.ifs.assessment.form.profile.AssessorProfileDeclarationForm;
import com.worth.ifs.assessment.form.profile.AssessorProfileFamilyAffiliationForm;
import com.worth.ifs.assessment.model.profile.AssessorProfileDeclarationModelPopulator;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.controller.ValidationHandler;
import com.worth.ifs.user.resource.AffiliationResource;
import com.worth.ifs.user.resource.AffiliationResourceBuilder;
import com.worth.ifs.user.resource.AffiliationType;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static com.worth.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static com.worth.ifs.user.resource.AffiliationType.*;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
    private Validator validator;

    private static final String FORM_ATTR_NAME = "form";

    @RequestMapping(method = RequestMethod.GET)
    public String getDeclaration(Model model,
                                 @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                 @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                 BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            populateFormWithExistingValues(form, loggedInUser);
        }
        return doViewDeclaration(model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submitDeclaration(Model model,
                                    @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                    @Valid @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                    BindingResult bindingResult,
                                    ValidationHandler validationHandler) {

        Supplier<String> failureView = () -> getDeclaration(model, loggedInUser, form, bindingResult);

        validateLists(form, bindingResult);

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> updateResult = userService.updateUserAffiliations(loggedInUser.getId(), populateAffiliatonsFromForm(form));
            return validationHandler.addAnyErrors(updateResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, () -> "redirect:/assessor/dashboard");
        });
    }

    @RequestMapping(params = {"addAppointment"}, method = RequestMethod.POST)
    public String addAppointment(Model model,
                                 @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        form.getAppointments().add(new AssessorProfileAppointmentForm());
        return doViewDeclaration(model);
    }

    @RequestMapping(params = {"removeAppointment"}, method = RequestMethod.POST)
    public String removeAppointment(Model model,
                                    @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                    @RequestParam(name = "removeAppointment") Integer position) {
        form.getAppointments().remove(position.intValue());
        return doViewDeclaration(model);
    }

    @RequestMapping(params = {"addFamilyMemberAffiliation"}, method = RequestMethod.POST)
    public String addFamilyMemberAffiliation(Model model,
                                             @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form) {
        form.getFamilyAffiliations().add(new AssessorProfileFamilyAffiliationForm());
        return doViewDeclaration(model);
    }

    @RequestMapping(params = {"removeFamilyMemberAffiliation"}, method = RequestMethod.POST)
    public String removeFamilyMemberAffiliation(Model model,
                                                @ModelAttribute(FORM_ATTR_NAME) AssessorProfileDeclarationForm form,
                                                @RequestParam(name = "removeFamilyMemberAffiliation") Integer position) {
        form.getFamilyAffiliations().remove(position.intValue());
        return doViewDeclaration(model);
    }

    private String doViewDeclaration(Model model) {
        model.addAttribute("model", assessorProfileDeclarationModelPopulator.populateModel());
        return "profile/declaration-of-interest";
    }

    private void validateLists(AssessorProfileDeclarationForm form, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(form.getHasFamilyAffiliations())) {
            ValidationUtils.invokeValidator(validator, form, bindingResult, AssessorProfileFamilyAffiliationForm.FamilyAffiliations.class);
        }

        if (Boolean.TRUE.equals(form.getHasAppointments())) {
            ValidationUtils.invokeValidator(validator, form, bindingResult, AssessorProfileAppointmentForm.Appointments.class);
        }
    }

    private void populateFormWithExistingValues(AssessorProfileDeclarationForm form, UserResource user) {
        Map<AffiliationType, List<AffiliationResource>> affiliations = getAffiliationsMap(userService.getUserAffiliations(user.getId()));

        form.setPrincipalEmployer(getPrincipalEmployer(affiliations).map(AffiliationResource::getOrganisation).orElse(null));
        form.setRole(getPrincipalEmployer(affiliations).map(AffiliationResource::getPosition).orElse(null));
        form.setProfessionalAffiliations(getProfessionalAffiliations(affiliations));

        form.setHasAppointments(hasAppointments(affiliations));
        form.setAppointments(getAppointments(affiliations));

        form.setHasFinancialInterests(hasFinancialInterests(affiliations));
        form.setFinancialInterests(getFinancialInterests(affiliations));

        form.setHasFamilyAffiliations(hasFamilyAffiliations(affiliations));
        form.setFamilyAffiliations(getFamilyAffiliations(affiliations));

        form.setHasFamilyFinancialInterests(hasFamilyFinancialInterests(affiliations));
        form.setFamilyFinancialInterests(getFamilyFinancialInterests(affiliations));
    }

    private List<AffiliationResource> populateAffiliatonsFromForm(AssessorProfileDeclarationForm form) {
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

    private Optional<AffiliationResource> getPrincipalEmployer(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(EMPLOYER, affiliations);
    }

    private AffiliationResource getPrincipalEmployer(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder().setAffiliationType(EMPLOYER).setExists(TRUE).setOrganisation(form.getPrincipalEmployer()).setPosition(form.getRole()).createAffiliationResource();
    }

    private String getProfessionalAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(PROFESSIONAL, affiliations).map(AffiliationResource::getDescription).orElse(null);
    }

    private AffiliationResource getProfessionalAffiliations(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder().setAffiliationType(PROFESSIONAL).setExists(StringUtils.isNotBlank(form.getProfessionalAffiliations())).setDescription(form.getProfessionalAffiliations()).createAffiliationResource();
    }

    private Boolean hasAppointments(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(PERSONAL, affiliations);
    }

    private List<AssessorProfileAppointmentForm> getAppointments(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationsByType(PERSONAL, affiliations).map(affiliationResources -> affiliationResources.stream().filter(AffiliationResource::getExists).map(row -> new AssessorProfileAppointmentForm(row.getOrganisation(), row.getPosition())).collect(toList())).orElse(Collections.emptyList());
    }

    private List<AffiliationResource> getAppointments(AssessorProfileDeclarationForm form) {
        if (form.getHasAppointments()) {
            return form.getAppointments().stream().map(appointmentForm -> new AffiliationResourceBuilder()
                    .setAffiliationType(PERSONAL)
                    .setExists(TRUE)
                    .setOrganisation(appointmentForm.getOrganisation())
                    .setPosition(appointmentForm.getPosition())
                    .createAffiliationResource()
            ).collect(toList());
        } else {
            return singletonList(new AffiliationResourceBuilder().setAffiliationType(PERSONAL).setExists(FALSE).createAffiliationResource());
        }
    }

    private Boolean hasFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(PERSONAL_FINANCIAL, affiliations);
    }

    private String getFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(PERSONAL_FINANCIAL, affiliations).map(AffiliationResource::getDescription).orElse(null);
    }

    private AffiliationResource getFinancialInterests(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder().setAffiliationType(PERSONAL_FINANCIAL).setExists(form.getHasFinancialInterests()).setDescription(form.getHasFinancialInterests() ? form.getFinancialInterests() : null).createAffiliationResource();
    }

    private Boolean hasFamilyAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(FAMILY, affiliations);
    }

    private List<AssessorProfileFamilyAffiliationForm> getFamilyAffiliations(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationsByType(FAMILY, affiliations).map(affiliationResources -> affiliationResources.stream().filter(AffiliationResource::getExists).map(row -> new AssessorProfileFamilyAffiliationForm(row.getRelation(), row.getOrganisation(), row.getPosition())).collect(toList())).orElse(Collections.emptyList());
    }

    private List<AffiliationResource> getFamilyAffiliations(AssessorProfileDeclarationForm form) {
        if (form.getHasFamilyAffiliations()) {
            return form.getFamilyAffiliations().stream().map(familyAffiliationForm -> new AffiliationResourceBuilder()
                    .setAffiliationType(FAMILY)
                    .setExists(TRUE)
                    .setRelation(familyAffiliationForm.getRelation())
                    .setOrganisation(familyAffiliationForm.getOrganisation())
                    .setPosition(familyAffiliationForm.getPosition())
                    .createAffiliationResource()
            ).collect(toList());
        } else {
            return singletonList(new AffiliationResourceBuilder().setAffiliationType(FAMILY).setExists(FALSE).createAffiliationResource());
        }
    }

    private Boolean hasFamilyFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return hasAffiliationsByType(FAMILY_FINANCIAL, affiliations);
    }

    private String getFamilyFinancialInterests(Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return getAffiliationByType(FAMILY_FINANCIAL, affiliations).map(AffiliationResource::getDescription).orElse(null);
    }

    private AffiliationResource getFamilyFinancialInterests(AssessorProfileDeclarationForm form) {
        return new AffiliationResourceBuilder().setAffiliationType(FAMILY_FINANCIAL).setExists(form.getHasFamilyFinancialInterests()).setDescription(form.getHasFamilyFinancialInterests() ? form.getFamilyFinancialInterests() : null).createAffiliationResource();
    }

    private Map<AffiliationType, List<AffiliationResource>> getAffiliationsMap(List<AffiliationResource> affiliations) {
        if (affiliations == null) {
            return emptyMap();
        } else {
            return affiliations.stream().collect(groupingBy(AffiliationResource::getAffiliationType));
        }
    }

    private Optional<AffiliationResource> getAffiliationByType(AffiliationType affiliationType, Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType)).flatMap(affiliationsByType -> affiliationsByType.stream().findFirst());
    }

    private Optional<List<AffiliationResource>> getAffiliationsByType(AffiliationType affiliationType, Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType));
    }

    /**
     * Determine if the user specified an answer for having affiliations of the specified affiliationType. An affiliation question that has been answered as not existing is recorded as a single entry with the <code>exists</code> property as {@link Boolean#FALSE}.
     *
     * @param affiliationType
     * @param affiliations
     * @return
     */
    private Boolean hasAffiliationsByType(AffiliationType affiliationType, Map<AffiliationType, List<AffiliationResource>> affiliations) {
        return ofNullable(affiliations.get(affiliationType)).map(affiliationsByType -> !(affiliationsByType.size() == 1 && FALSE == affiliationsByType.get(0).getExists())).orElse(null);
    }
}